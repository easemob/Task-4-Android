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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.hotim.data.model.*
import com.kangaroo.hotim.data.model.params.TokenPostParams
import com.kangaroo.hotim.data.source.AppRepository
import com.kangaroo.hotim.event.*
import com.kangaroo.hotim.tools.MqttUtil
import com.kangaroo.hotim.tools.MyDownTimer
import com.kangaroo.hotim.tools.UStore
import com.kangaroo.hotim.ui.adapter.introduce.IntroduceAdapter
import com.kangaroo.hotim.widget.TipTestDialog
import com.kangraoo.basektlib.data.DataResult
import com.kangraoo.basektlib.data.succeeded
import com.kangraoo.basektlib.tools.encryption.MessageDigestUtils
import com.kangraoo.basektlib.tools.json.HJson
import com.kangraoo.basektlib.tools.log.ULog
import com.kangraoo.basektlib.tools.tip.Tip
import com.kangraoo.basektlib.widget.dialog.LibCheckDialog
import com.qdedu.baselibcommon.ui.activity.WebPageActivity
import huati
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 自动生成：by WaTaNaBe on 2021-07-22 14:25
 * #首页#
 */
class MainActivity : BActivity(),MyDownTimer.ITimer{

    companion object{

        fun startFrom(activity: Activity) {
            LibActivityLauncher.instance
                .launch(activity, MainActivity::class.java)
        }

    }

    override fun getLayoutId() = R.layout.activity_main

    var dialog: TipTestDialog? = null
    private lateinit var adapter: IntroduceAdapter

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
//        viewData(1)
        myDownTimer = MyDownTimer(60000,1000,this)

        libCheckDialog = LibCheckDialog(visitActivity())
        libCheckDialog?.cancleVisable(View.GONE)
        libCheckDialog?.sureVisable(View.GONE)
        dialog = TipTestDialog(visitActivity())

        tongji.setOnClickListener {
            dialog!!.show()
        }

        LiveEventBus.get<RenEvent>(MqttUtil.toRen, RenEvent::class.java).observe(this, Observer {
            runOnUiThread {
                liveNum.text = UStore.set.size.toString()
            }
        })

        LiveEventBus.get<HuaTiNowEvent>(MqttUtil.huatiId, HuaTiNowEvent::class.java).observe(this, Observer {
            MqttUtil.message(MqttUtil.toHuatiId, HJson.toJson(HuaTiNow(id,0,finishTime,it.st)))
        })
        LiveEventBus.get<MessageEvent>(MqttUtil.messageData, MessageEvent::class.java).observe(this, Observer {
            if(it.mesage.id == huati[idRandom[id]].id){
                adapter.addData(it.mesage)
                adapter.notifyDataSetChanged()
                recycle.smoothScrollToPosition(adapter.itemCount-1)
            }
        })



        LiveEventBus.get<LikeEvent>(MqttUtil.toLike, LikeEvent::class.java).observe(this, Observer {
            runOnUiThread{
                huatiLike.text = UStore.getHuaTiSql(huati[idRandom[id]].id).toString()
                dialog?.refresh()
            }
        })
//        LiveEventBus.get<HuaTiStartEvent>(MqttUtil.toHuatiId, HuaTiStartEvent::class.java).observe(this, Observer {
//            id = it.huati.id
//            nextid = it.huati.nextId
//            myDownTimer = MyDownTimer(it.huati.time,1000,this)
//            runOnUiThread{
//                viewData(id)
//            }
//
//        })
        dialog?.likeViewClick = object : TipTestDialog.LikeViewClick{
            override fun onClickLike(h: HuaTiHotModel) {
                UStore.putHuaTiSql(h.huaTiModel.id)
                huatiLike.text = UStore.getHuaTiSql(huati[idRandom[id]].id).toString()
                dialog?.refresh()

                MqttUtil.message(MqttUtil.like,HJson.toJson(LikeModel(UStore.getUser()!!.name,0,h.huaTiModel.id)))
//                live_view.addFavor()

            }

        }
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        live_view.addLikeImage(R.mipmap.ic_like_red);
        like.setOnClickListener {
            UStore.putHuaTiSql(huati[idRandom[id]].id)
            huatiLike.text = UStore.getHuaTiSql(huati[idRandom[id]].id).toString()
            dialog?.refresh()

            MqttUtil.message(MqttUtil.like,HJson.toJson(LikeModel(UStore.getUser()!!.name,0,huati[idRandom[id]].id)))

            live_view.addFavor()
        }
        var list = ArrayList<TextIntroduceEntity>()
        adapter = IntroduceAdapter(list)
        var layoutManager = LinearLayoutManager(visitActivity())
        recycle.layoutManager = layoutManager
        recycle.adapter = adapter

        liveNum.text = "1"
        launch {
            val user = UStore.getUser()
            showProgressingDialog("加载数据中")
            var data = AppRepository.instance.tokenPost(
                TokenPostParams(
                    username = user!!.name, password = MessageDigestUtils.sha1(
                        user!!.pass
                    )
                )
            )
            if(data.succeeded){
                if (data is DataResult.Success) {
                    user.token = data.data.access_token
                    UStore.putUser(user)
                    withContext(Dispatchers.IO){
                        MqttUtil.mqttService()
                        MqttUtil.message(MqttUtil.ren, user.name)
                        MqttUtil.message(MqttUtil.huatiId, user.name)
                        MqttUtil.message(MqttUtil.allLike, user.name)
//                        MqttUtil.message(MqttUtil.tomessageData, user.name)
                        postDelayed(Runnable {
                            dismissProgressDialog()
                            if(UStore.h!=null){
                                id = UStore.h!!.id
                                myDownTimer = MyDownTimer(if(UStore.h!!.time-5000<0)1000 else UStore.h!!.time-5000,1000,this@MainActivity)
                                myDownTimer?.myStart()
                            }else{
                                myDownTimer?.myStart()
                            }
                        },9000)

//                        MqttUtil.message(MqttUtil.laun, user.name)
//
//                        postDelayed(Runnable {
//                            dismissProgressDialog()
//                            var userQt = UStore.getUserListFromQt()
//                            UStore.bianliLian()
//                            if (!userQt.user.contains(User(user.name))) {
//                                (userQt.user as HashSet<User>).add(User(user.name))
//                                UStore.putUserList(userQt)
//                                MqttUtil.message(
//                                    MqttUtil.cn, HJson.toJson(
//                                        UStore.getUserList()!!.apply {
//                                            extusername = user.name
//                                        })
//                                )
//                                ULog.d(
//                                    "第一次登录发现数据中心没有自己的数据发送新数据出去",
//                                    HJson.toJson(UStore.getUserList())
//                                )
//                            } else if (first) {
//                                first = false
//                                MqttUtil.message(
//                                    MqttUtil.cn, HJson.toJson(
//                                        UStore.getUserList()!!.apply {
//                                            extusername = user.name
//                                        })
//                                )
//                                ULog.d("第一次登录发送新数据出去", HJson.toJson(UStore.getUserList()))
//
//                            }
//                        }, 10000)
                    }
                } else {
                    dismissProgressDialog()
                    showToastMsg(Tip.Error, "加载失败")
                }
            }else{
                dismissProgressDialog()
                showToastMsg(Tip.Error, "加载失败")
            }

        }

        edit.setOnEditorActionListener { v, actionId, event ->
            var k = TextIntroduceEntity(UStore.getUser()!!.name,edit.text.toString(),huati[idRandom[id]].id)
            MqttUtil.message(MqttUtil.messageData, HJson.toJson(k))
            UStore.putMessage(k,huati[idRandom[id]].id)

            edit.setText("")
            false
        }


    }

    override fun onDestroy() {
        MqttUtil.message(MqttUtil.DeRen, UStore.getUser()?.name?:"")
        MqttUtil.unsubscribe()
        super.onDestroy()
    }

    var myDownTimer:MyDownTimer? = null
    var libCheckDialog:LibCheckDialog? = null

    private fun viewData(id:Int){
        var data = huati[id]
        huatiName.text = data.name
        huatiContent.text = data.content
        huatiUrl.text = data.url
        huatiUrl.setOnClickListener {
            WebPageActivity.start(visitActivity(),data.url,data.name,titleVisible = true)
        }
        huatiContent.setOnClickListener {
            libCheckDialog?.title(data.name)
            libCheckDialog?.content(data.content)

//            libCheckDialog?.onLibDialogListener = (null)
            libCheckDialog?.show()
        }

        huatiLike.text = UStore.getHuaTiSql(data.id).toString()
        var list = ArrayList<TextIntroduceEntity>()
        list.addAll(UStore.getMessage(data.id))
        adapter.setNewInstance(list)
        adapter.notifyDataSetChanged()
    }

    override fun time(time: String,millisUntilFinished: Long) {
        finishTime = millisUntilFinished
        textView2.text = "时间倒计时$time"
    }

    override fun timeFinish() {
        myDownTimer = MyDownTimer(60000,1000,this)
        id = (id+1)%idRandom.size
        myDownTimer?.myStart()
    }


    var finishTime = 60000L

    var id = 0

    var idRandom = arrayListOf(
            0,2,3,1,2,3,1,3,4,2,1,2,3,3,4,2,1,2,
            3,1,2,1,0,2,3,1,2,3,1,0,2,1,2,3,1,0,
            1,3,4,2,3,4,2,3,0,1,2,3,4,3,0,1,2,3,
            0,2,3,1,2,3,1,3,4,2,1,2,3,3,4,2,1,2,
            3,4,2,3,0,2,3,1,3,0,3,0,2,1,3,0,3,0,
            2,3,1,2,3,1,3,4,2,3,2,3,1,4,2,3,2,3,
            1,2,4,2,3,0,2,3,1,0,2,3,0,3,1,0,2,3,
            2,3,1,2,3,1,3,4,2,3,2,3,1,4,2,3,2,3,
            2,3,1,0,2,3,1,2,3,1,0,2,3,2,3,1,0,2,
            3,4,2,3,1,2,3,1,3,4,3,1,2,1,3,4,3,1,
            2,3,1,2,1,0,2,3,1,2,2,1,0,3,1,2,2,1,
            3,1,3,4,2,3,4,2,2,3,4,2,3,2,2,3,4,2,
            1,3,4,2,3,4,2,3,0,2,2,3,4,3,0,2,2,3,
            3,1,3,0,2,3,1,2,3,1,0,2,3,2,3,1,0,2,
            3,4,2,3,1,2,4,2,3,0,3,1,2,2,3,0,3,1,
            2,3,1,0,2,3,1,2,3,1,0,2,3,2,3,1,0,2,
            3,4,2,3,2,3,1,0,2,3,3,2,3,0,2,3,3,2,
            1,2,3,1,3,4,3,0,2,3,1,3,4,0,2,3,1,3,
            1,3,0,2,3,1,2,3,1,3,2,3,1,3,1,3,2,3,
            4,2,3,1,2,4,2,3,0,2,1,2,4,3,0,2,1,2,
            3,1,0,2,3,1,2,3,1,3,2,3,1,3,1,3,2,3,
            4,2,3)

    override fun timeStart() {
        viewData(idRandom[id])
    }

}
