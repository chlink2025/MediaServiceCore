package com.liskovsoft.youtubeapi.browse.v2.gen

import com.liskovsoft.youtubeapi.common.models.gen.AboutChannelViewModel
import com.liskovsoft.youtubeapi.common.models.gen.NavigationEndpointItem
import com.liskovsoft.youtubeapi.common.models.gen.RendererContext
import com.liskovsoft.youtubeapi.common.models.gen.TextItem
import com.liskovsoft.youtubeapi.common.models.gen.ThumbnailItem

/** NEWTUBE(channel-about): the TV channel header (contents...tvSurfaceContentRenderer.header). */
internal data class ChannelHeaderWrapper(
    val channelHeaderRenderer: ChannelHeaderRenderer?
)

internal data class ChannelHeaderRenderer(
    val title: TextItem?,
    val avatar: ThumbnailItem?,
    val backgroundImage: ThumbnailItem?,
    val subtitle: SubtitleRenderer?,
    val selectableDescription: SelectableDescription?
)

internal data class SubtitleRenderer(
    val lineRenderer: LineRenderer?
) {
    data class LineRenderer(
        val items: List<LineItemWrapper?>?
    )
}

internal data class LineItemWrapper(
    val lineItemRenderer: LineItemRenderer?
)

internal data class LineItemRenderer(
    val text: TextItem?
)

internal data class SelectableDescription(
    val selectableTextRenderer: SelectableTextRenderer?
)

internal data class SelectableTextRenderer(
    val compactDescription: TextItem?,
    val onSelectCommand: NavigationEndpointItem?
)

/** NEWTUBE(channel-about): top-level header of the params-shaped TV/WEB channel response. */
internal data class PageHeaderWrapper(
    val pageHeaderRenderer: PageHeaderRenderer?
)

internal data class PageHeaderRenderer(
    val pageTitle: String?,
    val content: PageHeaderContent?
)

internal data class PageHeaderContent(
    val pageHeaderViewModel: PageHeaderViewModel?
)

internal data class PageHeaderViewModel(
    val title: DynamicTextViewModel?,
    val image: PageHeaderImage?,
    val metadata: PageHeaderMetadata?,
    val description: DescriptionPreviewWrapper?,
    val attribution: AttributionWrapper?,
    val banner: ThumbnailItem?
)

internal data class DynamicTextViewModel(
    val text: TextItem?
)

internal data class PageHeaderImage(
    val decoratedAvatarViewModel: DecoratedAvatarViewModel?
)

internal data class DecoratedAvatarViewModel(
    val avatar: AvatarWrapper?
)

internal data class AvatarWrapper(
    val avatarViewModel: AvatarViewModel?
)

internal data class AvatarViewModel(
    val image: ThumbnailItem?
)

internal data class PageHeaderMetadata(
    val contentMetadataViewModel: ContentMetadata?
)

internal data class ContentMetadata(
    val metadataRows: List<MetadataRow?>?
) {
    data class MetadataRow(
        val metadataParts: List<MetadataPart?>?
    )

    data class MetadataPart(
        val text: TextItem?
    )
}

internal data class DescriptionPreviewWrapper(
    val descriptionPreviewViewModel: DescriptionPreviewViewModel?
)

internal data class DescriptionPreviewViewModel(
    val description: TextItem?,
    val truncationText: TextItem?,
    val rendererContext: RendererContext?
)

internal data class AttributionWrapper(
    val attributionViewModel: AttributionViewModel?
)

internal data class AttributionViewModel(
    val text: TextItem?
)

/** NEWTUBE(channel-about): About engagement panel continuation response. */
internal data class AboutChannelResult(
    val onResponseReceivedEndpoints: List<AboutResponseEndpoint?>?
)

internal data class AboutResponseEndpoint(
    val appendContinuationItemsAction: AboutContinuationAction?,
    val reloadContinuationItemsCommand: AboutContinuationAction?
)

internal data class AboutContinuationAction(
    val continuationItems: List<AboutItemWrapper?>?
)

internal data class AboutItemWrapper(
    val aboutChannelRenderer: AboutChannelRenderer?
)

internal data class AboutChannelRenderer(
    val metadata: AboutChannelMetadata?
)

internal data class AboutChannelMetadata(
    val aboutChannelViewModel: AboutChannelViewModel?
)

/** NEWTUBE(channel-about): WEB channel response metadata (description fallback). */
internal data class WebMetadata(
    val channelMetadataRenderer: ChannelMetadataRenderer?
)

internal data class ChannelMetadataRenderer(
    val title: String?,
    val description: String?,
    val avatar: ThumbnailItem?,
    val vanityChannelUrl: String?,
    val ownerUrls: List<String?>?
)
