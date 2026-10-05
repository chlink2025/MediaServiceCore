package com.liskovsoft.mediaserviceinterfaces.data;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * NEWTUBE(channel-about): the channel author's "About" block - what the official client shows in
 * the channel page header and in the About panel (description, stats, links, artist bio).
 *
 * <p>The page's first {@code /browse} answer already carries the short form (title, avatar,
 * handle, subscriber/video counts, description, stats); {@link #getLinks()} and
 * {@link #getArtistBio()} are only filled by the lazily-loaded full About panel.</p>
 */
public interface ChannelHeader {
    @Nullable
    String getChannelId();

    @Nullable
    String getTitle();

    /** Public handle, e.g. {@code @mkbhd}. */
    @Nullable
    String getHandle();

    @Nullable
    String getSubscriberCount();

    @Nullable
    String getVideoCount();

    /** Full channel description (bio). */
    @Nullable
    String getDescription();

    /** Artist biography (Official Artist Channels only). */
    @Nullable
    String getArtistBio();

    @Nullable
    String getAvatarUrl();

    @Nullable
    String getBannerUrl();

    /** Country, join date, subscribers, videos, views - in delivery order. */
    @Nullable
    List<InfoRow> getInfoRows();

    /** External links (full About panel only). */
    @Nullable
    List<Link> getLinks();

    interface InfoRow {
        @Nullable
        String getLabel();

        /** One of PRIVACY_PUBLIC, INFO_OUTLINE, PERSON_RADAR, MY_VIDEOS, TRENDING_UP. */
        @Nullable
        String getIconType();
    }

    interface Link {
        @Nullable
        String getTitle();

        @Nullable
        String getUrl();

        @Nullable
        String getFaviconUrl();
    }
}
