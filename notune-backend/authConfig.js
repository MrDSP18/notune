"use strict";

function getJwtSecret(env = process.env) {
  const secret = env.JWT_SECRET;
  if (typeof secret !== "string" || secret.length < 32 || /^(your_|change.?me|replace.?me|default|secret)/i.test(secret)) {
    throw new Error("JWT_SECRET must be configured with a strong random value of at least 32 characters");
  }
  return secret;
}

module.exports = { getJwtSecret };
