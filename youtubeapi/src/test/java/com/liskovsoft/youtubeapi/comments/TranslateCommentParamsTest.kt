package com.liskovsoft.youtubeapi.comments

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLDecoder
import java.util.Base64

class TranslateCommentParamsTest {
    @Test
    fun youtubeJsLayout() {
        // The reference client (YouTube.js v18.1.0) sends blank ids plus unk_num=2 for a translate
        // without a comment id; this is the payload the capture comparison is against.
        val fields = parse(decode(CommentsApiParams.translateCommentParams("hello", "en")))
        assertEquals(22L, fields.single { it.number == 1 }.varint)
        assertEquals(2L, fields.single { it.number == 2 }.varint)
        assertEquals(" ", fields.single { it.number == 3 }.text)
        assertEquals(" ", fields.single { it.number == 5 }.text)
        assertEquals(" ", fields.single { it.number == 23 }.text)
        val translate = parse(fields.single { it.number == 31 }.message!!)
        assertEquals(" ", translate.single { it.number == 2 }.text)
        assertEquals("en", translate.single { it.number == 4 }.text)
        val params = parse(translate.single { it.number == 3 }.message!!)
        val comment = parse(params.single { it.number == 1 }.message!!)
        assertEquals("hello", comment.single { it.number == 1 }.text)
    }

    @Test
    fun aRealCommentIdReplacesTheUnkNumMarker() {
        val fields = parse(decode(CommentsApiParams.translateCommentParams("hi", "ja", "vid", "cid")))
        assertTrue(fields.none { it.number == 2 })
        assertEquals("cid", fields.single { it.number == 3 }.text)
        assertEquals("vid", fields.single { it.number == 5 }.text)
        val translate = parse(fields.single { it.number == 31 }.message!!)
        assertEquals("cid", translate.single { it.number == 2 }.text)
    }

    @Test
    fun emojisAreStripped() {
        // YouTube.js drops everything that is not a letter, number, punctuation or separator;
        // InnerTube answers a 400 for the text otherwise.
        val fields = parse(decode(CommentsApiParams.translateCommentParams("nice \uD83D\uDE00 work \u2764", "en")))
        val translate = parse(fields.single { it.number == 31 }.message!!)
        val params = parse(translate.single { it.number == 3 }.message!!)
        val comment = parse(params.single { it.number == 1 }.message!!)
        assertEquals("nice  work ", comment.single { it.number == 1 }.text)
    }

    private fun decode(params: String): ByteArray =
        Base64.getDecoder().decode(URLDecoder.decode(params, "UTF-8"))

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
