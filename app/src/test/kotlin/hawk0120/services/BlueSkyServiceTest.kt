package hawk0120.services

import okhttp3.Call
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Timeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlueSkyServiceTest {

    // Helper fake Call that returns a prepared Response
    private class FakeCall(private val response: Response) : Call {
        private var executed = false
        private var canceled = false

        override fun execute(): Response {
            executed = true
            return response
        }

        override fun enqueue(responseCallback: okhttp3.Callback) {
            executed = true
            responseCallback.onResponse(this, response)
        }

        override fun isExecuted(): Boolean = executed
        override fun cancel() { canceled = true }
        override fun isCanceled(): Boolean = canceled
        override fun request(): Request = response.request
        override fun timeout(): Timeout = Timeout()
        override fun clone(): Call = this
    }

    @Test
    fun `getMentionsWithHandler should deduplicate notifications with same id`() {
        // Prepare JSON with two notifications that share the same id
        val notif = """
            {
              "id": "n1",
              "record": { "text": "hello" },
              "author": { "handle": "other" }
            }
        """.trimIndent()
        val notificationsJson = "{\"notifications\": [ $notif, $notif ] }"

        // callFactory returns different responses depending on request URL
        val callFactory: (Request) -> Call = { req ->
            val url = req.url.toString()
            val body = when {
                url.contains("createSession") -> "{\"accessJwt\": \"token\"}"
                url.contains("listNotifications") -> notificationsJson
                else -> "{}"
            }
            val resp = Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
            FakeCall(resp)
        }

        val service = BlueSkyService(callFactory = callFactory)

        // set private username/password via reflection so URLs are built correctly
        val usernameField = BlueSkyService::class.java.getDeclaredField("username")
        usernameField.isAccessible = true
        usernameField.set(service, "testuser")
        val passwordField = BlueSkyService::class.java.getDeclaredField("password")
        passwordField.isAccessible = true
        passwordField.set(service, "pwd")

        // perform login to populate sessionToken
        val loggedIn = service.login()
        // login uses the fake callFactory which returns a token
        kotlin.test.assertTrue(loggedIn)

        val calls = mutableListOf<String>()
        service.getMentionsWithHandler { text, author ->
            calls.add("${author ?: "?"}:$text")
            "ok"
        }

        // Only one call should have been made because second notification is a duplicate
        assertEquals(1, calls.size)
        assertEquals("other:hello", calls[0])
    }

    @Test
    fun `getMentionsWithHandler should process distinct notifications`() {
        val notif1 = """
            {
              "id": "n1",
              "record": { "text": "hello1" },
              "author": { "handle": "alice" }
            }
        """.trimIndent()
        val notif2 = """
            {
              "id": "n2",
              "record": { "text": "hello2" },
              "author": { "handle": "bob" }
            }
        """.trimIndent()
        val notificationsJson = "{\"notifications\": [ $notif1, $notif2 ] }"

        val callFactory: (Request) -> Call = { req ->
            val url = req.url.toString()
            val body = when {
                url.contains("createSession") -> "{\"accessJwt\": \"token\"}"
                url.contains("listNotifications") -> notificationsJson
                else -> "{}"
            }
            val resp = Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
            FakeCall(resp)
        }

        val service = BlueSkyService(callFactory = callFactory)
        val usernameField = BlueSkyService::class.java.getDeclaredField("username")
        usernameField.isAccessible = true
        usernameField.set(service, "testuser")
        val passwordField = BlueSkyService::class.java.getDeclaredField("password")
        passwordField.isAccessible = true
        passwordField.set(service, "pwd")

        val loggedIn = service.login()
        kotlin.test.assertTrue(loggedIn)

        val calls = mutableListOf<String>()
        service.getMentionsWithHandler { text, author ->
            calls.add("${author ?: "?"}:$text")
            "ok"
        }

        assertEquals(2, calls.size)
        assertEquals("alice:hello1", calls[0])
        assertEquals("bob:hello2", calls[1])
    }
}

