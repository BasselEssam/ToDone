package com.example.todone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    var title:String,
    var description:String,
    var date: String, var time:String,
    var done:Boolean,
    var locationLat:Double, var locationLong:Double,
    var location:String,
    var notifiedBefore:Boolean=false,
    var notifiedLocation:Boolean=false
)
