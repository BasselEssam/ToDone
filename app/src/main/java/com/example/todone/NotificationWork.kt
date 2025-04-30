package com.example.todone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class NotificationWork(context: Context, params: WorkerParameters): Worker(context, params) {
    val db=TasksDB.getDatabase(context)
    val taskDao=db.taskDao()
    val currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"))
    val tasks=taskDao.getNotifTasks(currentDate)
        override fun doWork(): Result {
        if(tasks.isNotEmpty()){
            val tasksNotif=tasks.filter {
                (!Duration.between(LocalTime.now(), LocalTime.parse(it.time, DateTimeFormatter.ofPattern("hh:mm a"))).isNegative
                        && Duration.between(LocalTime.now(), LocalTime.parse(it.time, DateTimeFormatter.ofPattern("hh:mm a"))).toMinutes()<=30) }
            //val duration= Duration.between(LocalTime.now(), LocalTime.parse(tasksTimes.last(), DateTimeFormatter.ofPattern("hh:mm a")))
            Log.d("NotificationManager","False Notification")
            println(Duration.between(LocalTime.now(), LocalTime.parse(tasksNotif.first().time, DateTimeFormatter.ofPattern("hh:mm a"))).toMinutes())

            if(tasksNotif.isNotEmpty()){
                println(Duration.between(LocalTime.now(), LocalTime.parse(tasksNotif.first().time, DateTimeFormatter.ofPattern("hh:mm a"))).toMinutes())
                Log.d("NotificationManager","New True Notification")

                // create channel
                createChannel()

                val a=Intent(applicationContext,MainActivity::class.java)
                val pending=PendingIntent.getActivity(applicationContext,1,a,PendingIntent.FLAG_IMMUTABLE)
                // create notification
                val notif=NotificationCompat.Builder(applicationContext,"ToDone")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(tasksNotif.first().title)
                    .setContentText("Be ready, you should start this task at ${tasksNotif.first().time}")
                    .setAutoCancel(true)
                    .setContentIntent(pending)
                    .build()

                tasksNotif.first().notifiedBefore=true
                taskDao.upsertTask(tasksNotif.first())

                try {
                    NotificationManagerCompat.from(applicationContext).notify(1,notif)
                } catch (e: SecurityException) {
                    println("Notification Error!")
                }
                return Result.success()
            }
        }
        return Result.failure()

            // test start
//            createChannel()
//             val a=Intent(applicationContext,MainActivity::class.java)
//            val pending=PendingIntent.getActivity(applicationContext,1,a,PendingIntent.FLAG_IMMUTABLE)
//            val notif=NotificationCompat.Builder(applicationContext,"ToDone")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle("Teeest Notifications")
//                    .setContentText("Be ready, you should start this task at ")
//                    .setAutoCancel(true)
//                    .setContentIntent(pending)
//                    .build()
//            try {
//                    NotificationManagerCompat.from(applicationContext).notify(1,notif)
//                } catch (e: SecurityException) {
//                    println("Notification Error!")
//                }
//            return Result.success()
            // test end

    }

    fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel=  NotificationChannel("ToDone","default",NotificationManager.IMPORTANCE_DEFAULT)
            val notifManager=applicationContext.getSystemService(NotificationManager::class.java)
            notifManager.createNotificationChannel(channel)
        }
    }
}