package s1nk.ahu.reptile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val TAG = "reptile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread {
//            OneLogin.doo()
            // 成绩
            val gradeRet = ReptileClient.getGrade("Y02114562", "1qwertyuiop.")
            if (gradeRet.isSuccessful()) {
                Log.e(TAG, gradeRet.data.toString())
            } else {
                Log.e(TAG, "获取成绩失败", gradeRet.error)
            }
            // 课表
            val scheduleRet = ReptileClient.getSchedule(
                "Y02114562", "1qwertyuiop.",
                "2022-2023", 2
            )
            if (scheduleRet.isSuccessful()) {
                Log.e(TAG, scheduleRet.data.toString())
            } else {
                Log.e(TAG, "获取课表失败", scheduleRet.error)
            }
            // 余额
            val cardBalanceRet =
                ReptileClient.getCardBalance("Y02114562", "1qwertyuiop.")
            if (cardBalanceRet.isSuccessful) {
                Log.e(TAG, cardBalanceRet.data.toString())
            } else {
                Log.e(TAG, "获取余额失败", cardBalanceRet.error)
            }
            // 账单
            val cardBillRet = ReptileClient.getCardBill(
                "Y02114562",
                "1qwertyuiop.",
                "2023-08-23",
                "2023-08-24",
                "187797"
            )
            if (cardBillRet.isSuccessful) {
                Log.e(TAG, cardBillRet.data.toString())
            } else {
                Log.e(TAG, "获取账单失败", cardBillRet.error)
            }

            // 校园网状态
            val campusNetQueryRet =
                ReptileClient.getCampusNetQuery("Y02114562", "1qwertyuiop.")
            if (campusNetQueryRet.isSuccessful) {
                Log.e(TAG, campusNetQueryRet.data.toString())
            } else {
                Log.e(TAG, "获取账单失败", campusNetQueryRet.error)
            }

        }
    }
}