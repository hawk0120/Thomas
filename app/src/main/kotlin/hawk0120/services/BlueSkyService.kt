package hawk0120.services

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class BlueSkyService(private val username: String, private val password: String) {
    private val client = OkHttpClient()
    private var sessionToken: String? = null
    private val loginUrl = "https://bsky.social/xrpc/com.atproto.server.createSession"
    private val postUrl = "https://bsky.social/xrpc/com.atproto.repo.createRecord"

    fun login(): Boolean {
        val json = JSONObject()
        json.put("identifier", username)
        json.put("password", password)
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(loginUrl)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val respJson = JSONObject(response.body?.string() ?: "")
                sessionToken = respJson.optString("accessJwt", null)
                return sessionToken != null
            }
            return false
        }
    }

    fun postToBluesky(content: String): Boolean {
        if (sessionToken == null) return false
        val json = JSONObject()
        json.put("repo", username)
        json.put("collection", "app.bsky.feed.post")
        json.put("record", JSONObject().put("text", content).put("createdAt", java.time.Instant.now().toString()))
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(postUrl)
            .addHeader("Authorization", "Bearer $sessionToken")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    fun getFromBluesky(endpoint: String): String? {
        if (sessionToken == null) return null
        val request = Request.Builder()
            .url(endpoint)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) response.body?.string() else null
        }
    }
}