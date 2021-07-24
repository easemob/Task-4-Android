package com.kangaroo.hotim.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.kangraoo.basektlib.ui.BActivity
import com.kangraoo.basektlib.widget.toolsbar.LibToolBarOptions
import com.kangraoo.basektlib.widget.toolsbar.OnLibToolBarListener
import com.kangaroo.hotim.R;
import kotlinx.android.synthetic.main.activity_main.*
import com.qdedu.baselibcommon.widget.toolsbar.CommonToolBarListener
import com.qdedu.baselibcommon.widget.toolsbar.CommonToolBarOptions
import com.kangraoo.basektlib.tools.launcher.LibActivityLauncher
import android.app.Activity
import com.qdedu.baselibcommon.ui.activity.WebPageActivity
import huati

/**
 * 自动生成：by WaTaNaBe on 2021-07-22 14:25
 * #首页#
 */
class MainActivity : BActivity(){

    companion object{

        fun startFrom(activity: Activity) {
            LibActivityLauncher.instance
                .launch(activity, MainActivity::class.java)
        }

    }

    override fun getLayoutId() = R.layout.activity_main


    override fun onViewCreated(savedInstanceState: Bundle?) {
        immersionBar {
            statusBarDarkFont(true)
            statusBarColor(R.color.theme)
        }

        val libToolBarOptions = CommonToolBarOptions()
        libToolBarOptions.titleString = "话题聊天室"
        libToolBarOptions.background = R.color.theme
        libToolBarOptions.titleColor = R.color.white
        libToolBarOptions.setNeedNavigate(false)
        setToolBar(R.id.toolbar, libToolBarOptions, object : CommonToolBarListener(){},false)
        viewData(1)
    }

    private fun viewData(id:Int){
        var data = huati[id]
        huatiName.text = data.name
        huatiContent.text = data.content
        huatiUrl.text = data.url
        huatiUrl.setOnClickListener {
            WebPageActivity.start(visitActivity(),data.url,data.name,titleVisible = true)
        }
    }

}
