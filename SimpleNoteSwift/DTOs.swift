import Foundation


struct TokenRequest: Codable { let username: String; let password: String }
struct TokenPair: Codable { let access: String; let refresh: String }
struct RefreshRequest: Codable { let refresh: String }
struct AccessOnly: Codable { let access: String }


struct ChangePasswordRequest: Codable { let old_password: String; let new_password: String }
struct Message: Codable { let detail: String? }


struct UserInfo: Codable, Identifiable {
let id: Int
let username: String
let email: String
let first_name: String?
let last_name: String?
}


struct Note: Codable, Identifiable, Hashable {
let id: Int
let title: String
let description: String
let created_at: String
let updated_at: String
let creator_name: String?
let creator_username: String?
}


struct NoteRequest: Codable { let title: String; let description: String }


struct PaginatedNoteList: Codable {
let count: Int
let next: String?
let previous: String?
let results: [Note]
}


// Generic error envelope (your backend sometimes returns { errors: [{ detail, attr, code }] })
struct ApiErrorItem: Codable { let attr: String?; let code: String?; let detail: String? }
struct ApiErrorResponse: Codable { let type: String?; let errors: [ApiErrorItem]? }