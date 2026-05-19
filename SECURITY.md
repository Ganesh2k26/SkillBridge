# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security issue, please **do not** open a public GitHub issue.

Instead, email the maintainer with:

- A description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

We will acknowledge receipt within 48 hours and work on a fix as soon as possible.

## Security Best Practices for Self-Hosting

- Never commit `.env`, `application-local.properties`, or real API keys
- Rotate `JWT_SECRET` and `GEMINI_API_KEY` if they were ever exposed
- Use strong MySQL passwords in production
- Disable `app.seed-data` in production after initial setup
- Run the backend behind HTTPS and restrict CORS origins in `AppConfig.java`
