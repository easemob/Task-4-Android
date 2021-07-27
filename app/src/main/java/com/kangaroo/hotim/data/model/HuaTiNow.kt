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
data class HuaTiNow (val id:Int,val nextId:Int,val time:Long,val name :String): Parcelable