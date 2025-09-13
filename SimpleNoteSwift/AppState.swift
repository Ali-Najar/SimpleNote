import Foundation
import Combine


final class AppState: ObservableObject {
static let shared = AppState()


@Published var user: UserInfo? = nil
@Published var isAuthenticated: Bool = false
@Published var seenOnboarding: Bool = false


private var cancellables = Set<AnyCancellable>()


init() {
Task { @MainActor in
self.isAuthenticated = (await APIClient.shared.auth.accessToken) != nil
if self.isAuthenticated {
do { self.user = try await APIClient.shared.userinfo() } catch { self.user = nil }
}
}
}


func logout() {
Task { @MainActor in
await APIClient.shared.auth.clear()
self.user = nil
self.isAuthenticated = false
}
}
}