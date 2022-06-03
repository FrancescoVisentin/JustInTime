package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    override fun onCreate(db: SQLiteDatabase)
    {
        val trainTable = "CREATE TABLE Viaggio(\n" +
                "Numero VARCHAR(50) NOT NULL PRIMARY KEY\n" +
                ");"

        val plannerTable = "CREATE TABLE Planner(\n" +
                "Nome VARCHAR(30) NOT NULL PRIMARY KEY\n" +
                ");"

        val dateTable = "CREATE TABLE Giorno(\n" +
                "Nome VARCHAR(10) NOT NULL PRIMARY KEY\n" +
                ");"

        val combinationTable  = "CREATE TABLE Associazione(\n" +
                "Numero VARCHAR(50) NOT NULL,\n" +
                "NomePlanner VARCHAR(30) NOT NULL,\n" +
                "Nome VARCHAR(10) NOT NULL,\n" +
                "StazionePartenza VARCHAR(50) NOT NULL,\n" +
                "StazioneArrivo VARCHAR(50) NOT NULL,\n" +
                "OrarioPartenza VARCHAR(10) NOT NULL,\n" +
                "OrarioArrivo VARCHAR(10) NOT NULL,\n" +
                "Durata VARCHAR(20) NOT NULL,\n" +
                "Cambi VARCHAR(10) NOT NULL,\n" +
                "PRIMARY KEY (Numero, NomePlanner, Nome),\n" +
                "FOREIGN KEY(Numero) REFERENCES Viaggio(Numero)\n" +
                "ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "FOREIGN KEY(NomePlanner) REFERENCES Planner(Nome)\n" +
                "ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "FOREIGN KEY(Nome) REFERENCES Giorno(Nome)\n" +
                "ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");\n"

        val dateValues = "INSERT INTO Giorno(Nome) VALUES\n" +
                "('Lunedi'),\n" +
                "('Martedi'),\n" +
                "('Mercoledi'),\n" +
                "('Giovedi'),\n" +
                "('Venerdi'),\n" +
                "('Sabato'),\n" +
                "('Domenica');"


        db.execSQL(trainTable)
        db.execSQL(plannerTable)
        db.execSQL(dateTable)
        db.execSQL(combinationTable)
        db.execSQL(dateValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Planner")
            db.execSQL("DROP TABLE IF EXISTS Giorno")
            db.execSQL("DROP TABLE IF EXISTS Associazione")
            db.execSQL("DROP TABLE IF EXISTS Viaggio")
            onCreate(db)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun addPlanner(name : String): Boolean {
        val cv = ContentValues()
        cv.put("Nome", name)
        return writableDatabase.insert("Planner", null, cv) != -1L
    }

    fun getPlannersName(): ArrayList<String> {
        val cursor = readableDatabase.rawQuery("SELECT Nome FROM Planner", null)
        val ret = ArrayList<String>()

        while (cursor.moveToNext()) {
            ret.add(cursor.getString(0))
        }
        cursor.close()
        return ret
    }

    fun getPlannersCount(): Int {
        val cursor =  readableDatabase.rawQuery("SELECT count(*) FROM Planner", null)
        cursor.moveToFirst()
        val ret = cursor.getInt(0)
        cursor.close()
        return ret
    }

    fun deletePlanner(name : String): Boolean {
        return writableDatabase.delete("Planner", "Nome='$name'", null) != 0
    }

    fun addTripToPlanner(numero: String, day: String, planner: String, departureStation: String, arrivalStation: String, departureTime: String, arrivalTime: String, duration: String, changes: String): Boolean
    {
        val cv = ContentValues()
        cv.put("Numero", numero)
        writableDatabase.insertWithOnConflict("Viaggio",null,cv,SQLiteDatabase.CONFLICT_IGNORE) != -1L
        val values = ContentValues().apply {
            put("Numero", numero)
            put("NomePlanner", planner)
            put("Nome", day)
            put("StazionePartenza", departureStation)
            put("StazioneArrivo",arrivalStation)
            put("OrarioPartenza", departureTime)
            put("OrarioArrivo", arrivalTime)
            put("Durata", duration)
            put("Cambi", changes)
        }
        return writableDatabase.insert("Associazione", null, values) != -1L
    }

    fun getTrips(planner:String, day: String): MutableList<TripInfo> {
        val cursor = readableDatabase.rawQuery("SELECT Numero, StazionePartenza, StazioneArrivo, OrarioPartenza, OrarioArrivo, Durata, Cambi FROM Associazione WHERE NomePlanner='${planner}' AND Nome='${day}'", null)
        val ret = mutableListOf<TripInfo>()
        var trip : TripInfo

        while (cursor.moveToNext()) {
            trip = TripInfo("")
            trip.trainNumber = cursor.getString(cursor.getColumnIndexOrThrow("Numero"))
            trip.departureStation = cursor.getString(cursor.getColumnIndexOrThrow("StazionePartenza"))
            trip.arrivalStation = cursor.getString(cursor.getColumnIndexOrThrow("StazioneArrivo"))
            trip.departureTime = cursor.getString(cursor.getColumnIndexOrThrow("OrarioPartenza"))
            trip.arrivalTime = cursor.getString(cursor.getColumnIndexOrThrow("OrarioArrivo"))
            trip.duration = cursor.getString(cursor.getColumnIndexOrThrow("Durata"))
            trip.changes = cursor.getString(cursor.getColumnIndexOrThrow("Cambi"))
            ret.add(trip)
        }
        return ret
    }

    fun deleteTrip(numero: String): Boolean
    {
        return writableDatabase.delete("Viaggio","NUmero='$numero'", null) != 0
    }

    fun checkTable()
    {
        val cursor = readableDatabase.rawQuery("SELECT * FROM Associazione", null)
        var column : String = ""
        Log.e("TYPE:", "Numero | Nome | NomePlanner | StazionePartenza | StazioneArrivo | OrarioPartenza | OrarioArrivo | Durata | Cambi")
        while (cursor.moveToNext()) {
            column = ""
            for(i in 0..cursor.columnCount-1) {
                column = column + "${cursor.getString(i)} | "
            }
            Log.e("Riga ${cursor.position}", column)
        }
        cursor.close()

        val cursor2 = readableDatabase.rawQuery("SELECT Numero FROM Viaggio", null)
        while (cursor2.moveToNext()) {
            Log.e("Colonna Numero di Viaggio:", cursor2.getString(0))
        }
        cursor2.close()

        val cursor3 = readableDatabase.rawQuery("SELECT Nome FROM Planner", null)
        while (cursor3.moveToNext()) {
            Log.e("Colonna Nome di Planner:", cursor3.getString(0))
        }
        cursor3.close()

        val cursor4 = readableDatabase.rawQuery("SELECT Nome FROM Giorno", null)
        while (cursor4.moveToNext()) {
            Log.e("Colonna Nome di Giorno:", cursor4.getString(0))
        }
        cursor4.close()
    }

    /*fun deleteRows()
    {
    writableDatabase.delete("Associazione", "Nome='Martedi'",null)
    }*/

    companion object
    {
        private const val DB_NAME = "mydb.db"
        private const val DB_VERSION = 1
    }

    data class TripInfo(var changes: String)
    {

        var trainNumber : String = ""
        var departureStation : String = ""
        var arrivalStation : String = ""
        var departureTime : String = ""
        var arrivalTime : String = ""
        var duration : String = ""
    }
}