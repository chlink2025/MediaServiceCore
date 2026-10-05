package com.liskovsoft.youtubeapi.comments

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * NEWTUBE(comment-translate): what YouTube answered to the translate action
 * (perform_comment_action type 22). The translated text arrives in an entity update:
 * frameworkUpdates.entityBatchUpdate.mutations[].payload.commentEntityPayload.translatedContent.
 * The answer is walked loosely - the first commentEntityPayload that carries a translatedContent,
 * matching commentId preferred - because YouTube nests it differently per client. A refusal is
 * thrown as "ErrorResponse: <YouTube's words>", the form RetrofitHelper gives an HTTP error's
 * message, so a caller finds the reason one way.
 */
internal object CommentTranslateAnswers {
    private const val SUCCEEDED = "STATUS_SUCCEEDED"

    /**
     * The translated text; null when YouTube sent none (the text may already be in the target
     * language, which is not a failure).
     */
    fun translatedText(answer: JsonObject?, commentId: String? = null): String? {
        answer ?: throw IllegalStateException("No answer")
        checkRefusal(answer)
        val payloads = ArrayList<JsonObject>()
        collectCommentEntityPayloads(answer, payloads)
        val payload = payloads.firstOrNull { commentId != null && commentId == keyOf(it) }
            ?: payloads.firstOrNull()
        return payload?.get("translatedContent")?.let { contentOf(it) }
    }

    /** A failed actionResult throws with YouTube's reason; no result at all is not a refusal. */
    private fun checkRefusal(answer: JsonObject) {
        val result = findFirst(answer, "actionResult") as? JsonObject ?: return
        val status = result.get("status")?.takeIf { it.isJsonPrimitive }?.asString ?: return
        if (status != SUCCEEDED) {
            throw IllegalStateException("ErrorResponse: " + (feedback(result) ?: status))
        }
    }

    /** "Translated by Google" feedback, or YouTube's reason for refusing. */
    private fun feedback(result: JsonObject): String? {
        val runs = (result.get("feedbackText") as? JsonObject)?.get("runs") as? JsonArray ?: return null
        val text = runs.mapNotNull { (it as? JsonObject)?.get("text")?.takeIf { t -> t.isJsonPrimitive }?.asString }
            .joinToString("")
        return text.ifEmpty { null }
    }

    private fun keyOf(payload: JsonObject): String? =
        payload.get("key")?.takeIf { it.isJsonPrimitive }?.asString
            ?: payload.get("commentId")?.takeIf { it.isJsonPrimitive }?.asString

    private fun contentOf(content: JsonElement): String? = when {
        content.isJsonPrimitive -> content.asString
        content.isJsonObject -> {
            val obj = content.asJsonObject
            obj.get("content")?.takeIf { it.isJsonPrimitive }?.asString
                ?: obj.get("simpleText")?.takeIf { it.isJsonPrimitive }?.asString
                ?: runsText(obj)
        }
        else -> null
    }

    private fun runsText(text: JsonObject): String? {
        val runs = text.get("runs") as? JsonArray ?: return null
        val joined = runs.mapNotNull { (it as? JsonObject)?.get("text")?.takeIf { t -> t.isJsonPrimitive }?.asString }
            .joinToString("")
        return joined.ifEmpty { null }
    }

    private fun collectCommentEntityPayloads(node: JsonElement?, out: MutableList<JsonObject>) {
        when (node) {
            is JsonObject -> {
                (node.get("commentEntityPayload") as? JsonObject)?.let { out.add(it) }
                for ((_, value) in node.entrySet()) {
                    collectCommentEntityPayloads(value, out)
                }
            }
            is JsonArray -> for (value in node) {
                collectCommentEntityPayloads(value, out)
            }
            else -> {}
        }
    }

    private fun findFirst(node: JsonElement?, key: String): JsonElement? {
        when (node) {
            is JsonObject -> {
                node.get(key)?.let { return it }
                for ((_, value) in node.entrySet()) {
                    findFirst(value, key)?.let { return it }
                }
            }
            is JsonArray -> for (value in node) {
                findFirst(value, key)?.let { return it }
            }
            else -> {}
        }
        return null
    }
}
