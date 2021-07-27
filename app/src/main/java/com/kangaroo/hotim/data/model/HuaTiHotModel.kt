package com.kangaroo.hotim.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * @author shidawei
 * 创建日期：2021/7/22
 * 描述：
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class HuaTiHotModel(val huaTiModel: HuaTiModel,val like:Int):Parcelable