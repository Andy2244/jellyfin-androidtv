package org.jellyfin.androidtv.ui.playback

import android.app.Activity
import android.content.Context
import android.content.Intent
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.preference.constant.PreferredVideoPlayer
import org.jellyfin.androidtv.ui.playback.rewrite.PlaybackForwardingActivity
import org.jellyfin.apiclient.model.dto.BaseItemDto
import org.jellyfin.apiclient.model.dto.BaseItemType

interface PlaybackLauncher {
	fun useExternalPlayer(itemType: BaseItemType?): Boolean
	fun getPlaybackActivityClass(itemType: BaseItemType?): Class<out Activity>
	fun interceptPlayRequest(context: Context, item: BaseItemDto?): Boolean
}

class GarbagePlaybackLauncher(
		private val userPreferences: UserPreferences
) : PlaybackLauncher {
	override fun useExternalPlayer(itemType: BaseItemType?) = when (itemType) {
		BaseItemType.Movie,
		BaseItemType.Episode,
		BaseItemType.Video,
		BaseItemType.Series,
		BaseItemType.Season,
		BaseItemType.Recording,
		-> userPreferences[UserPreferences.videoPlayer] === PreferredVideoPlayer.EXTERNAL
		BaseItemType.TvChannel,
		BaseItemType.Program,
		-> userPreferences[UserPreferences.liveTvVideoPlayer] === PreferredVideoPlayer.EXTERNAL
		else -> false
	}

	override fun getPlaybackActivityClass(itemType: BaseItemType?) = when {
		useExternalPlayer(itemType) -> ExternalPlayerActivity::class.java
		else -> PlaybackOverlayActivity::class.java
	}

	override fun interceptPlayRequest(context: Context, item: BaseItemDto?): Boolean = false
}

class RewritePlaybackLauncher : PlaybackLauncher {
	override fun useExternalPlayer(itemType: BaseItemType?) = false
	override fun getPlaybackActivityClass(itemType: BaseItemType?) = PlaybackForwardingActivity::class.java
	override fun interceptPlayRequest(context: Context, item: BaseItemDto?): Boolean {
		if (item == null) return false

		val intent = Intent(context, PlaybackForwardingActivity::class.java)
		intent.putExtra(PlaybackForwardingActivity.EXTRA_ITEM_ID, item.id)
		context.startActivity(intent)

		return true
	}
}
