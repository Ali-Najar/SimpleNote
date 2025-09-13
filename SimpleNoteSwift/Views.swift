import SwiftUI
Text(me.email).foregroundStyle(.secondary)
}
}


Section(header: Text("App Settings")) {
NavigationLink("Change Password", destination: ChangePasswordView())
Button(role: .destructive) {
AppState.shared.logout()
} label: { Text("Log Out") }
}


if let e = error { Text(e).foregroundStyle(.red) }
}
.navigationTitle("Settings")
.task {
loading = true; error = nil
do { me = try await APIClient.shared.userinfo() } catch { error = (error as? APIError)?.body ?? error.localizedDescription }
loading = false
}
}
}


struct ChangePasswordView: View {
@StateObject private var vm = ChangePasswordVM()
@Environment(\.dismiss) private var dismiss


var body: some View {
Form {
Section("Create a New Password") {
SecureField("Current password", text: $vm.current)
SecureField("New password", text: $vm.new1)
SecureField("Retype new password", text: $vm.new2)
}
if let e = vm.error { Text(e).foregroundStyle(.red) }
Button(vm.loading ? "Please wait…" : "Create Password") {
Task { await vm.submit(); if vm.success { dismiss() } }
}.disabled(vm.loading)
}
.navigationTitle("Change Password")
}
}