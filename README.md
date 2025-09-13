# SimpleNote — Android (Kotlin, Jetpack Compose)

A Kotlin Android app for your SimpleNote backend: onboarding → auth (register/login) → notes (search + pagination) → editor (create/edit/delete) → settings (profile, change password, logout). JWT auth with automatic **access token refresh**.

---

## Prerequisites
- Android Studio (Giraffe or newer)
- JDK 11
- Android SDK Platform 35 (target), minSdk 25
- Backend running locally or accessible over the network
  - Redoc & Postman import: `/api/schema/redoc/`

---

## Quick Start

1) **Run backend** (example with Docker):
```bash
docker compose up --build
# API docs: http://localhost:8000/api/schema/redoc/
```

2) **Set API base URL** for the Android emulator (maps host to `10.0.2.2`):
`app/build.gradle.kts`
```kotlin
android {
  defaultConfig {
    buildConfigField("String", "API_BASE_URL", ""http://10.0.2.2:8000/"")
  }
  buildFeatures {
    buildConfig = true
    compose = true
  }
}
```

> Physical device: use your machine’s LAN IP (e.g., `http://192.168.1.10:8000/`).  
> Genymotion: `http://10.0.3.2:8000/`.

3) **Allow HTTP during development** (if your backend is not HTTPS):
`AndroidManifest.xml` inside `<application ...>`:
```xml
<application
    android:usesCleartextTraffic="true"
    ... >
```

4) **Sync & Run**: Sync Gradle and run on an emulator or device.

---

## Features
- Onboarding with illustration
- Register / Login (stores tokens)
- **JWT**: OkHttp Interceptor adds `Authorization: Bearer <access>`; **Authenticator** refreshes access on 401
- Notes list with search (`/api/notes/filter`) and infinite scroll
- Create/Update/Delete notes
- Settings: profile (`/api/auth/userinfo/`), change password, logout
- Error messages surfaced from server (400/401/404/etc.)

---

## API Endpoints (expected)
- `POST /api/auth/register/` → `{ username, password, email, first_name?, last_name? }`
- `POST /api/auth/token/` → `{ username, password }` ⇒ `{ access, refresh }`
- `POST /api/auth/token/refresh/` → `{ refresh }` ⇒ `{ access }`
- `GET  /api/auth/userinfo/`
- `POST /api/auth/change-password/` → `{ old_password, new_password }`

Notes:
- `GET  /api/notes/?page=&page_size=`
- `GET  /api/notes/filter?title=&description=&page=&page_size=`
- `GET  /api/notes/{id}/`
- `POST /api/notes/` → `{ title, description }`
- `PUT  /api/notes/{id}/` → `{ title, description }`
- `DELETE /api/notes/{id}/`

---

## Project Layout (high level)
```
app/src/main/java/com/example/simplenote/
  data/local/           # TokenStore (DataStore Preferences)
  data/remote/          # ApiClient, AuthApi, NotesApi, TokenAuthenticator, DTOs
  data/repo/            # NotesRepository
  ui/auth/              # Login, Register, ChangePassword (+ ViewModels)
  ui/home/              # Home (+ ViewModel) with paging & search
  ui/note/              # Note editor (+ ViewModel)
  ui/settings/          # Settings (+ ViewModel)
  ui/AppNav.kt          # Navigation graph (onboarding/login/register/home/editor/settings/change-pw)
app/src/main/res/       # drawables, themes, strings
```

---

## Troubleshooting

**Emulator can’t reach backend**  
- Use `http://10.0.2.2:8000/` (emulator → host).  
- Ensure `android:usesCleartextTraffic="true"` for HTTP dev.  
- Verify Docker exposes port `8000`.  
- See OkHttp logs in Logcat (requests/responses).

**Delete returns 404**  
- Often wrong note id or missing auth. Check that access token is set and auto-refresh works.

**New note saved but Home doesn’t update**  
- Home refreshes on resume and after editor success. If needed, pull-to-refresh or re-open Home.

**Registration 400**  
- Backend validation (e.g., weak password or duplicate username/email). UI shows server message.

---

## License
MIT (or your preferred license).
