class Taskk (var title:String, var description:String, var date: String, var time:String, var done:Boolean,
             var locationLat:Double, var locationLong:Double){
    fun updateStatus(){
        done=!done
    }
}