package com.kangaroo.hotim.widget

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.kangaroo.hotim.R
import com.kangaroo.hotim.data.model.HuaTiHotModel
import com.kangaroo.hotim.tools.UStore.getZhengTi
import com.kangaroo.hotim.widget.adapter.TipAdapter
import com.kangraoo.basektlib.tools.UFont
import com.kangraoo.basektlib.tools.tip.Tip
import com.kangraoo.basektlib.tools.tip.TipToast
import com.kangraoo.basektlib.widget.common.DialogPopupConfig
import com.kangraoo.basektlib.widget.dialog.LibBaseDialog
import huati
import kotlinx.android.synthetic.main.dialog_test_tip.view.*

/**
 * author : sdw
 * e-mail : shidawei@xiaohe.com
 * time : 2019/08/29
 * desc :
 * version: 1.0
 */
class TipTestDialog(context: Context, iDialogPopup: DialogPopupConfig = DialogPopupConfig.build { }) : LibBaseDialog(context,iDialogPopup) {

    override val windowLayoutId: Int
        get() = R.layout.dialog_test_tip

    var tipAdapter: TipAdapter? = null
    override fun onViewCreated(contentView: View) {
        contentView.recycle.layoutManager = LinearLayoutManager(context)
        tipAdapter  = TipAdapter()
        contentView.recycle.adapter = tipAdapter

        val list = getZhengTi().toMutableList()
//        list.add(HuaTiHotModel(huati[0],1000))
        tipAdapter?.setNewInstance(list)
        tipAdapter?.addChildClickViewIds(R.id.like)
        tipAdapter?.setOnItemChildClickListener(object : OnItemChildClickListener{
            override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                when(view.id){
                    R.id.like ->{
                        likeViewClick?.onClickLike(adapter.data[position] as HuaTiHotModel)
                    }
                }
            }

        })
    }

    fun refresh(){
        val list = getZhengTi().toMutableList()
//        list.add(HuaTiHotModel(huati[0],1000))
        tipAdapter?.setNewInstance(list)
    }
    var likeViewClick:LikeViewClick? = null
    interface LikeViewClick{
        fun onClickLike(huati:HuaTiHotModel)
    }

    override fun onShow() {
    }

}