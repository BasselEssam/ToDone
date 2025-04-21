import java.util.Date

class Task (var title:String, var description:String, var date: String,var time:String,var done:Boolean){
    fun updateStatus(){
        done=!done
    }
}