package com.liskovsoft.youtubeapi.comments

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CommentTranslateAnswersTest {
    @Test
    fun readsTheTranslatedText() {
        assertEquals("Bonjour, ceci est un test",
            CommentTranslateAnswers.translatedText(fixture("translate_comment.json")))
    }

    @Test
    fun prefersTheNamedComment() {
        val answer = inline("""
            {"frameworkUpdates":{"entityBatchUpdate":{"mutations":[
              {"payload":{"commentEntityPayload":{"key":"other","translatedContent":{"content":"wrong"}}}},
              {"payload":{"commentEntityPayload":{"key":"mine","translatedContent":{"content":"right"}}}}
            ]}}}
        """)
        assertEquals("right", CommentTranslateAnswers.translatedText(answer, "mine"))
        assertEquals("wrong", CommentTranslateAnswers.translatedText(answer, "missing"))
    }

    @Test
    fun readsAPlainStringTranslation() {
        val answer = inline("""
            {"frameworkUpdates":{"entityBatchUpdate":{"mutations":[
              {"payload":{"commentEntityPayload":{"translatedContent":"solo texto"}}}
            ]}}}
        """)
        assertEquals("solo texto", CommentTranslateAnswers.translatedText(answer))
    }

    @Test
    fun findsTheTranslationInALaterMutation() {
        // The translation is not guaranteed to be the first mutation: keep walking.
        val answer = inline("""
            {"frameworkUpdates":{"entityBatchUpdate":{"mutations":[
              {"payload":{"commentEntityPayload":{"key":"mine"}}},
              {"payload":{"commentEntityPayload":{"key":"mine","translatedContent":{"content":"found"}}}}
            ]}}}
        """)
        assertEquals("found", CommentTranslateAnswers.translatedText(answer, "mine"))
    }

    @Test
    fun anEmptyTranslationIsNotOne() {
        val answer = inline("""
            {"frameworkUpdates":{"entityBatchUpdate":{"mutations":[
              {"payload":{"commentEntityPayload":{"translatedContent":{"content":"   "}}}}
            ]}}}
        """)
        assertNull(CommentTranslateAnswers.translatedText(answer))
    }

    @Test
    fun aRealTranslationWinsOverAFailedAction() {
        // The content is what the person asked for; the action's status is only read when there is
        // no translation at all.
        val answer = inline("""
            {"actionResult":{"status":"STATUS_FAILED","feedbackText":{"runs":[{"text":"nope"}]}},
             "frameworkUpdates":{"entityBatchUpdate":{"mutations":[
               {"payload":{"commentEntityPayload":{"translatedContent":{"content":"hello"}}}}
             ]}}}
        """)
        assertEquals("hello", CommentTranslateAnswers.translatedText(answer))
    }

    @Test
    fun noTranslationIsNotAFailure() {
        assertNull(CommentTranslateAnswers.translatedText(inline("""{"frameworkUpdates":{}}""")))
        assertNull(CommentTranslateAnswers.translatedText(inline("""{"actionResult":{"status":"STATUS_SUCCEEDED"}}""")))
    }

    @Test
    fun aRefusalCarriesYouTubesReason() {
        val refused = inline("""
            {"actionResult":{"status":"STATUS_FAILED","feedbackText":{"runs":[{"text":"Unable to translate."}]}}}
        """)
        val error = assertThrows(IllegalStateException::class.java) {
            CommentTranslateAnswers.translatedText(refused)
        }
        assertEquals("ErrorResponse: Unable to translate.", error.message)
    }

    @Test
    fun anAnswerWithNothing() {
        assertThrows(IllegalStateException::class.java) { CommentTranslateAnswers.translatedText(null) }
    }

    private fun inline(json: String): JsonObject = JsonParser.parseString(json).asJsonObject

    private fun fixture(name: String): JsonObject =
        javaClass.classLoader!!.getResourceAsStream("comments/$name")!!.reader().use {
            JsonParser.parseReader(it).asJsonObject
        }
}
