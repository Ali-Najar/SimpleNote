# SimpleNote — Android (Kotlin) & iOS (SwiftUI)

A tiny cross-platform notes app that talks to your SimpleNote backend.  
Features: onboarding → auth (register/login) → notes list (search + pagination) → note editor (create/edit/delete) → settings (profile, change password, logout).  
Auth is JWT with automatic **access token refresh**.

---

## TL;DR

1) **Run the backend** (Docker ok):
```bash
# in the backend repo
docker compose up --build
# API docs:
# http://localhost:8000/api/schema/redoc/
```

2) **Android (emulator):** set `API_BASE_URL = "http://10.0.2.2:8000/"` and run.

3) **iOS (simulator):** set `API_BASE_URL = http://127.0.0.1:8000/` and run (enable ATS for HTTP dev).

4) Login or Register → create/edit/delete notes.

---

## Backend (expected API)

The app uses these endpoints (paths are relative to your base URL):

- `POST /api/auth/register/` → `{ username, password, email, first_name?, last_name? }`
- `POST /api/auth/token/` → `{ username, password }` ⇒ `{ access, refresh }`
- `POST /api/auth/token/refresh/` → `{ refresh }` ⇒ `{ access }`
- `GET  /api/auth/userinfo/` ⇒ `{ id, username, email, first_name?, last_name? }`
- `POST /api/auth/change-password/` → `{ old_password, new_password }`

Notes:
- `GET  /api/notes/?page=&page_size=`
- `GET  /api/notes/filter?title=&description=&page=&page_size=`
- `GET  /api/notes/{id}/`
- `POST /api/notes/` → `{ title, description }`
- `PUT  /api/notes/{id}/` → `{ title, description }`
- `DELETE /api/notes/{id}/`

Redoc (with **Postman import**):
```
/api/schema/redoc/
```

---

## Android app (Kotlin + Jetpack Compose)

### Requirements
- Android Studio (Giraffe or newer)
- JDK 11
- minSdk 25 / targetSdk 35

### Configure API base URL
`app/build.gradle.kts`:
```kotlin
android {
  defaultConfig {
    // For Android emulator → your host machine:
    buildConfigField("String", "API_BASE_URL", ""http://10.0.2.2:8000/"")
  }
  buildFeatures {
    buildConfig = true
    compose = true
  }
}
```

> Genymotion: `http://10.0.3.2:8000/`  
> Physical device: your computer’s LAN IP, e.g. `http://192.168.1.10:8000/`.

### Allow HTTP during development
`AndroidManifest.xml` inside `<application ...>`:
```xml
<application
    android:usesCleartextTraffic="true"
    ... >
```
(Prefer HTTPS in production.)

### Tech stack
- **UI:** Jetpack Compose (Material 3, Navigation Compose, Foundation grid)
- **Networking:** Retrofit + Kotlinx Serialization, OkHttp + logging
- **Auth:** Interceptor adds `Authorization: Bearer <access>`; **Authenticator** refreshes access token on 401
- **Storage:** DataStore Preferences for tokens
- **State:** ViewModel + StateFlow
- **Screens:** Onboarding, Login, Register, Home, NoteEditor, Settings, ChangePassword

### Run
- Open in Android Studio
- **Sync Gradle**, then **Run** on an emulator or device

---

## iOS app (SwiftUI)

A complete SwiftUI implementation is included.

### Requirements
- Xcode 15+
- iOS 16+ target

### Configure API base URL
`Config.swift`:
```swift
enum Config {
    static let API_BASE_URL = URL(string: "http://127.0.0.1:8000/")! // Simulator + backend on your Mac
}
```
> On device: use your Mac’s LAN IP, e.g. `http://192.168.1.10:8000/`.

### Allow HTTP during development (ATS)
**Info.plist**:
```xml
<key>NSAppTransportSecurity</key>
<dict>
  <key>NSAllowsArbitraryLoads</key>
  <true/>
</dict>
```
(Prefer HTTPS in production.)

### Tech stack
- **UI:** SwiftUI + NavigationStack
- **Networking:** URLSession, Codable
- **Auth:** Keychain stores access/refresh; auto-refresh on 401 and retry
- **State:** ObservableObject VMs; `AppState` tracks login/session
- **Screens:** Onboarding, Login, Register, Home (search + pagination), Editor, Settings, Change Password

### Run
- Open in Xcode → **Build & Run** on the simulator

---

## Screens

- **Onboarding** – centered illustration with “Create account” / “I already have an account”
- **Login / Register** – validation + server error messages
- **Home** – search (`/filter`), infinite scroll
- **Editor** – create, edit, delete note
- **Settings** – profile (`/userinfo/`), change password, logout
- **Change Password** – client checks + server messages

---

## Troubleshooting

**Android emulator can’t hit backend**
- Use `http://10.0.2.2:8000/`
- Allow cleartext traffic for dev
- Check Logcat (OkHttp logs enabled)

**iOS simulator can’t hit backend**
- Use `http://127.0.0.1:8000/`
- Add ATS exception for HTTP dev

**Delete returns 404**
- Usually wrong note ID or request without auth (expired access).  
  Access token should auto-refresh on 401; confirm refresh token exists.

**New note saved but Home doesn’t update**
- Android: Home refreshes on return (onResume) and when editor signals success.
- iOS: Home refreshes after dismissing the editor.

**Registration fails (400)**
- Weak password or duplicate username/email; UI surfaces server error details.

---

## Project structure (high level)

```
/android-app
  app/src/main/java/com/example/simplenote/
    data/local/           # TokenStore (DataStore)
    data/remote/          # ApiClient, interceptors, DTOs, retrofit APIs
    data/repo/            # NotesRepository
    ui/auth/              # Login/Register/ChangePassword
    ui/home/              # Home
    ui/note/              # Note editor
    ui/settings/          # Settings
    ui/AppNav.kt          # Navigation graph
  app/src/main/res/       # drawables, themes, etc.

/ios-app
  Config.swift
  APIClient.swift
  Keychain.swift
  DTOs.swift
  ViewModels.swift
  Views.swift
  App.swift
  Assets.xcassets/
```

---

## Development notes

- **Logging (Android):** OkHttp `HttpLoggingInterceptor` shows request/response bodies in debug.
- **Token refresh:**  
  - Android: `TokenAuthenticator` calls `/api/auth/token/refresh/` then retries.  
  - iOS: `APIClient` refreshes on 401 and retries the original request.
- **Pagination:** both apps use `page`/`page_size` and keep fetching until `next == null`.

---

## Roadmap / TODOs

- Offline-first (local DB + sync)  
- Unit/UI tests  
- Theming and design polish (match Figma exactly)  
- Persist “seen onboarding” across launches  
- CI (lint, build, tests)

---

## Contributing

PRs and issues welcome. Please include:
- clear description,
- repro steps / screen recordings,
- backend logs for API issues.

---

## License

MIT (or your preferred license). Add a `LICENSE` file in the repo root.

---

### Credits
- Backend by you (SimpleNote).  
- Design cues from your Figma (onboarding/login/register/home/editor/settings).
