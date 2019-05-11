package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.os.Environment
import java.io.File
import java.io.FileWriter

class Logger {

    companion object{
        var file: FileWriter? = null

        fun log(msg: String){
            file?.append(msg)
        }
    }

    init {
        val filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Space-manager-log.txt"
        //file = FileWriter(File(filename))
    }

    fun close(){
        file?.close()
    }
}