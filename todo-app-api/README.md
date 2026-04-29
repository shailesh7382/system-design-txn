# todo-app-api

Spring Boot backend API for `todo-app` to persist todos.

## What It Provides

- `GET /api/todos?filter=all|active|completed&search=keyword`
- `POST /api/todos`
- `PATCH /api/todos/{id}`
- `PATCH /api/todos/{id}/toggle`
- `DELETE /api/todos/{id}`
- `DELETE /api/todos/completed`
- `DELETE /api/todos`

Todos are stored in an H2 file database at `./data/todo-app-api`.

## Todo JSON Shape

```json
{
  "id": "uuid",
  "title": "Buy milk",
  "completed": false,
  "priority": "MEDIUM",
  "createdAt": "2026-04-29T15:00:00Z"
}
```

## Run

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app-api
./mvnw spring-boot:run
```

If Maven wrapper is not present in your environment, use:

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app-api
mvn spring-boot:run
```

## Test

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app-api
mvn test
```

## Quick curl

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Ship todo backend","priority":"HIGH"}'

curl "http://localhost:8080/api/todos?filter=all&search=ship"
```

