import SwiftUI


@main
struct SimpleNoteApp: App {
@StateObject var app = AppState.shared


var body: some Scene {
WindowGroup {
RootView()
.environmentObject(app)
}
}
}


struct RootView: View {
@EnvironmentObject var app: AppState


var body: some View {
if !app.seenOnboarding {
OnboardingView(
onLogin: { app.seenOnboarding = true },
onRegister: { app.seenOnboarding = true }
)
} else if app.isAuthenticated {
HomeView()
} else {
NavigationStack {
LoginView(onRegistered: { })
}
}
}
}