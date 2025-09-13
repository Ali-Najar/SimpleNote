import Foundation
import Security


enum Keychain {
static func set(_ data: Data, for key: String) {
let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
kSecAttrAccount as String: key,
kSecValueData as String: data]
SecItemDelete(query as CFDictionary)
SecItemAdd(query as CFDictionary, nil)
}
static func get(_ key: String) -> Data? {
let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
kSecAttrAccount as String: key,
kSecReturnData as String: true,
kSecMatchLimit as String: kSecMatchLimitOne]
var result: AnyObject?
SecItemCopyMatching(query as CFDictionary, &result)
return result as? Data
}
static func remove(_ key: String) {
let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
kSecAttrAccount as String: key]
SecItemDelete(query as CFDictionary)
}
}