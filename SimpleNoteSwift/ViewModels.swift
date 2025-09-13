import Foundation
let n: Note
if let id = id { n = try await APIClient.shared.updateNote(id: id, title: title, description: description) }
else { n = try await APIClient.shared.createNote(title: title, description: description) }
id = n.id; lastEdited = n.updated_at
} catch {
self.error = (error as? APIError)?.body ?? error.localizedDescription
loading = false
throw error
}
loading = false
}


func deleteNote() async throws {
guard let id = id else { return }
loading = true; error = nil
do {
try await APIClient.shared.deleteNote(id: id)
} catch {
self.error = (error as? APIError)?.body ?? error.localizedDescription
loading = false
throw error
}
loading = false
}
}


@MainActor
final class ChangePasswordVM: ObservableObject {
@Published var current = ""
@Published var new1 = ""
@Published var new2 = ""
@Published var loading = false
@Published var error: String?
@Published var success = false


func submit() async {
guard new1 == new2 else { error = "Passwords do not match"; return }
guard new1.count >= 8 else { error = "Password must be at least 8 characters"; return }
loading = true; error = nil
do {
try await APIClient.shared.changePassword(old: current, new: new1)
success = true
} catch {
self.error = (error as? APIError)?.body ?? error.localizedDescription
}
loading = false
}
}