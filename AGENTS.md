# Code Judge Project Guidelines

## Project Overview

This repository contains a Java code-judge backend and a Vue/Vite frontend.
The backend is the source of truth for domain behavior and persistence; the
frontend is a thin client for managing users, problems, contests, and
submissions.

## Repository Layout

- `src/main/java/`: main Java application.
  - `Main.java`: starts the Javalin API on port `7070`.
  - `AppModule.java`: Guice bindings for service interfaces.
  - `apiController/`: HTTP handlers and JSON request/response mapping.
  - `controller/`: older/application-facing handlers used outside the HTTP layer.
  - `service/`: domain operations, judging, submissions, queues, and plagiarism.
  - `service/codeRunner/`: Java/C++ compilation and execution implementations.
  - `repository/`: repository interfaces and contracts.
  - `repositoryDataBase/`: H2-backed repository implementations.
  - `model/`: domain entities and enums.
  - `dto/`: API request/response DTOs.
  - `exception/`: domain and data-access exceptions.
- `src/main/schema.sql`: H2 schema for users, problems, test cases, categories,
  contests, and submissions.
- `src/test/java/`: JUnit and Mockito tests for repositories, DTOs, and judging.
- `frontend/`: Vue 3 + Vite client. `src/App.vue` contains the current page,
  state, forms, CodeMirror editor, and submission polling.
- `frontend/src/api/codeJudgeApi.js`: centralized fetch helpers; the API base
  URL is currently `http://localhost:7070`.
- `databaseDemo/` and `test-javalin-api/`: separate demo/test Maven modules;
  do not assume they share the root application wiring.
- `json_folder/`: sample JSON data. `data/`, `target/`, and `frontend/dist/`
  contain local/generated artifacts and should not be edited as source.

## Backend Architecture

Use the existing flow for new functionality:

`Main` → `apiController` → `service` → `repositoryDataBase` → H2.

Keep HTTP parsing and response formatting in `apiController`; keep business
rules in services; keep SQL/JDBC and persistence details in database
repositories. Reuse the existing model, DTO, and exception types rather than
introducing parallel representations.

The public API currently includes:

- `GET/POST /api/users`, `GET /api/users/{id}`, `DELETE /api/users/{id}`
- `GET/POST /api/problems`, `GET /api/problems/{id}`,
  `DELETE /api/problems/{id}`
- `GET/POST /api/contests`, `GET /api/contests/{id}`,
  `DELETE /api/contests/{id}`, `GET /api/contests/{id}/add/{problemId}`
- `POST /api/submit`
- `GET /api/submissions/{id}`
- `GET /api/submissions/?userId={userId}`

The frontend expects the backend to allow CORS from its configured development
origin and to return JSON compatible with the DTOs. Preserve UUID and enum
handling when changing endpoint payloads.

## Frontend Architecture

The frontend is intentionally small and currently uses one composition-style
Vue component. Keep API calls in `frontend/src/api/codeJudgeApi.js`, update
reactive state in `App.vue`, and preserve the existing notice/error behavior.
CodeMirror is configured for Java and C++ and the selected language controls
the editor language extension.

When adding an API operation, add a named helper in the API module and use it
from the component instead of calling `fetch` directly in template logic.
Keep the API base URL and endpoint paths centralized. Follow the existing
double-quoted JavaScript style and Vue `<script setup>` pattern.

## Build and Test

Backend (from the repository root):

```bash
mvn test
mvn package
```

Frontend:

```bash
cd frontend
npm install
npm run build
```

Run the backend from the IDE or with the compiled Maven classpath so `Main`
starts Javalin on `localhost:7070`, then run `npm run dev` in `frontend/` for
interactive API testing. The frontend has no configured automated test script;
`npm run build` is the minimum validation for frontend changes.

## Change and Validation Rules

- Make focused changes and preserve existing endpoint behavior unless the task
  explicitly changes the contract.
- Add or update tests under `src/test/java/` for backend behavior changes.
- Do not commit H2 database files, compiled `.class` files, Maven `target/`
  output, or Vite `dist/` output.
- Be careful with code execution changes: runners compile and execute submitted
  Java/C++ source, so preserve timeout, process cleanup, and error-status
  handling.
- Check both sides of any API contract change: the corresponding Java DTO or
  controller and the frontend API helper/consumer.
- Treat existing working-tree modifications as user work; do not revert or
  overwrite them while making unrelated changes.
