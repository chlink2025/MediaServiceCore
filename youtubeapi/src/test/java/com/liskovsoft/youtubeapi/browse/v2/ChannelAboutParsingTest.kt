package com.liskovsoft.youtubeapi.browse.v2

import com.google.gson.Gson
import com.liskovsoft.youtubeapi.browse.v2.gen.AboutChannelResult
import com.liskovsoft.youtubeapi.browse.v2.gen.BrowseResultTV
import com.liskovsoft.youtubeapi.browse.v2.gen.getAboutPanelToken
import com.liskovsoft.youtubeapi.browse.v2.gen.getChannelHeader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.InputStreamReader

/**
 * NEWTUBE(channel-about): the three response shapes that feed the channel page header and the
 * About panel - the base TV header (inline about), the params-shaped TV header (panel token) and
 * the About continuation (links, artist bio).
 */
class ChannelAboutParsingTest {
    private val gson = Gson()

    @Test
    fun testTvChannelHeaderCarriesInlineAbout() {
        val result = parse("browse/tv/2026.10.05_channel_header.json", BrowseResultTV::class.java)
        val header = result.getChannelHeader("UCBJycsmduvYEL83R_U4JriQ")

        assertNotNull(header)
        assertEquals("Marques Brownlee", header?.title)
        assertEquals("UCBJycsmduvYEL83R_U4JriQ", header?.channelId)
        assertNotNull(header?.avatarUrl)
        assertNotNull(header?.description)
        assertTrue("Full bio parsed", header?.description?.startsWith("MKBHD") == true)
        assertNotNull(header?.subscriberCount)
        assertNotNull(header?.videoCount)

        val infoRows = header?.infoRows
        assertNotNull(infoRows)
        assertEquals(5, infoRows?.size ?: -1)
        assertEquals("United States", infoRows?.getOrNull(0)?.label)
        assertEquals("PRIVACY_PUBLIC", infoRows?.getOrNull(0)?.iconType)
        assertEquals("INFO_OUTLINE", infoRows?.getOrNull(1)?.iconType)
        assertEquals("TRENDING_UP", infoRows?.getOrNull(4)?.iconType)
    }

    @Test
    fun testAboutContinuationCarriesLinksAndArtistBio() {
        val result = parse("browse/tv/2026.10.05_channel_about.json", AboutChannelResult::class.java)
        val header = result.getChannelHeader("UCa8RiU2st_48AA4WscbwYVQ")

        assertNotNull(header)
        assertEquals("UCa8RiU2st_48AA4WscbwYVQ", header?.channelId)
        assertNotNull(header?.description)
        assertNotNull(header?.artistBio)
        assertTrue("Artist bio parsed", header?.artistBio?.startsWith("Captain Jack") == true)
        assertNotNull(header?.infoRows)

        val links = header?.links
        assertNotNull(links)
        assertEquals(5, links?.size ?: -1)
        val spotify = links?.firstOrNull { it?.title == "Spotify" }
        assertNotNull("Spotify link parsed", spotify)
        assertTrue("Link target parsed", spotify?.url?.startsWith("https://") == true)
    }

    @Test
    fun testParamsHeaderCarriesDescriptionAndAboutPanelToken() {
        val result = parse("browse/tv/2026.02.21_channel_search.json", BrowseResultTV::class.java)
        val header = result.getChannelHeader("UCa8RiU2st_48AA4WscbwYVQ")

        assertNotNull(header)
        assertEquals("Captain Jack Official", header?.title)
        assertNotNull(header?.description)
        assertNotNull(header?.avatarUrl)

        // The description's engagement panel carries the About continuation (links etc.).
        assertNotNull("About panel token parsed", result.getAboutPanelToken())
    }

    private fun <T> parse(path: String, clazz: Class<T>): T {
        val stream = javaClass.classLoader?.getResourceAsStream(path)

        assertNotNull("Missing fixture: $path", stream)

        return gson.fromJson(InputStreamReader(stream, Charsets.UTF_8), clazz)
    }
}
