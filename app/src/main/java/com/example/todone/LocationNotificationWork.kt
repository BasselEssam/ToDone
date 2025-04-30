package com.example.todone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import mumayank.com.airlocationlibrary.AirLocation

class LocationNotificationWork(context: Context, params: WorkerParameters): Worker(context, params) {
    val db=TasksDB.getDatabase(context)
    val taskDao=db.taskDao()
    val tasks=taskDao.getNotifLocationTasks()
    override fun doWork(): Result {
        if(tasks.isNotEmpty()){
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            val cancellationTokenSource = CancellationTokenSource()
            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        val tasksToRemove= mutableListOf<Task>()
                        tasks.forEach {
                            val taskLocation=Location("")
                            taskLocation.latitude=it.locationLat
                            taskLocation.longitude=it.locationLong
                            if((location.distanceTo(taskLocation)/1000.0)>=1){
                                tasksToRemove.add(it)
                            }
                        }
                        tasks.removeAll(tasksToRemove)
                        if(tasks.isNotEmpty()){
                            Log.d("NotificationManager","New True Notification")

                            // create channel
                            createChannel()

                            val a= Intent(applicationContext,MainActivity::class.java)
                            val pending= PendingIntent.getActivity(applicationContext,2,a, PendingIntent.FLAG_IMMUTABLE)
                            // create notification
                            val notif= NotificationCompat.Builder(applicationContext,"ToDone")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(tasks.first().title)
                                .setContentText("You are near from ${tasks.first().location}. Ready to start task now?")
                                .setAutoCancel(true)
                                .setContentIntent(pending)
                                .build()

                            tasks.first().notifiedLocation=true
                            taskDao.upsertTask(tasks.first())

                            try {
                                NotificationManagerCompat.from(applicationContext).notify(2,notif)
                            } catch (e: SecurityException) {
                                println("Notification Error!")
                            }
                        }
                    } else {
                        // Handle location not available
                    }
                }
            } catch (e: SecurityException) {
                println("Location Permission Error!")
            }
            return Result.success()
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
            val channel=  NotificationChannel("ToDone","default", NotificationManager.IMPORTANCE_DEFAULT)
            val notifManager=applicationContext.getSystemService(NotificationManager::class.java)
            notifManager.createNotificationChannel(channel)
        }
    }
}