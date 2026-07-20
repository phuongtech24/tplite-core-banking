# TPLite Core Banking Web

React + Redux frontend for the Spring Boot Core Banking backend.

## Run

```bash
npm install
Copy-Item .env.example .env
npm run dev
```

Default API base URL:

```text
http://localhost:8081/api/v1
```

## Structure

- `src/app`: Redux store.
- `src/routes`: route declarations and route guards.
- `src/services`: Axios client, token storage, API services.
- `src/layouts`: shell layout only.
- `src/components`: reusable UI components.
- `src/features/*`: feature pages and feature state.

## Backend requirements

Backend exposes versioned APIs under `/api/v1` and allows CORS from `http://localhost:5173`.
