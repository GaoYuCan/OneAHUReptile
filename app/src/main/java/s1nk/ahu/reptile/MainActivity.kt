package s1nk.ahu.reptile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.concurrent.thread
import kotlin.time.Duration

class MainActivity : AppCompatActivity() {
    val TAG = "reptile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread {
            // 成绩
            val gradeRet = ReptileClient.getGrade("this is studentID", "this is p4ssw0rd")
            if (gradeRet.isSuccessful) {
                Log.e(TAG, gradeRet.data.toString())
            } else {
                Log.e(TAG, "获取成绩失败", gradeRet.error)
            }
            // 课表
            val scheduleRet = ReptileClient.getSchedule(
                "this is studentID", "this is p4ssw0rd",
                "2022-2023", 2
            )
            if (scheduleRet.isSuccessful) {
                Log.e(TAG, scheduleRet.data.toString())
            } else {
                Log.e(TAG, "获取课表失败", scheduleRet.error)
            }
            // 余额
            val cardBalanceRet =
                ReptileClient.getCardBalance("this is studentID", "this is p4ssw0rd")
            if (cardBalanceRet.isSuccessful) {
                Log.e(TAG, cardBalanceRet.data.toString())
            } else {
                Log.e(TAG, "获取余额失败", cardBalanceRet.error)
            }
            // 账单
            val cardBillRet = ReptileClient.getCardBill(
                "this is studentID",
                "this is p4ssw0rd",
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
                ReptileClient.getCampusNetQuery("this is studentID", "this is p4ssw0rd")
            if (campusNetQueryRet.isSuccessful) {
                Log.e(TAG, campusNetQueryRet.data.toString())
            } else {
                Log.e(TAG, "获取账单失败", campusNetQueryRet.error)
            }

        }
    }
}