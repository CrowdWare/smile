package at.crowdware.nocode.at.crowdware.nocode.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

/*
fun callOpenRouter(apiKey: String, referer: String?, title: String?) {
    val client = OkHttpClient()

    val messages = JSONArray().put(
        JSONObject()
            .put("role", "user")
            .put("content", "What is the meaning of life?")
    )

    val jsonBody = JSONObject()
        .put("model", "openai/gpt-4o")
        .put("messages", messages)

    val mediaType = "application/json".toMediaTypeOrNull()
    val body = RequestBody.create(mediaType, jsonBody.toString())

    val requestBuilder = Request.Builder()
        .url("https://openrouter.ai/api/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")

    referer?.let { requestBuilder.addHeader("HTTP-Referer", it) }
    title?.let { requestBuilder.addHeader("X-Title", it) }

    val request = requestBuilder.post(body).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("❌ Request failed: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val result = response.body?.string()
            println("✅ Response: $result")
        }
    })
}
*/


class AiSession(
    private val model: String = "openai/gpt-4o"
) {
    private val client = OkHttpClient()
    private val apiKey = System.getenv("OPENROUTERKEY") ?: error("OPENROUTERKEY not set")
    private val messages = mutableListOf<JSONObject>()

    fun warmUp(systemContext: String) {
        messages.clear()
        messages.add(JSONObject().put("role", "system").put("content", systemContext))
    }

    fun ask(prompt: String): String {
        messages.add(JSONObject().put("role", "user").put("content", prompt))

        val body = JSONObject()
            .put("model", model)
            .put("messages", JSONArray(messages))

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), body.toString()))
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return "❌ Request failed: ${response.code}"
            }

            val result = JSONObject(response.body?.string() ?: "{}")
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            messages.add(JSONObject().put("role", "assistant").put("content", result))
            result
        } catch (e: Exception) {
            "❌ Exception: ${e.message}"
        }
    }
}

fun generateCombinedContext(sourceDir: File): String {
    val buffer = StringBuilder()

    // Alle SML Definitionen sammeln
    sourceDir.listFiles()?.filter { it.extension == "sml" }?.sortedBy { it.name }?.forEach { file ->
        buffer.appendLine("### ${file.nameWithoutExtension}")
        buffer.appendLine(file.readText())
        buffer.appendLine()
    }

    // KI-Instruktionen anhängen
    buffer.appendLine("### AI Instructions")
    buffer.appendLine(
        """
        Your task is to generate SML code based on user prompts.
        Always use defined elements and their properties.
        Only use elements in allowed roots.
        Return only the code in SML format unless requested otherwise.
        Images are stored in the images folder and can be referred with only using the filename without the folder.
        """.trimIndent()
    )

    return buffer.toString()
}
/*
fun generateSmlProject(prompt: String, docs: String, outputDir: File): Boolean {
    val session = AiSession()
    session.warmUp(docs)

    val fullPrompt = """
        $prompt
        
        Please generate a complete SML project including:
        - an App definition (with Theme if needed)
        - at least 2–3 pages like home, about, contact
        - use standard SML structure
        - split your answer clearly using markers like:
        
        === app.sml ===
        App { ... }

        === pages/home.sml ===
        Page { ... }

        === pages/about.sml ===
        Page { ... }
    """.trimIndent()

    val result = session.ask(fullPrompt)

    if (result.contains("===")) {
        val parts = result.split("=== ")
        for (part in parts) {
            val lines = part.lines()
            if (lines.isEmpty()) continue

            val filename = lines.first().trim()
            val content = lines.drop(1).joinToString("\n").trim()
            if (filename.isNotBlank() && content.isNotBlank()) {
                val outFile = File(outputDir, filename)
                outFile.parentFile?.mkdirs()
                outFile.writeText(content)
                println("✅ Created: ${outFile.relativeTo(File("."))}")
            }
        }
        return true
    } else {
        println("⚠️ AI response could not be parsed. Output:\n$result")
        return false
    }
}*/

fun generateSmlProject(prompt: String, docs: String, outputDir: File): Boolean {
    val session = AiSession()
    session.warmUp(docs)

    val fullPrompt = """
        $prompt

        Please generate a complete SML project, and format each file like this:

        === app.sml ===
        ```sml
        App { ... }
        ```

        === pages/home.sml ===
        ```sml
        Page { ... }
        ```
    """.trimIndent()

    val result = session.ask(fullPrompt)

    // Aufteilen nach === filename ===
    val fileSections = result.split(Regex("===\\s*(.*?)\\s*===")).drop(1) // drop(0) is before first filename

    if (fileSections.size % 2 != 0) {
        println("⚠️ Malformed response. Could not split into filename/content pairs.")
        return false
    }

    var successCount = 0

    for (i in fileSections.indices step 2) {
        val rawName = fileSections[i].trim()
        val filename = rawName
            .lineSequence()
            .firstOrNull { it.endsWith(".sml") }  // nimm erste Zeile mit .sml
            ?.trim()
            ?: "unknown.sml"
        val blockWithBackticks = fileSections[i + 1]

        // Extrahiere Inhalt zwischen ```sml ... ```
        val codeRegex = Regex("```sml\\s*([\\s\\S]*?)```")
        val match = codeRegex.find(blockWithBackticks)

        if (match != null) {
            val content = match.groupValues[1].trim()
            val outFile = File(outputDir, filename)
            outFile.parentFile?.mkdirs()
            outFile.writeText(content)
            println("✅ Created: ${outFile.relativeTo(File("."))}")
            successCount++
        } else {
            println("⚠️ No valid SML block found for file: $filename")
        }
    }

    return successCount > 0
}