package com.kangaroo.hotim.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * @author shidawei
 * 创建日期：2021/7/26
 * 描述：
 */

@Parcelize
@JsonClass(generateAdapter = true)
data class HuaTiSimpleModel(var id:Int,var like:Int):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class HuaTiSimpleModelMap(var data:Map<Int,Int>,var name:String? = null):Parcelable