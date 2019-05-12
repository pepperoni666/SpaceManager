package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class Logger {

    companion object{
        private var file: FileWriter? = null

        fun log(msg: String){
            val df = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
            val date = df.format(Calendar.getInstance().time)
            val log = "$date -> $msg \n"
            file?.append(log)
            file?.flush()
        }
    }

    init {
        val filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Space-manager-log.txt"
        file = FileWriter(File(filename), true)
    }

    fun close(){
        file?.append("\n##############\n")
        file?.flush()
        file?.close()
    }
}