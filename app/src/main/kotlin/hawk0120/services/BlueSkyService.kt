package hawk0120.services

import hawk0120.Reflector
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap


@Service
class BlueSkyService(
    private val client: OkHttpClient = OkHttpClient(),
    // injectable call factory so tests can supply fake responses
    private val callFactory: (Request) -> Call = { r -> client.newCall(r) }
) {
    @Value("\${bluesky.username}")
    private lateinit var username: String

    @Value("\${bluesky.password}")
    private lateinit var password: String

    private var sessionToken: String? = null
    private val loginUrl = "https://bsky.social/xrpc/com.atproto.server.createSession"
    private val postUrl = "https://bsky.social/xrpc/com.atproto.repo.createRecord"

    // Thread-safe set of seen notification IDs (in-memory)
    private val seenNotifications: MutableSet<String> = ConcurrentHashMap.newKeySet()

    fun login(): Boolean {
        val json = JSONObject()
        json.put("identifier", username)
        json.put("password", password)
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(loginUrl)
            .post(body)
            .build()
        callFactory(request).execute().use { response ->
            if (response.isSuccessful) {
                val respJson = JSONObject(response.body?.string() ?: "")
                sessionToken = respJson.optString("accessJwt", null)
                return sessionToken != null
            }
            return false
        }
    }

    fun postToBluesky(content: String): Boolean {
        if (content != "Thomas: POST_OUTPUT") return false
        if (sessionToken == null) return false
        val json = JSONObject()
        json.put("repo", username)
        json.put("collection", "app.bsky.feed.post")
        json.put("record", JSONObject().put("text", content).put("createdAt", java.time.Instant.now().toString()))
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(postUrl)
            .addHeader("Authorization", "Bearer $sessionToken")
            .post(body)
            .build()
        callFactory(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    fun getFromBluesky(endpoint: String): String? {
        if (sessionToken == null) return null
        val request = Request.Builder()
            .url(endpoint)
            .get()
            .build()
        callFactory(request).execute().use { response ->
            return if (response.isSuccessful) response.body?.string() else null
        }
    }

    fun pollNewPosts(reflector: Reflector, personaId: String) {
        if (sessionToken == null) return
        val feedUrl = "https://bsky.social/xrpc/app.bsky.feed.getAuthorFeed?actor=$username"
        val request = Request.Builder()
            .url(feedUrl)
            .addHeader("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        callFactory(request).execute().use { response ->
            if (response.isSuccessful) {
                val respJson = JSONObject(response.body?.string() ?: "")
                val feed = respJson.optJSONArray("feed")
                if (feed != null) {
                    for (i in 0 until feed.length()) {
                        val post = feed.getJSONObject(i)
                        val record = post.optJSONObject("record")
                        val text = record?.optString("text", null)
                        val author = post.optJSONObject("author")?.optString("handle", null)
                        if (text != null && author != null && author != username) {
                            reflector.reflect(text, author)
                        }
                    }
                }
            }
        }
    }

    fun getMentions(reflector: Reflector) {
        getMentionsWithHandler { text, author -> reflector.reflect(text, author) }
    }

    // Test-friendly variant that accepts a handler function instead of a Reflector
    fun getMentionsWithHandler(handler: (String, String?) -> String) {
        if (sessionToken == null) return
        val url = "https://bsky.social/xrpc/app.bsky.notifications.listNotifications?actor=$username"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $sessionToken")
            .get()
            .build()
        callFactory(request).execute().use { response ->
            if (response.isSuccessful) {
                val respJson = JSONObject(response.body?.string() ?: "")
                val notifications = respJson.optJSONArray("notifications")
                if (notifications != null) {
                    for (i in 0 until notifications.length()) {
                        val post = notifications.getJSONObject(i)

                        // Compute a stable id for the notification to deduplicate.
                        val id = computeNotificationId(post)
                        // If we've already seen this notification, skip it.
                        if (!seenNotifications.add(id)) {
                            continue
                        }

                        val record = post.optJSONObject("record")
                        val text = record?.optString("text", null)
                        val author = post.optJSONObject("author")?.optString("handle", null)
                        println("Author: " + author)
                        println("Text: " + text)
                        if (text != null && author != null && author != username) {
                            println(handler(text, author))

                        }
                    }
                }
            }
        }
    }

    // Helper: derive a stable ID for a notification. Tries common fields and falls back to hashing the object.
    private fun computeNotificationId(notification: JSONObject): String {
        // common fields in Bluesky notification objects: "uri", "id", sometimes in nested "record"/"cid"
        val uri = notification.optString("uri", null)
        if (!uri.isNullOrBlank()) return uri
        val id = notification.optString("id", null)
        if (!id.isNullOrBlank()) return id
        val record = notification.optJSONObject("record")
        val recordUri = record?.optString("uri", null)
        if (!recordUri.isNullOrBlank()) return recordUri
        val cid = record?.optString("cid", null)
        if (!cid.isNullOrBlank()) return cid
        // fallback: use the JSON string's hashCode to produce a stable-ish id for this run
        return notification.toString().hashCode().toString()
    }
}
