import test from 'node:test';
import assert from 'node:assert/strict';
import {
  getValidatedJwtSecret,
  isValidUuid,
  signJwt,
  verifyJwt,
  createAuthMiddleware
} from '../auth.js';

const VALID_TEST_SECRET = 'a_cryptographically_secure_test_jwt_secret_with_32_characters_minimum';
const VALID_USER_ID = '11111111-2222-4333-8444-555555555555';
const VALID_USERNAME = 'test_listener';

// --------------------------------------------------------------------
// 1. Startup & Secret Validation Tests
// --------------------------------------------------------------------

test('getValidatedJwtSecret rejects missing or empty secret', () => {
  const orig = process.env.JWT_SECRET;
  try {
    delete process.env.JWT_SECRET;
    assert.throws(() => getValidatedJwtSecret(), /missing or empty/);
    process.env.JWT_SECRET = '   ';
    assert.throws(() => getValidatedJwtSecret(), /missing or empty/);
  } finally {
    process.env.JWT_SECRET = orig;
  }
});

test('getValidatedJwtSecret rejects secret shorter than 32 characters', () => {
  const orig = process.env.JWT_SECRET;
  try {
    process.env.JWT_SECRET = 'short_key_under_32_chars';
    assert.throws(() => getValidatedJwtSecret(), /too short/);
  } finally {
    process.env.JWT_SECRET = orig;
  }
});

test('getValidatedJwtSecret rejects known predictable placeholder secrets', () => {
  const orig = process.env.JWT_SECRET;
  const placeholders = [
    'notune_production_jwt_secret_key_2026',
    'notune_docker_jwt_production_secret_2026',
    'secret',
    'changeme',
    'your_jwt_secret_here',
    'your_jwt_signing_secret_min_32_chars',
    'replace_with_openssl_generated_random_secret_at_least_32_bytes'
  ];
  try {
    for (const placeholder of placeholders) {
      process.env.JWT_SECRET = placeholder;
      assert.throws(() => getValidatedJwtSecret(), /insecure known or predictable/);
    }
  } finally {
    process.env.JWT_SECRET = orig;
  }
});

test('getValidatedJwtSecret accepts cryptographically secure secret (>= 32 chars)', () => {
  const orig = process.env.JWT_SECRET;
  try {
    process.env.JWT_SECRET = VALID_TEST_SECRET;
    assert.equal(getValidatedJwtSecret(), VALID_TEST_SECRET);
  } finally {
    process.env.JWT_SECRET = orig;
  }
});

// --------------------------------------------------------------------
// 2. UUID Claims Validation Tests
// --------------------------------------------------------------------

test('isValidUuid accepts valid UUIDs and rejects invalid patterns', () => {
  assert.equal(isValidUuid('11111111-2222-4333-8444-555555555555'), true);
  assert.equal(isValidUuid('39d9cfd0-f146-42df-80c5-4d5254ce7b42'), true);
  assert.equal(isValidUuid('not-a-uuid'), false);
  assert.equal(isValidUuid(''), false);
  assert.equal(isValidUuid(null), false);
  assert.equal(isValidUuid(12345), false);
  assert.equal(isValidUuid('00000000-0000-0000-0000-000000000000; DROP TABLE'), false);
});

// --------------------------------------------------------------------
// 3. JWT Signing & Verification Tests
// --------------------------------------------------------------------

test('signJwt and verifyJwt roundtrip successfully with valid claims', () => {
  const token = signJwt(
    { id: VALID_USER_ID, username: VALID_USERNAME },
    VALID_TEST_SECRET,
    { expiresInSeconds: 3600 }
  );
  assert.ok(typeof token === 'string' && token.split('.').length === 3);

  const claims = verifyJwt(token, VALID_TEST_SECRET);
  assert.equal(claims.id, VALID_USER_ID);
  assert.equal(claims.username, VALID_USERNAME);
  assert.ok(claims.exp > Math.floor(Date.now() / 1000));
});

test('verifyJwt detects forged signature signed with different key', () => {
  const wrongSecret = 'another_totally_different_key_with_at_least_32_characters_long';
  const token = signJwt(
    { id: VALID_USER_ID, username: VALID_USERNAME },
    wrongSecret
  );

  assert.throws(() => verifyJwt(token, VALID_TEST_SECRET), /INVALID_SIGNATURE/);
});

test('verifyJwt detects expired token', () => {
  const token = signJwt(
    { id: VALID_USER_ID, username: VALID_USERNAME },
    VALID_TEST_SECRET,
    { expiresInSeconds: -10 }
  );

  assert.throws(() => verifyJwt(token, VALID_TEST_SECRET), /TOKEN_EXPIRED/);
});

test('verifyJwt rejects token with algorithm other than HS256', () => {
  const token = signJwt(
    { id: VALID_USER_ID, username: VALID_USERNAME },
    VALID_TEST_SECRET,
    { header: { alg: 'none' } }
  );

  assert.throws(() => verifyJwt(token, VALID_TEST_SECRET), /UNSUPPORTED_ALGORITHM/);
});

test('verifyJwt rejects token missing valid UUID id claim', () => {
  const tokenWithBadId = signJwt(
    { id: 'not_a_valid_uuid_string', username: 'attacker' },
    VALID_TEST_SECRET
  );

  assert.throws(() => verifyJwt(tokenWithBadId, VALID_TEST_SECRET), /INVALID_TOKEN_CLAIMS/);
});

test('verifyJwt rejects malformed token strings', () => {
  assert.throws(() => verifyJwt('just.two.parts.extra', VALID_TEST_SECRET), /INVALID_TOKEN_FORMAT/);
  assert.throws(() => verifyJwt('not-a-token', VALID_TEST_SECRET), /INVALID_TOKEN_FORMAT/);
  assert.throws(() => verifyJwt(null, VALID_TEST_SECRET), /TOKEN_NOT_STRING/);
});

// --------------------------------------------------------------------
// 4. Express Authentication Middleware Tests
// --------------------------------------------------------------------

const { requireAuth, optionalAuth } = createAuthMiddleware(() => VALID_TEST_SECRET);

function createMockReqRes(headers = {}) {
  const req = { headers };
  let statusCode = 200;
  let jsonResponse = null;

  const res = {
    status(code) {
      statusCode = code;
      return this;
    },
    json(data) {
      jsonResponse = data;
      return this;
    }
  };

  return { req, res, getStatus: () => statusCode, getJson: () => jsonResponse };
}

test('requireAuth rejects missing Authorization header with 401 UNAUTHORIZED', () => {
  const { req, res, getStatus, getJson } = createMockReqRes({});
  let nextCalled = false;

  requireAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, false);
  assert.equal(getStatus(), 401);
  assert.equal(getJson().error, 'UNAUTHORIZED');
  assert.equal(req.user, undefined, 'Must NEVER assign guest_user on missing token');
});

test('requireAuth rejects invalid header format with 401 INVALID_TOKEN_FORMAT', () => {
  const { req, res, getStatus, getJson } = createMockReqRes({ authorization: 'Basic user:pass' });
  let nextCalled = false;

  requireAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, false);
  assert.equal(getStatus(), 401);
  assert.equal(getJson().error, 'INVALID_TOKEN_FORMAT');
});

test('requireAuth rejects expired token with 401 TOKEN_EXPIRED', () => {
  const expiredToken = signJwt({ id: VALID_USER_ID }, VALID_TEST_SECRET, { expiresInSeconds: -1 });
  const { req, res, getStatus, getJson } = createMockReqRes({ authorization: `Bearer ${expiredToken}` });
  let nextCalled = false;

  requireAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, false);
  assert.equal(getStatus(), 401);
  assert.equal(getJson().error, 'TOKEN_EXPIRED');
});

test('requireAuth rejects forged token with 401 INVALID_TOKEN', () => {
  const wrongSecret = 'another_totally_different_key_with_at_least_32_characters_long';
  const forgedToken = signJwt({ id: VALID_USER_ID }, wrongSecret);
  const { req, res, getStatus, getJson } = createMockReqRes({ authorization: `Bearer ${forgedToken}` });
  let nextCalled = false;

  requireAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, false);
  assert.equal(getStatus(), 401);
  assert.equal(getJson().error, 'INVALID_TOKEN');
});

test('requireAuth succeeds with valid token and sets verified req.user', () => {
  const validToken = signJwt({ id: VALID_USER_ID, username: VALID_USERNAME }, VALID_TEST_SECRET);
  const { req, res } = createMockReqRes({ authorization: `Bearer ${validToken}` });
  let nextCalled = false;

  requireAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, true);
  assert.deepEqual(req.user, { id: VALID_USER_ID, username: VALID_USERNAME });
});

test('optionalAuth allows unauthenticated access with req.user = null', () => {
  const { req, res } = createMockReqRes({});
  let nextCalled = false;

  optionalAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, true);
  assert.equal(req.user, null);
});

test('optionalAuth sets req.user when valid token is provided', () => {
  const validToken = signJwt({ id: VALID_USER_ID, username: VALID_USERNAME }, VALID_TEST_SECRET);
  const { req, res } = createMockReqRes({ authorization: `Bearer ${validToken}` });
  let nextCalled = false;

  optionalAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, true);
  assert.deepEqual(req.user, { id: VALID_USER_ID, username: VALID_USERNAME });
});

test('optionalAuth rejects expired or forged token with 401', () => {
  const wrongSecret = 'another_totally_different_key_with_at_least_32_characters_long';
  const forgedToken = signJwt({ id: VALID_USER_ID }, wrongSecret);
  const { req, res, getStatus, getJson } = createMockReqRes({ authorization: `Bearer ${forgedToken}` });
  let nextCalled = false;

  optionalAuth(req, res, () => { nextCalled = true; });

  assert.equal(nextCalled, false);
  assert.equal(getStatus(), 401);
  assert.equal(getJson().error, 'INVALID_TOKEN');
});
