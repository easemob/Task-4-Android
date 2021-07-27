package com.kangaroo.hotim.ui.adapter.introduce

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kangaroo.hotim.R
import com.kangaroo.hotim.data.model.TextIntroduceEntity
import com.kangaroo.hotim.tools.UStore
import com.kangraoo.basektlib.app.SApplication
import kotlinx.android.synthetic.main.introduce_text_view.view.*
import kotlinx.android.synthetic.main.item_layout_chat_robot.view.*
import java.lang.RuntimeException
import java.util.zip.Inflater

/**
 * @author shidawei
 * 创建日期：2021/7/19
 * 描述：
 */
sealed class Introduce<T : TextIntroduceEntity>(val viewGroup: View, val data :T) {

    abstract fun getInnerView():View

    fun isLeft():Boolean{
        val userName = UStore.getUser()!!.name
        return userName != data.name
    }

    fun showView(){

        var leftImg = viewGroup.findViewById<TextView>(R.id.leftImg)
        var leftTex = viewGroup.findViewById<TextView>(R.id.leftTex)
        var rightImg = viewGroup.findViewById<TextView>(R.id.rightImg)
        var rightTex = viewGroup.findViewById<TextView>(R.id.rightTex)
        leftImg.visibility = if(isLeft())View.VISIBLE else View.INVISIBLE
        leftTex.visibility = if(isLeft())View.VISIBLE else View.INVISIBLE
        rightImg.visibility = if(isLeft())View.INVISIBLE else View.VISIBLE
        rightTex.visibility = if(isLeft())View.INVISIBLE else View.VISIBLE

        leftTex.text = if(isLeft()) data.name else "我"
        leftImg.text = if(data.name.isNotEmpty()) data.name.subSequence(0,1) else "N"
        rightImg.text = "我"
        var bg = viewGroup.findViewById<FrameLayout>(R.id.innerLayout)
        var view = getInnerView()
        layoutView(view)
        bg.removeAllViews()
        bg.addView(view)
    }

    private fun layoutView(view:View){
        var bg = view.findViewById<FrameLayout>(R.id.layout)
        bg.setBackgroundResource(if(isLeft())leftBg() else rightBg())
        val layoutParams:FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = if(isLeft()) Gravity.START else Gravity.END
        bg.layoutParams = layoutParams
    }

    open fun leftBg() = R.drawable.app_left_layout_bg
    open fun rightBg() = R.drawable.app_right_layout_bg


    class TextIntroduct(viewGroup: View, data : TextIntroduceEntity): Introduce<TextIntroduceEntity>(viewGroup,data) {
        override fun getInnerView(): View {
            var view = LayoutInflater.from(viewGroup.context).inflate(R.layout.introduce_text_view, null)
            view.text.text = data.content
            if(isLeft()){
                view.text.setTextColor(ContextCompat.getColor(viewGroup.context,R.color.introduce_text_color))
            }else{
                view.text.setTextColor(ContextCompat.getColor(viewGroup.context,R.color.white))
            }
            return view
        }
    }


}