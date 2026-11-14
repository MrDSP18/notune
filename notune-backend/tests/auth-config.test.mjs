import test from "node:test";
import assert from "node:assert/strict";
import { createRequire } from "node:module";

const require = createRequire(import.meta.url);
const { getJwtSecret } = require("../authConfig.js");

test("rejects a missing JWT secret", () => {
  assert.throws(() => getJwtSecret({}), /JWT_SECRET must be configured/);
});

test("rejects short and placeholder JWT secrets", () => {
  assert.throws(() => getJwtSecret({ JWT_SECRET: "short" }), /JWT_SECRET must be configured/);
  assert.throws(() => getJwtSecret({ JWT_SECRET: "your_jwt_signing_secret_min_32_chars" }), /JWT_SECRET must be configured/);
});

test("accepts a configured strong JWT secret", () => {
  const secret = "a-strong-random-signing-secret-with-more-than-32-characters";
  assert.equal(getJwtSecret({ JWT_SECRET: secret }), secret);
});
