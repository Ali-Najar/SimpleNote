import Foundation
req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
}


let (data, resp) = try await session.data(for: req)
guard let http = resp as? HTTPURLResponse else { throw APIError(status: -1, body: "No HTTP response") }


if http.statusCode == 401, authorized, await auth.refreshToken != nil {
// try refresh once
try await refreshAccessToken()
return try await request(method, path, body: body, authorized: authorized)
}
if (200..<300).contains(http.statusCode) {
// Handle empty bodies
if T.self == Empty.self { return Empty() as! T }
return try decoder.decode(T.self, from: data)
} else {
let bodyText = String(data: data, encoding: .utf8) ?? ""
// Try friendly error
if let friendly = try? decoder.decode(ApiErrorResponse.self, from: data), let first = friendly.errors?.first?.detail {
throw APIError(status: http.statusCode, body: first)
}
throw APIError(status: http.statusCode, body: bodyText)
}
}


private func refreshAccessToken() async throws {
guard let refresh = await auth.refreshToken else { throw APIError(status: 401, body: "Missing refresh token") }
let resp: AccessOnly = try await post("/api/auth/token/refresh/", body: RefreshRequest(refresh: refresh), authorized: false)
await auth.set(access: resp.access, refresh: nil)
}


// MARK: - HTTP helpers
private func get<T: Decodable>(_ path: String, authorized: Bool = true) async throws -> T { try await request("GET", path, authorized: authorized) }
private func post<T: Decodable>(_ path: String, body: Encodable, authorized: Bool = true) async throws -> T { try await request("POST", path, body: body, authorized: authorized) }
private func post<T: Decodable>(_ type: T.Type, _ path: String, body: Encodable, authorized: Bool = true) async throws -> T { try await request("POST", path, body: body, authorized: authorized) }
private func put<T: Decodable>(_ path: String, body: Encodable, authorized: Bool = true) async throws -> T { try await request("PUT", path, body: body, authorized: authorized) }
private func delete<T: Decodable>(_ path: String, authorized: Bool = true) async throws -> T { try await request("DELETE", path, authorized: authorized) }
private struct Empty: Decodable {}
}


/// Wrap Encodable to erase concrete type
private struct AnyEncodable: Encodable {
private let _encode: (Encoder) throws -> Void
init(_ encodable: Encodable) { _encode = encodable.encode }
func encode(to encoder: Encoder) throws { try _encode(encoder) }
}