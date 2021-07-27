package com.kangaroo.hotim.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * @author shidawei
 * 创建日期：2021/7/27
 * 描述：
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class LikeModel(val userName: String,val like:Int,val id:Int): Parcelable

