package pl.asseco.ptim.avagat.mobile.beaconapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pl.asseco.ptim.avagat.mobile.beaconapp.beacons.MyBeacon

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($MAC Varchar(20) PRIMARY KEY, $NAME TEXT, $CALIBRATED_RSSI DOUBLE, $ACTION_TAG_IN TEXT, $ACTION_TAG_OUT TEXT, $UUID TEXT, $MAJOR TEXT, $MINOR TEXT)"
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun clearDatabase(){
        val db = this.writableDatabase
        onUpgrade(db, 1, 2)
    }

    fun saveBeacon(beacon: MyBeacon){
        val values = ContentValues()
        values.put(MAC, beacon.bluetoothAddress)
        values.put(NAME, beacon.name)
        values.put(CALIBRATED_RSSI, beacon.calibratedRssi)
        values.put(ACTION_TAG_IN, beacon.actionTagIn)
        values.put(ACTION_TAG_OUT, beacon.actionTagOut)
        values.put(UUID, beacon.id1.toString())
        values.put(MAJOR, beacon.id2.toString())
        values.put(MINOR, beacon.id3.toString())
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateBeacon(beacon: MyBeacon){
        val values = ContentValues()
        values.put(MAC, beacon.bluetoothAddress)
        values.put(NAME, beacon.name)
        values.put(CALIBRATED_RSSI, beacon.calibratedRssi)
        values.put(ACTION_TAG_IN, beacon.actionTagIn)
        values.put(ACTION_TAG_OUT, beacon.actionTagOut)
        values.put(UUID, beacon.id1.toString())
        values.put(MAJOR, beacon.id2.toString())
        values.put(MINOR, beacon.id3.toString())
        val db = this.writableDatabase
        db.update(TABLE_NAME, values, MAC + "=?", arrayOf(beacon.bluetoothAddress))
        db.close()
    }

    fun deleteBeacon(beacon: MyBeacon){
        val db = this.writableDatabase
        db.delete(TABLE_NAME,MAC + "=?", arrayOf(beacon.bluetoothAddress))
        db.close()
    }

    fun getAllSavedBeacons(): MutableList<MyBeacon>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val list: MutableList<MyBeacon> = mutableListOf()
        if(cursor!=null && cursor.count>0){
            cursor.moveToFirst()
            do {
                val beacon = MyBeacon(cursor.getString(cursor.getColumnIndex(MAC)),
                    cursor.getString(cursor.getColumnIndex(UUID)),
                    cursor.getString(cursor.getColumnIndex(MAJOR)),
                    cursor.getString(cursor.getColumnIndex(MINOR)))
                beacon.name = cursor.getString(cursor.getColumnIndex(NAME))
                beacon.calibratedRssi = cursor.getDouble(cursor.getColumnIndex(CALIBRATED_RSSI))
                beacon.actionTagIn = cursor.getString(cursor.getColumnIndex(ACTION_TAG_IN))
                beacon.actionTagOut = cursor.getString(cursor.getColumnIndex(ACTION_TAG_OUT))
                list.add(beacon)
            }while(cursor.moveToNext())
            cursor.close()
        }
        else
            Logger.log("Data table is empty")
        return list
    }

    companion object {
        private val DB_NAME = "SpaceManagerDB"
        private val DB_VERSIOM = 1
        private val TABLE_NAME = "savedBeacons"
        private val MAC = "mac"
        private val NAME = "name"
        private val CALIBRATED_RSSI = "calibratedRSSI"
        private val ACTION_TAG_IN = "actionTagIn"
        private val ACTION_TAG_OUT = "actionTagOut"
        private val UUID = "UUID"
        private val MAJOR = "major"
        private val MINOR = "minor"
    }
}