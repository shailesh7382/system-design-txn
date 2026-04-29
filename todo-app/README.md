# Pulse Tasks (Expo Todo App)

A funky but professional todo app built with Expo + React Native TypeScript.

## Features

- Create todos with priorities (high, medium, low)
- Mark complete/incomplete
- Edit and delete todos
- Filter: all, active, completed
- Search todos by title
- Clear all completed todos
- Reset all todos in backend storage
- API-backed persistence with `todo-app-api`
- Works across iOS, Android, and Web from one codebase

## Tech Stack

- Expo SDK 54
- React Native + TypeScript
- Spring Boot API (`todo-app-api`) for persistence
- Vitest for utility tests

## Quick Start

Start backend:

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app-api
mvn spring-boot:run
```

Start app:

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app
npm install
npm run typecheck
npm test
npm run web
```

For native targets:

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app
npm run ios
npm run android
```

## API URL Override

Set `EXPO_PUBLIC_API_BASE_URL` when not using local defaults:

```bash
cd /Users/shailesh/codebase/system-design-txn/todo-app
EXPO_PUBLIC_API_BASE_URL="http://localhost:8080" npm run web
```

Default behavior:

- Web/iOS simulator: `http://localhost:8080`
- Android emulator: `http://10.0.2.2:8080`

## Scripts

- `npm run start` - Start Expo dev server
- `npm run web` - Run web app
- `npm run ios` - Run iOS simulator target
- `npm run android` - Run Android emulator target
- `npm run typecheck` - TypeScript validation
- `npm test` - Run unit tests
