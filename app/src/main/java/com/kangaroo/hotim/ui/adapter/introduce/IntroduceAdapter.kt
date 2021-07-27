package com.kangaroo.hotim.ui.adapter.introduce

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kangaroo.hotim.R
import com.kangaroo.hotim.data.model.TextIntroduceEntity

/**
 * @author shidawei
 * 创建日期：2021/7/19
 * 描述：
 */
class IntroduceAdapter(data: MutableList<TextIntroduceEntity>): BaseQuickAdapter<TextIntroduceEntity, BaseViewHolder>(
    R.layout.item_layout_chat_robot,data) {
    override fun convert(holder: BaseViewHolder, item: TextIntroduceEntity) {
        Introduce.TextIntroduct(holder.itemView, item ).showView()
    }
}


