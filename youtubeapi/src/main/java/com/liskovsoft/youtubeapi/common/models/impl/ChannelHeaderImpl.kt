package com.liskovsoft.youtubeapi.common.models.impl

import com.liskovsoft.mediaserviceinterfaces.data.ChannelHeader

internal class ChannelHeaderImpl(
    channelId: String? = null,
    title: String? = null,
    handle: String? = null,
    subscriberCount: String? = null,
    videoCount: String? = null,
    description: String? = null,
    artistBio: String? = null,
    avatarUrl: String? = null,
    bannerUrl: String? = null,
    infoRows: List<ChannelHeader.InfoRow>? = null,
    links: List<ChannelHeader.Link>? = null
) : ChannelHeader {
    private val mChannelId = channelId
    private val mTitle = title
    private val mHandle = handle
    private val mSubscriberCount = subscriberCount
    private val mVideoCount = videoCount
    private val mDescription = description
    private val mArtistBio = artistBio
    private val mAvatarUrl = avatarUrl
    private val mBannerUrl = bannerUrl
    private val mInfoRows = infoRows
    private val mLinks = links

    override fun getChannelId(): String? = mChannelId
    override fun getTitle(): String? = mTitle
    override fun getHandle(): String? = mHandle
    override fun getSubscriberCount(): String? = mSubscriberCount
    override fun getVideoCount(): String? = mVideoCount
    override fun getDescription(): String? = mDescription
    override fun getArtistBio(): String? = mArtistBio
    override fun getAvatarUrl(): String? = mAvatarUrl
    override fun getBannerUrl(): String? = mBannerUrl
    override fun getInfoRows(): List<ChannelHeader.InfoRow>? = mInfoRows
    override fun getLinks(): List<ChannelHeader.Link>? = mLinks
}

internal class InfoRowImpl(
    label: String? = null,
    iconType: String? = null
) : ChannelHeader.InfoRow {
    private val mLabel = label
    private val mIconType = iconType

    override fun getLabel(): String? = mLabel
    override fun getIconType(): String? = mIconType
}

internal class LinkImpl(
    title: String? = null,
    url: String? = null,
    faviconUrl: String? = null
) : ChannelHeader.Link {
    private val mTitle = title
    private val mUrl = url
    private val mFaviconUrl = faviconUrl

    override fun getTitle(): String? = mTitle
    override fun getUrl(): String? = mUrl
    override fun getFaviconUrl(): String? = mFaviconUrl
}
