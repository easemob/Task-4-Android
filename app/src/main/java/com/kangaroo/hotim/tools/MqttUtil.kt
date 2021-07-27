package com.kangaroo.hotim.tools

import android.util.Log
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.hotim.data.model.*
import com.kangaroo.hotim.event.HuaTiNowEvent
import com.kangaroo.hotim.event.HuaTiStartEvent
import com.kangaroo.hotim.event.LikeEvent
import com.kangaroo.hotim.event.RenEvent
import com.kangraoo.basektlib.tools.json.HJson
import com.kangraoo.basektlib.tools.log.ULog
import com.kangraoo.basektlib.tools.task.TaskManager
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author shidawei
 * 创建日期：2021/6/4
 * 描述：
 */
object MqttUtil {

    var ren = "REN"
    var toRen = "TO_REN"
    var DeRen = "DE_REN"
    var huatiId = "HUATI_ID"
    var toHuatiId = "TO_HUATI_ID"
    var like = "LIKE"
    var toLike = "TO_LIKE"
    var allLike = "ALL_LIKE"
    var toAllLike = "TO_ALL_LIKE"
    var toAllMessageData = "TO_ALL_MESSAGE_DATA"
    var messageData = "MESSAGE_DATA"
    var tomessageData = "TO_MESSAGE_DATA"

    var mqttClient: MqttClient? = null
    val  executorService  = ThreadPoolExecutor(
            1,
            1,
            0,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue()
    )
    val qosLevel = 0
    val qosLevelNomar = 1
    val qosLevelHight = 2

    fun mqttService() {
        try {
            initmqtt()
            extended()
            lineService()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun initmqtt() {
        val user = UStore.getUser()

        var  deviceId = user!!.name
        var appId = "tgqna0"

        var endpoint =  "tgqna0.cn1.mqtt.chat"

//        clientId由两部分组成，格式为DeviceID@appId，其中DeviceID由业务方自己设置，appId在console控制台创建，clientId总长度不得超过64个字符。
        var  clientId = "$deviceId@$appId";

//  QoS参数代表传输质量，可选0，1，2。详细信息，请参见名词解释。
        val memoryPersistence = MemoryPersistence()
        try {
            mqttClient = MqttClient("tcp://$endpoint:1883", clientId, memoryPersistence)
        }catch (e: MqttException){
            e.printStackTrace()
        }

//        设置客户端发送超时时间，防止无限阻塞。
        mqttClient?.setTimeToWait(5000)
    }

    fun lineService() {
        val user = UStore.getUser()

        val mqttConnectOptions = MqttConnectOptions()

        /**
         * 用户名，在console中注册
         */
        mqttConnectOptions.userName = user!!.name

        /**
         * 用户密码为第一步中申请的token
         */
        mqttConnectOptions.password = user!!.token!!.toCharArray()
        mqttConnectOptions.isCleanSession = true
        mqttConnectOptions.keepAliveInterval = 90
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
        mqttConnectOptions.connectionTimeout = 5000
        try {
            mqttClient?.connect(mqttConnectOptions)
        } catch (e: MqttException) {
            e.printStackTrace();
        }
        // 暂停1秒钟，等待连接订阅完成
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe() {
        try {
            mqttClient?.unsubscribe(arrayOf(ren, toRen,DeRen, huatiId, toHuatiId,like,toLike,allLike,toAllLike,messageData, tomessageData,toAllMessageData));
        } catch (e: MqttException) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     */
    fun message(type:String,message: String) {

        try {
            /**
             * 构建一个Mqtt消息
             */
            val message = MqttMessage(message.toByteArray())
            //设置传输质量
            message.qos = qosLevel
            /**
             * 发送普通消息时，Topic必须和接收方订阅的Topic一致，或者符合通配符匹配规则。
             */
            TaskManager.taskExecutor.execute(Runnable {
                try {
                    mqttClient?.publish(type, message)
                }catch (e1: MqttException){
                    e1.printStackTrace();
                }
            })


        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    var threadExecutor = Executors.newSingleThreadExecutor()

    var inithuati = false

    fun extended() {
        val user = UStore.getUser()

        mqttClient?.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
            }

            /**
             * 接收消息回调方法
             * @param s
             * @param mqttMessage
             */
            override fun messageArrived(s: String, mqttMessage: MqttMessage) {
                var st = String(mqttMessage.payload)
                when(s){
                    ren -> {
                        if(user!=null){
                            /**
                             * 发送数据给其他用户，但要排除自己
                             */
                            UStore.reRen()
                            message(toRen,user.name)
                        }
                    }
                    toRen ->{
                        if(user!=null&&st != user.name){
                            /**
                             * 接收到数据请求,如果是自己发的则不增加
                             */
                            UStore.putUserName(st)
                        }
                    }
                    DeRen -> {
                        if(user!=null){
                            /**
                             * 接收到数据请求,如果是自己发的则不增加
                             */
                            UStore.deUserName(st)
                        }
                    }
                    huatiId ->{
                        if(user!=null&&st != user.name){
                            /**
                             * 接收到数据请求,如果是自己发的则不处理
                             */
                            LiveEventBus.get<HuaTiNowEvent>(MqttUtil.huatiId, HuaTiNowEvent::class.java).post(HuaTiNowEvent(st))
                        }
                    }
                    toHuatiId ->{
                        var data = HJson.fromJson<HuaTiNow>(st)!!
//                        if(user!=null&&data.name == user.name&&!inithuati){
//                            inithuati = true
//                            LiveEventBus.get<HuaTiStartEvent>(MqttUtil.toHuatiId, HuaTiStartEvent::class.java).post(HuaTiStartEvent(data))
//                        }
                        if(user!=null&&data.name == user.name){

                            ULog.d(st,"接收数据",user.name,"接受到数据请求，并返回数据",HJson.toJson(data))
                            UStore.putHuaTi(data)
                        }
//                        LiveEventBus.get<HuaTiStartEvent>(MqttUtil.toHuatiId, HuaTiStartEvent::class.java).post(HuaTiStartEvent(data))
                    }
                    like->{
                        var k = HJson.fromJson<LikeModel>(st)!!
                        if(user!=null){
                            if(user.name!=k.userName){
                                UStore.putHuaTiSql(k.id)
                                LiveEventBus.get<LikeEvent>(MqttUtil.toLike, LikeEvent::class.java).post(LikeEvent())
                            }
                            message(toLike,HJson.toJson(LikeModel(user.name,UStore.getHuaTiSql(k.id),k.id)))
                        }
                    }
                    toLike ->{
                        var k = HJson.fromJson<LikeModel>(st)!!
                        if(user!=null&&user.name!=k.userName){
                            UStore.putHuaTiCheckSql(k.like,k.id)
                        }
                    }
                    allLike ->{
                        if(user!=null&&st != user.name){
                            message(toAllLike,HJson.toJson(UStore.getZhengTi2().apply {
                                name = st
                            }))
                        }
                    }
                    toAllLike->{
                        var k = HJson.fromJson<HuaTiSimpleModelMap>(st)!!
                        if(user!=null&&k.name == user.name){
                            k.data.forEach { t, u ->
                                UStore.putHuaTiCheckSql(u,t,false)
                            }
                        }
                    }
                    messageData->{
                        var k = HJson.fromJson<TextIntroduceEntity>(st)!!
                        if(user!=null&&user.name!=k.name){
                            UStore.putMessage(k,k.id)
                        }
                    }
                    tomessageData->{
                        if(user!=null&&st != user.name){
                            message(toAllMessageData,HJson.toJson(UStore.getAllMessage().apply { name = st }))
                        }
                    }
                    toAllMessageData->{
                        var k = HJson.fromJson<TextIntroduceEntityList>(st)!!
                        if(user!=null&&k.name == user.name){
                            k.list.forEach {
                                UStore.putMessages(it.value,it.key)
                            }
                        }
                    }
                }
//                when (s) {
//                    lacn -> {
//                        var st = String(mqttMessage.payload)
//                        if(user!=null&&st != user.name){
//                            if(UStore.getUserList()!=null){
//                                /**
//                                 * 发送数据给其他用户，但要排除自己
//                                 */
//                                message(cn,HJson.toJson(UStore.getUserList()!!.apply {
//                                    username = st
//                                }))
//
//                                ULog.d(st,"发送拉取数据请求",user.name,"接受到数据请求，并返回数据",HJson.toJson(UStore.getUserList()))
//
//                            }
//                        }
//                    }
//                    laun -> {
//                        var st = String(mqttMessage.payload)
//                        if(user!=null&&st != user.name){
//                            if(UStore.getLian()!=null){
//                                /**
//                                 * 发送数据给其他用户，但要排除自己
//                                 */
//                                message(un,HJson.toJson(CoinNodeModel(username = st,coinNode = UStore.getLian()!!)))
//
//                                ULog.d(st,"发送拉取链请求",user.name,"接受到数据请求，并返回数据",HJson.toJson(CoinNodeModel(username = st,coinNode = UStore.getLian()!!)))
//                            }
//                        }
//                    }
//                    cn -> {
//                        var st = String(mqttMessage.payload)
//                        var users =  HJson.fromJson<UserList>(st)
//                        ULog.d(user?.name,"接收到数据请求",HJson.toJson(users))
//
//                        if(users?.username!=null){
//                            if(user!=null&&users.username == user.name){
//                                UStore.putUserListAll(users)
//                                ULog.d(user.name,"接收到数据请求并存入数据")
//                            }
//                        }else if(users?.extusername!=null){
//                            if(user!=null&&users.extusername != user.name){
//                                UStore.putUserListAll(users)
//                                ULog.d(user.name,"接收到数据请求并存入数据")
//                            }
//                        }
//                    }
//                    un -> {
//                        threadExecutor.execute(Runnable {
//                            var st = String(mqttMessage.payload)
//                            var lian =  HJson.fromJson<CoinNodeModel>(st)
//                            ULog.d(user?.name,"接收到链请求",HJson.toJson(lian))
//
//                            if(lian?.username!=null){
//                                if(user!=null&&lian.username == user.name){
//                                    UStore.putLian(lian)
//                                    ULog.d(user.name,"接收到数据请求并存入数据")
//                                }
//                            }else if(lian?.extusername!=null){
//                                if(user!=null&&lian.extusername != user.name){
//                                    UStore.putLian(lian)
//                                    ULog.d(user.name,"接收到数据请求并存入数据")
//                                }
//                            }
//                        })
//
//
//                    }
//
//                }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }

            /**
             * 连接完成回调方法
             * @param b
             * @param s
             */
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                /**
                 * 客户端连接成功后就需要尽快订阅需要的Topic。
                 */
                println("connect success")
                Log.d("connect", "connect success")
                executorService.submit {
                    try {
                        val topicFilter = arrayOf<String>(ren, toRen,DeRen,huatiId, toHuatiId,like,toLike,allLike,toAllLike,messageData, tomessageData,toAllMessageData)
                        val qos = intArrayOf(qosLevel,qosLevel, qosLevel, qosLevel, qosLevel,qosLevel, qosLevel, qosLevel, qosLevel,qosLevel, qosLevel, qosLevel)
                        mqttClient?.subscribe(topicFilter, qos)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}