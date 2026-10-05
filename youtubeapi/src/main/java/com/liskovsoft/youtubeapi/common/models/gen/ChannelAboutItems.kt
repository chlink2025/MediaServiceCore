package com.liskovsoft.youtubeapi.common.models.gen

/**
 * NEWTUBE(channel-about): the channel author's About view model. Served inline in the TV channel
 * header (description + infoRows) and, in its full form (links, artist bio, separate stat fields),
 * by the About engagement panel continuation.
 */
internal data class AboutChannelViewModel(
    val channelId: String?,
    val description: String?,
    val descriptionLabel: TextItem?,
    val artistBio: TextItem?,
    val artistBioLabel: TextItem?,
    val country: String?,
    val customLinksLabel: TextItem?,
    val subscriberCountText: String?,
    val viewCountText: String?,
    val videoCountText: String?,
    val joinedDateText: TextItem?,
    val canonicalChannelUrl: String?,
    val displayCanonicalChannelUrl: String?,
    val additionalInfoLabel: TextItem?,
    val infoRows: List<InfoRow?>?,
    val links: List<ChannelExternalLinkWrapper?>?
) {
    data class InfoRow(
        val label: String?,
        val icon: IconItem?
    )
}

internal data class ChannelExternalLinkWrapper(
    val channelExternalLinkViewModel: ChannelExternalLinkViewModel?
)

internal data class ChannelExternalLinkViewModel(
    val title: TextItem?,
    val link: LinkText?,
    val favicon: ThumbnailItem?
)

/** The link's display text plus the tappable runs carrying the real (redirect) URL. */
internal data class LinkText(
    val content: String?,
    val commandRuns: List<CommandRun?>?
) {
    data class CommandRun(
        val startIndex: Int?,
        val length: Int?,
        val onTap: OnTap?
    )
}
