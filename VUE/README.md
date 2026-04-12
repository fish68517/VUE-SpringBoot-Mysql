# 旅行日记 - Frontend (Vue.js 3)

## Project Structure

```
VUE/
├── src/
│   ├── main.js (Application entry point)
│   ├── App.vue (Root component)
│   ├── router/
│   │   └── index.js (Vue Router configuration)
│   ├── stores/
│   │   ├── userStore.js (User authentication state)
│   │   ├── travelStore.js (Travel records state)
│   │   └── planStore.js (Travel plans state)
│   ├── services/
│   │   ├── api.js (Axios configuration and interceptors)
│   │   └── authService.js (Authentication API calls)
│   ├── views/
│   │   ├── LoginView.vue
│   │   ├── RegisterView.vue
│   │   ├── DashboardView.vue
│   │   ├── RecordListView.vue
│   │   ├── RecordDetailView.vue
│   │   ├── PlanListView.vue
│   │   ├── PlanDetailView.vue
│   │   ├── SocialFeedView.vue
│   │   └── ProfileView.vue
│   ├── components/ (Reusable Vue components)
│   └── assets/ (Images, styles, etc.)
├── index.html (HTML entry point)
├── package.json (Dependencies)
├── vite.config.js (Vite configuration)
├── vitest.config.js (Vitest configuration)
├── .eslintrc.cjs (ESLint configuration)
└── .gitignore

```

## Installation

```bash
npm install
```

## Development

```bash
npm run dev
```

The application will start on `http://localhost:5173`

## Build

```bash
npm run build
```

## Testing

```bash
npm run test
```

## Linting

```bash
npm run lint
```

## Configuration

### API Proxy

The Vite dev server is configured to proxy API requests to the backend:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

Update `vite.config.js` if your backend runs on a different port.

## Dependencies

- Vue 3
- Vue Router 4
- Pinia (State management)
- Axios (HTTP client)
- Element Plus (UI component library)
- Quill (Rich text editor)
- AMap (Amap API loader)
- Vitest (Testing framework)

## Features

- User authentication (login/register)
- Travel record management
- Travel plan management
- Map footprint visualization
- Social interactions (comments, likes)
- User profile management
- Responsive design
