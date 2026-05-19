# Contributing to SkillBridge

Thank you for your interest in contributing. This project is a portfolio-grade full-stack application; contributions that improve quality, documentation, or test coverage are welcome.

## Getting Started

1. Fork the repository and clone your fork
2. Copy `.env.example` to `.env` and set `GEMINI_API_KEY`
3. Copy `backend/src/main/resources/application.properties.example` to `application-local.properties` in the same folder (or set environment variables)
4. Start MySQL, then run the backend and frontend (see [README.md](README.md))

## Development Workflow

1. Create a branch from `main`: `git checkout -b feature/your-feature-name`
2. Make focused changes with clear commit messages
3. Ensure the backend compiles: `cd backend && mvn -q compile`
4. Ensure the frontend builds: `cd frontend && npm run build`
5. Open a pull request describing **what** changed and **why**

## Code Style

- **Java**: Follow existing package structure (`controller`, `service`, `repository`, `dto`, `entity`)
- **React**: Functional components, hooks, Tailwind utility classes consistent with existing pages
- Keep diffs minimal; avoid unrelated refactors in the same PR

## Pull Request Checklist

- [ ] No secrets or credentials in committed files
- [ ] README or API docs updated if behavior changed
- [ ] Builds pass locally (backend + frontend)

## Questions

Open a GitHub Discussion or issue for questions about architecture or feature proposals before large changes.
