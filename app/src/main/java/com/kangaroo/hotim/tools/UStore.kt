package com.kangaroo.hotim.tools

import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.hotim.data.model.*
import com.kangaroo.hotim.event.LikeEvent
import com.kangaroo.hotim.event.MessageEvent
import com.kangaroo.hotim.event.RenEvent
import com.kangraoo.basektlib.tools.HString
import com.kangraoo.basektlib.tools.UTime
import com.kangraoo.basektlib.tools.json.HJson
import com.kangraoo.basektlib.tools.log.ULog
import com.kangraoo.basektlib.tools.store.MMKVStore
import com.kangraoo.basektlib.tools.store.MemoryStore
import huati

/**
 * @author shidawei
 * 创建日期：2021/6/21
 * 描述：
 */
const val USER:String = "user"
const val DATA:String = "data"
const val MESSAGE:String = "message"
const val HOTIM:String = "hotim"

object UStore {

    fun putUser(user : UserModel){
        MMKVStore.instance(HOTIM).put(USER,user)
    }

    fun getUser():UserModel? = MMKVStore.instance(HOTIM).get(USER,null,UserModel::class.java)

    fun clearUser(){
        MMKVStore.instance(HOTIM).remove(USER)
    }

    var set:HashSet<String> = HashSet<String>()

    fun putUserName(user:String){
        set.add(user)
        LiveEventBus.get<RenEvent>(MqttUtil.toRen,RenEvent::class.java).post(RenEvent())
    }

    fun reRen(){
        set.clear()
        var user = getUser()
        if(user!=null){
            set.add(user.name)
        }
    }

    fun deUserName(st: String) {
        set.remove(st)
        LiveEventBus.get<RenEvent>(MqttUtil.toRen,RenEvent::class.java).post(RenEvent())
    }

    var h:HuaTiNow? = null

    @Synchronized
    fun putHuaTi(huati : HuaTiNow){
        if(h==null){
            h = huati
        }
        if(huati.id>=h!!.id){
            h = huati
        }
    }


    @Synchronized
    fun putHuaTiSql(id:Int){
        val con = getHuaTiSql(id)
        ULog.d("con",con,id)
        MMKVStore.instance(HOTIM).put(DATA+id,con+1)
    }

    @Synchronized
    fun getHuaTiSql(id:Int) = MMKVStore.instance(HOTIM).get(DATA+id,0,Int::class.java)!!

    fun getZhengTi(): List<HuaTiHotModel> {
        var list = ArrayList<HuaTiHotModel>()
        huati.forEach {
            list.add(HuaTiHotModel(it, getHuaTiSql(it.id)))
        }
        return list.sortedByDescending { it.like }
    }





    fun putHuaTiCheckSql(like: Int, id: Int,bus:Boolean = true) {
        var m = getHuaTiSql(id)
        if(m<like){
            MMKVStore.instance(HOTIM).put(DATA+id,like)
            if(bus){
                LiveEventBus.get<LikeEvent>(MqttUtil.toLike, LikeEvent::class.java).post(LikeEvent())
            }
        }
    }

    fun getZhengTi2(): HuaTiSimpleModelMap {
        var map: HashMap<Int,Int> = HashMap()
        var list = getZhengTi()
        list.forEach {
            map.put(it.huaTiModel.id,it.like)
        }
        return HuaTiSimpleModelMap(map)
    }

    @Synchronized
    fun putMessages(mesage : ArrayList<TextIntroduceEntity>,id: Int){
        if(mesage.size>getMessage(id).size){
            MemoryStore.instance.put(MESSAGE+id,mesage)
        }
    }

    @Synchronized
    fun putMessage(mesage : TextIntroduceEntity,id: Int,bus:Boolean = true){
        var mem = getMessage(id)
        mem.add(mesage)
        if(bus){
            LiveEventBus.get<MessageEvent>(MqttUtil.messageData, MessageEvent::class.java).post(MessageEvent(mesage))
        }
        MemoryStore.instance.put(MESSAGE+id,mem)
    }
    @Synchronized
    fun getMessage(id: Int):ArrayList<TextIntroduceEntity>{

        var mem :ArrayList<TextIntroduceEntity> = MemoryStore.instance.get(MESSAGE+id,ArrayList<TextIntroduceEntity>())!!
        return mem
//        MMKVStore.instance(HOTIM).
    }

    @Synchronized
    fun getAllMessage():TextIntroduceEntityList{
        var list = HashMap<Int,ArrayList<TextIntroduceEntity>>()
        huati.forEach {
            list.put(it.id,getMessage(it.id))
        }
        return TextIntroduceEntityList(list)
    }




//    fun removeUserName(user:String){
//        set.remove(user)
//    }

//    fun putUserList(user : UserList){
//        MMKVStore.instance(WTCOIN).put(USERS,user)
//    }
//
//    fun getUserList():UserList? = MMKVStore.instance(WTCOIN).get(USERS,null,UserList::class.java)
//
//    fun getLian():CoinNode? = MMKVStore.instance(WTCOIN).get(LIAN,null,CoinNode::class.java)
//
//    fun putLocLian(coin : CoinNode){
//        MMKVStore.instance(WTCOIN).put(LIAN,coin)
//    }
//
//
//
//    var set:HashSet<UserList> = HashSet<UserList>()
//
//    @Synchronized
//    fun putUserListAll(user : UserList){
//        user.username = null
//        if(set.size>1000){
//            return
//        }
//        set.add(user)
//    }
//
//    @Synchronized
//    fun getUserListFromQt():UserList{
//        var userList = HashSet<User>()
//        var pre = getUserList()?.user
//        if(pre!=null){
//            userList.addAll(pre)
//        }
//        var iter = set.iterator()
//        while (iter.hasNext()){
//            userList.addAll(iter.next().user)
//            iter.remove()
//        }
//        var j = UserList(null,null,userList)
//        putUserList(j)
//        return j
//    }
//
//    @Synchronized
//    fun putLian(lian: CoinNodeModel) {
//        val prelian = getLian()
//        if(prelian!=null){
//            bianliLian()
//            if(lian.lianchang> length){
//                putLocLian(lian.coinNode)
//                bianliLian()
//            }else if (lian.lianchang == length&&lian.lastTime < lastNode!!.time){
//                putLocLian(lian.coinNode)
//                bianliLian()
//            }else{
//                MqttUtil.message(MqttUtil.un, HJson.toJson(CoinNodeModel(extusername = getUser()!!.name,lianchang = length,lastTime = lastNode!!.time,coinNode = node!!)))
//            }
//        }else{
//            putLocLian(lian.coinNode)
//            bianliLian()
//        }
//    }
//
//    @Volatile
//    var lastNode:CoinNode? = null
//
//    @Volatile
//    var node:CoinNode? = null
//
//    @Volatile
//    var length = 0
//
//    @Synchronized
//    fun bianliLian(){
//        val prelian = getLian()
//        node = prelian
//        var temp = prelian
//        if(temp==null){
//            length = 0
//            lastNode = null
//            return
//        }
//        var l = 1
//        while (temp!!.next!=null){
//            l++
//            temp = temp.next
//        }
//        length = l
//        lastNode = temp
//    }
//
//
//    @Synchronized
//    fun putOne(last:CoinNode?,length :Int = 0,nodes:CoinNode? = null){
//        var node = CoinNode(last?.hashCode()?.toString(),
//            HString.to32UUID(),
//            UTime.currentTimeMillis(), User(getUser()?.name!!),null)
//
//        if(last!=null){
//            last.next = node
//            MqttUtil.message(MqttUtil.un, HJson.toJson(CoinNodeModel(extusername = getUser()!!.name,lianchang = 1 + length,lastTime = node.time,coinNode = nodes!!)))
//            node = nodes
//        }else{
//            MqttUtil.message(MqttUtil.un, HJson.toJson(CoinNodeModel(extusername = getUser()!!.name,lianchang = 1,lastTime = node.time,coinNode = node)))
//        }
//        putLocLian(node)
//        bianliLian()
//    }

}