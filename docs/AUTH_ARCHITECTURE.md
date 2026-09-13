# NØTUNE Authentication Architecture

## 1. Overview
NØTUNE supports an offline-first identity system (`NotuneAccountRepository`) that allows users to use the application immediately upon installation without mandatory sign-in ("Offline Guest Mode"), while seamlessly connecting to cloud-backed identity providers when desired.

---

## 2. Supported Auth Providers
1. **Offline Guest Mode** (Default out-of-the-box mode; local UUID & guest profile).
2. **Google OAuth 2.0 / Credential Manager** (Server-side token verification).
3. **GitHub OAuth 2.0** (Authorization code flow).
4. **Phone OTP** (SMS verification).
5. **NØTUNE Account** (Email / Password credentials).

---

## 3. Session Security & Token Lifecycle
```
Client Device                         Backend Auth Service
     │                                         │
     ├────── 1. Authenticate Credential ──────►│
     │                                         │
     │◄───── 2. Access Token + Refresh Token ──┤
     │                                         │
 (Store in Android Keystore)                   │
     │                                         │
     ├────── 3. API Request w/ Bearer Token ──►│
     │                                         │
     ├────── 4. Refresh Token Rotation ───────►│
```

- **Tokens**: Access tokens expire in 15 minutes; Refresh tokens expire in 30 days.
- **Secure Storage**: Tokens stored exclusively in Android Keystore via `SecureStorageManager`.
- **Revocation**: Server tracks active sessions in `user_sessions` table; users can revoke remote sessions at any time.
