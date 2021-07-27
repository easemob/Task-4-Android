package com.kangaroo.hotim.widget.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kangaroo.hotim.R
import com.kangaroo.hotim.data.model.HuaTiHotModel
import com.kangaroo.hotim.data.model.HuaTiModel
import com.kangaroo.hotim.tools.UStore
import kotlinx.android.synthetic.main.item_paihang.view.*

/**
 * @author shidawei
 * 创建日期：2021/7/24
 * 描述：
 */
class TipAdapter : BaseQuickAdapter<HuaTiHotModel, BaseViewHolder>(R.layout.item_paihang) {

    override fun convert(holder: BaseViewHolder, item: HuaTiHotModel) {
        holder.itemView.bianhao.text =(holder.adapterPosition+1).toString()
        holder.itemView.content.text = item.huaTiModel.name
        holder.itemView.like.text = item.like.toString()

    }

}