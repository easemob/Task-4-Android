package com.kangaroo.hotim.tools

import android.os.CountDownTimer
import com.kangraoo.basektlib.tools.tip.Tip
import com.kangraoo.basektlib.tools.tip.TipToast

/**
 * @author shidawei
 * 创建日期：2021/7/23
 * 描述：
 */
class MyDownTimer(millisInFuture: Long, countDownInterval: Long):
        CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) {

    }

    override fun onFinish() {

    }
}