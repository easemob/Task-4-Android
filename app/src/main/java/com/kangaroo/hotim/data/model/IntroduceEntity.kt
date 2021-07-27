package com.kangaroo.hotim.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * @author shidawei
 * 创建日期：2021/7/19
 * 描述：
 */
@Parcelize
@JsonClass(generateAdapter = true)
class TextIntroduceEntity(val name :String,val content:String,val id:Int): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
class TextIntroduceEntityList(val list :HashMap<Int,ArrayList<TextIntroduceEntity>>,var name :String? = null): Parcelable
