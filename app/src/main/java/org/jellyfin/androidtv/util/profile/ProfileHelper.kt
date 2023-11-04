package org.jellyfin.androidtv.util.profile

import android.os.Build
import org.jellyfin.androidtv.constant.Codec
import org.jellyfin.androidtv.util.DeviceUtils
import org.jellyfin.apiclient.model.dlna.CodecProfile
import org.jellyfin.apiclient.model.dlna.CodecType
import org.jellyfin.apiclient.model.dlna.DirectPlayProfile
import org.jellyfin.apiclient.model.dlna.DlnaProfileType
import org.jellyfin.apiclient.model.dlna.ProfileCondition
import org.jellyfin.apiclient.model.dlna.ProfileConditionType
import org.jellyfin.apiclient.model.dlna.ProfileConditionValue
import org.jellyfin.apiclient.model.dlna.SubtitleDeliveryMethod
import org.jellyfin.apiclient.model.dlna.SubtitleProfile

object ProfileHelper {
	// H264 codec levels https://en.wikipedia.org/wiki/Advanced_Video_Coding#Levels
	private const val H264_LEVEL_4_1 = "41"
	private const val H264_LEVEL_5_1 = "51"
	private const val H264_LEVEL_5_2 = "52"

	val MediaTest by lazy { MediaCodecCapabilitiesTest() }

	val deviceAv1CodecProfile by lazy {
		if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				MediaTest.supportsAv1()
			} else {
				TODO("VERSION.SDK_INT < Q")
			}
		) {
			CodecProfile().apply {
				type = CodecType.Video
				codec = Codec.Video.AV1
				conditions = arrayOf(av1VideoProfileCondition)
			}
		} else {
			null
		}
	}

	val av1VideoProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.EqualsAny,
			ProfileConditionValue.VideoProfile,
			listOfNotNull(
				"main",
				if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						MediaTest.supportsAV1Main10()
					} else {
						TODO("VERSION.SDK_INT < Q")
					}
				) "main 10" else null
			).joinToString("|")
		)
	}

	val deviceHevcCodecProfile by lazy {
		if (MediaTest.supportsHevc()) {
			CodecProfile().apply {
				type = CodecType.Video
				codec = Codec.Video.HEVC
				conditions = arrayOf(hevcVideoProfileCondition)
			}
		} else {
			null
		}
	}

	val hevcVideoProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.EqualsAny,
			ProfileConditionValue.VideoProfile,
			listOfNotNull(
				"main",
				if (MediaTest.supportsHevcMain10()) "main 10" else null
			).joinToString("|")
		)
	}

	val h264VideoLevelProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.LessThanEqual,
			ProfileConditionValue.VideoLevel,
			when {
				// https://developer.amazon.com/docs/fire-tv/device-specifications.html
				DeviceUtils.isFireTvStick4k() -> H264_LEVEL_5_2
				DeviceUtils.isFireTvGen2() -> H264_LEVEL_5_1
				DeviceUtils.isFireTv() -> H264_LEVEL_4_1
				DeviceUtils.isShieldTv() -> H264_LEVEL_5_2
				DeviceUtils.isZidooRTK() -> H264_LEVEL_5_2
				else -> H264_LEVEL_5_1
			}
		)
	}

	val h264VideoProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.EqualsAny,
			ProfileConditionValue.VideoProfile,
			listOfNotNull(
				"main",
				"high",
				"baseline",
				"constrained baseline",
				if (MediaTest.supportsAVCHigh10()) "high 10" else null
			).joinToString("|")
		)
	}

	val max1080pProfileConditions by lazy {
		arrayOf(
			ProfileCondition(
				ProfileConditionType.LessThanEqual,
				ProfileConditionValue.Width,
				"1920"
			),
			ProfileCondition(
				ProfileConditionType.LessThanEqual,
				ProfileConditionValue.Height,
				"1080"
			)
		)
	}

	val photoDirectPlayProfile by lazy {
		DirectPlayProfile().apply {
			type = DlnaProfileType.Photo
			container = arrayOf(
				"jpg",
				"jpeg",
				"png",
				"gif",
				"webp"
			).joinToString(",")
		}
	}

	fun audioDirectPlayProfile(containers: Array<String>) = DirectPlayProfile()
		.apply {
			type = DlnaProfileType.Audio
			container = containers.joinToString(",")
		}

	fun maxAudioChannelsCodecProfile(channels: Int) = CodecProfile()
		.apply {
			type = CodecType.VideoAudio
			conditions = arrayOf(
				ProfileCondition(
					ProfileConditionType.LessThanEqual,
					ProfileConditionValue.AudioChannels,
					channels.toString()
				)
			)
		}

	fun maxAudioChannelsCodecProfile(audioCodecs: Array<String>, channels: Int) = CodecProfile()
		.apply {
			type = CodecType.VideoAudio
			codec = audioCodecs.joinToString(",")
			conditions = arrayOf(
				ProfileCondition(
					ProfileConditionType.LessThanEqual,
					ProfileConditionValue.AudioChannels,
					channels.toString()
				)
			)
		}

	internal fun subtitleProfile(
		format: String,
		method: SubtitleDeliveryMethod
	) = SubtitleProfile().apply {
		this.format = format
		this.method = method
	}

	// CaptionInfoEx, smi
	internal fun subtitleProfile(
		format: String,
		method: SubtitleDeliveryMethod,
		didlMode: String
	) = SubtitleProfile().apply {
		this.format = format
		this.method = method
		this.didlMode = didlMode
	}
}
