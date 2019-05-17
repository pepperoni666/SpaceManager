package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.support.v4.content.ContextCompat.getSystemService

class Actions(private val context: Context) {

    companion object{
        val ACTION_RINGER_MODE_VIBRATE_TAG = "ACTION_RINGER_MODE_VIBRATE_TAG"
        val ACTION_RINGER_MODE_NORMAL_TAG = "ACTION_RINGER_MODE_NORMAL_TAG"
        val ACTION_RINGER_MODE_SILENT_TAG = "ACTION_RINGER_MODE_SILENT_TAG"
        val ACTION_LIST: ArrayList<String> = arrayListOf("Nothing", "Sound mode: Vibrate", "Sound mode: Normal", "Sound mode: Silent")
    }

    fun stateChangedAction(actionTag: String?){
        if(actionTag==null)
            return
        when(actionTag){
            ACTION_RINGER_MODE_VIBRATE_TAG ->{
                val amanager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                amanager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            }
            ACTION_RINGER_MODE_NORMAL_TAG -> {
                val amanager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                amanager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
            ACTION_RINGER_MODE_SILENT_TAG -> {
                val amanager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                amanager.ringerMode = AudioManager.RINGER_MODE_SILENT
            }
        }
    }
}