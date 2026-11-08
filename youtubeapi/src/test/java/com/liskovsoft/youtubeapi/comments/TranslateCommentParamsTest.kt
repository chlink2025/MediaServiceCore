package com.liskovsoft.youtubeapi.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

class TranslateCommentParamsTest {
    @Test
    fun currentClientLayout() {
        // The layout current clients send (and the one verified live on 2026-11-08):
        // {1: 22, 31: {2: commentId, 3: {1: {1: text}}, 4: targetLanguage, 5: 0}}.
        val fields = parse(decode(CommentsApiParams.translateCommentParams("hello", "en", "cid")))
        assertEquals(22L, fields.single { it.number == 1 }.varint)
        assertTrue(fields.none { it.number == 2 || it.number == 3 || it.number == 5 || it.number == 23 })

        val translate = parse(fields.single { it.number == 31 }.message!!)
        assertEquals("cid", translate.single { it.number == 2 }.text)
        assertEquals("en", translate.single { it.number == 4 }.text)
        assertEquals(0L, translate.single { it.number == 5 }.varint)

        val params = parse(translate.single { it.number == 3 }.message!!)
        val comment = parse(params.single { it.number == 1 }.message!!)
        assertEquals("hello", comment.single { it.number == 1 }.text)
    }

    @Test
    fun encodedAsBase64UrlWithoutPadding() {
        val params = CommentsApiParams.translateCommentParams("hi", "ja", "cid")
        assertFalse(params.contains('='))
        assertFalse(params.contains('+'))
        assertFalse(params.contains('/'))
        assertFalse(params.contains('%'))

        // The URL decoder accepts the unpadded form; the action is still one protobuf message.
        val fields = parse(decode(params))
        assertEquals(22L, fields.single { it.number == 1 }.varint)
        assertEquals("cid", parse(fields.single { it.number == 31 }.message!!).single { it.number == 2 }.text)
    }

    @Test
    fun emojisAreKept() {
        // The old YouTube.js layout refused emoji unless they were stripped. The current layout
        // accepts them, and keeping them preserves the emoji in the translated text.
        val fields = parse(decode(CommentsApiParams.translateCommentParams("nice \uD83D\uDE00 work \u2764", "en", "cid")))
        val translate = parse(fields.single { it.number == 31 }.message!!)
        val params = parse(translate.single { it.number == 3 }.message!!)
        val comment = parse(params.single { it.number == 1 }.message!!)
        assertEquals("nice \uD83D\uDE00 work \u2764", comment.single { it.number == 1 }.text)
    }

    private fun decode(params: String): ByteArray = Base64.getUrlDecoder().decode(params)

    private class Field(val number: Int, val varint: Long?, val text: String?, val message: ByteArray?)

    private fun parse(bytes: ByteArray): List<Field> {
        val fields = ArrayList<Field>()
        var i = 0
        while (i < bytes.size) {
            var tag = 0L
            var shift = 0
            while (true) {
                val b = bytes[i++].toInt() and 0xFF
                tag = tag or ((b and 0x7F).toLong() shl shift)
                if (b and 0x80 == 0) break
                shift += 7
            }
            val number = (tag ushr 3).toInt()
            when (tag and 7) {
                0L -> {
                    var value = 0L
                    shift = 0
                    while (true) {
                        val b = bytes[i++].toInt() and 0xFF
                        value = value or ((b and 0x7F).toLong() shl shift)
                        if (b and 0x80 == 0) break
                        shift += 7
                    }
                    fields.add(Field(number, value, null, null))
                }
                2L -> {
                    var length = 0
                    shift = 0
                    while (true) {
                        val b = bytes[i++].toInt() and 0xFF
                        length = length or ((b and 0x7F) shl shift)
                        if (b and 0x80 == 0) break
                        shift += 7
                    }
                    val slice = bytes.copyOfRange(i, i + length)
                    i += length
                    fields.add(Field(number, null, slice.toString(Charsets.UTF_8), slice))
                }
                else -> throw IllegalStateException("Unsupported wire type ${tag and 7}")
            }
        }
        return fields
    }
}
