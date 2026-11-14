/**
 * NØTUNE Cryptographic Authentication & Token Validation Gateway
 * Zero-external-dependency, constant-time HMAC-SHA256 JWT security implementation.
 */

const crypto = require('node:crypto');
const { getJwtSecret } = require('./authConfig');

const JWT_MIN_LENGTH = 32;
const FORBIDDEN_PLACEHOLDER_SECRETS = new Set([
  'notune_production_jwt_secret_key_2026',
  'notune_docker_jwt_production_secret_2026',
  'secret',
  'jwt_secret',
  'changeme',
  'your_jwt_secret_here',
  'your_jwt_signing_secret_min_32_chars',
  'replace_with_openssl_generated_random_secret_at_least_32_bytes'
]);

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function isValidUuid(id) {
  return typeof id === 'string' && UUID_REGEX.test(id);
}

function getValidatedJwtSecret(overrideSecret) {
  const secret = overrideSecret !== undefined ? overrideSecret : process.env.JWT_SECRET;
  const isProduction = process.env.NODE_ENV === 'production';
  const isStrict = isProduction || process.env.STRICT_AUTH === 'true';

  if (!secret || typeof secret !== 'string' || secret.trim().length === 0) {
    const errorMsg = 'FATAL: JWT_SECRET environment variable is missing or empty. A cryptographically secure secret (>= 32 chars) is required.';
    if (isStrict) {
      console.error(errorMsg);
      process.exit(1);
    }
    throw new Error(errorMsg);
  }

  const trimmed = secret.trim();

  if (FORBIDDEN_PLACEHOLDER_SECRETS.has(trimmed.toLowerCase())) {
    const errorMsg = 'FATAL: JWT_SECRET uses an insecure known or predictable default placeholder.';
    if (isStrict) {
      console.error(errorMsg);
      process.exit(1);
    }
    throw new Error(errorMsg);
  }

  if (trimmed.length < JWT_MIN_LENGTH) {
    const errorMsg = `FATAL: JWT_SECRET is too short (${trimmed.length} chars). Minimum ${JWT_MIN_LENGTH} characters (256 bits) required for cryptographic safety.`;
    if (isStrict) {
      console.error(errorMsg);
      process.exit(1);
    }
    throw new Error(errorMsg);
  }

  return trimmed;
}

function signJwt(payload, secret, options = {}) {
  const validatedSecret = secret || getValidatedJwtSecret();
  const header = { alg: 'HS256', typ: 'JWT', ...options.header };
  const encodedHeader = Buffer.from(JSON.stringify(header)).toString('base64url');

  const now = Math.floor(Date.now() / 1000);
  const expiresInSeconds = options.expiresInSeconds !== undefined ? options.expiresInSeconds : (30 * 24 * 3600);
  
  const fullPayload = {
    ...payload,
    iat: payload.iat !== undefined ? payload.iat : now,
    exp: payload.exp !== undefined ? payload.exp : (now + expiresInSeconds)
  };
  const encodedPayload = Buffer.from(JSON.stringify(fullPayload)).toString('base64url');

  const signatureInput = `${encodedHeader}.${encodedPayload}`;
  const signature = crypto.createHmac('sha256', validatedSecret).update(signatureInput).digest('base64url');
  return `${signatureInput}.${signature}`;
}

function verifyJwt(token, secret) {
  const validatedSecret = secret || getValidatedJwtSecret();
  if (typeof token !== 'string') {
    throw new Error('TOKEN_NOT_STRING');
  }

  const parts = token.trim().split('.');
  if (parts.length !== 3) {
    throw new Error('INVALID_TOKEN_FORMAT');
  }

  const [encodedHeader, encodedPayload, signature] = parts;

  // 1. Constant-time signature verification
  const expectedSigBuffer = crypto.createHmac('sha256', validatedSecret).update(`${encodedHeader}.${encodedPayload}`).digest();
  const actualSigBuffer = Buffer.from(signature, 'base64url');

  if (expectedSigBuffer.length !== actualSigBuffer.length || !crypto.timingSafeEqual(expectedSigBuffer, actualSigBuffer)) {
    throw new Error('INVALID_SIGNATURE');
  }

  // 2. Header parsing and algorithm validation
  let header;
  try {
    header = JSON.parse(Buffer.from(encodedHeader, 'base64url').toString('utf8'));
  } catch (_e) {
    throw new Error('MALFORMED_HEADER');
  }

  if (header.alg !== 'HS256' || (header.typ && header.typ !== 'JWT')) {
    throw new Error('UNSUPPORTED_ALGORITHM');
  }

  // 3. Payload parsing & claims verification
  let claims;
  try {
    claims = JSON.parse(Buffer.from(encodedPayload, 'base64url').toString('utf8'));
  } catch (_e) {
    throw new Error('MALFORMED_PAYLOAD');
  }

  const now = Math.floor(Date.now() / 1000);

  if (claims.exp !== undefined) {
    if (!Number.isSafeInteger(claims.exp) || claims.exp <= now) {
      throw new Error('TOKEN_EXPIRED');
    }
  }

  if (claims.nbf !== undefined) {
    if (!Number.isSafeInteger(claims.nbf) || claims.nbf > now) {
      throw new Error('TOKEN_NOT_ACTIVE');
    }
  }

  if (typeof claims.id !== 'string' || !isValidUuid(claims.id)) {
    throw new Error('INVALID_TOKEN_CLAIMS');
  }

  return claims;
}

function createAuthMiddleware(secretGetter) {
  function requireAuth(req, res, next) {
    const authHeader = req.headers['authorization'];
    if (!authHeader) {
      return res.status(401).json({
        error: 'UNAUTHORIZED',
        message: 'Authentication required. Authorization header missing.'
      });
    }

    const parts = authHeader.trim().split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer') {
      return res.status(401).json({
        error: 'INVALID_TOKEN_FORMAT',
        message: 'Invalid authorization format. Expected "Bearer <token>".'
      });
    }

    const token = parts[1];
    try {
      const secret = typeof secretGetter === 'function' ? secretGetter() : secretGetter;
      const claims = verifyJwt(token, secret);
      req.user = {
        id: claims.id,
        username: claims.username || claims.display_name || 'Listener'
      };
      next();
    } catch (err) {
      if (err.message === 'TOKEN_EXPIRED') {
        return res.status(401).json({
          error: 'TOKEN_EXPIRED',
          message: 'Session token has expired. Please re-authenticate.'
        });
      }
      if (err.message === 'INVALID_TOKEN_CLAIMS') {
        return res.status(401).json({
          error: 'INVALID_TOKEN_CLAIMS',
          message: 'Token identity claim (id) is invalid or missing UUID format.'
        });
      }
      return res.status(401).json({
        error: 'INVALID_TOKEN',
        message: 'Invalid or forged session token.'
      });
    }
  }

  function optionalAuth(req, res, next) {
    const authHeader = req.headers['authorization'];
    if (!authHeader) {
      req.user = null;
      return next();
    }

    const parts = authHeader.trim().split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer') {
      return res.status(401).json({
        error: 'INVALID_TOKEN_FORMAT',
        message: 'Invalid authorization format. Expected "Bearer <token>".'
      });
    }

    const token = parts[1];
    try {
      const secret = typeof secretGetter === 'function' ? secretGetter() : secretGetter;
      const claims = verifyJwt(token, secret);
      req.user = {
        id: claims.id,
        username: claims.username || claims.display_name || 'Listener'
      };
      next();
    } catch (err) {
      if (err.message === 'TOKEN_EXPIRED') {
        return res.status(401).json({
          error: 'TOKEN_EXPIRED',
          message: 'Session token has expired. Please re-authenticate.'
        });
      }
      return res.status(401).json({
        error: 'INVALID_TOKEN',
        message: 'Invalid or forged session token.'
      });
    }
  }

  return { requireAuth, optionalAuth };
}

module.exports = {
  getJwtSecret,
  getValidatedJwtSecret,
  isValidUuid,
  signJwt,
  verifyJwt,
  createAuthMiddleware
};
