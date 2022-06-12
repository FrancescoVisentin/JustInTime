package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    //Inizializzo il database con le tabelle Viaggio, Planner, Associazione, Giorno
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

        //Inserisco in Giorno tutti i giorni della settimana
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

    //Metodo vuoto, non sono previsti aggiornamenti del database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    //Permette di abilitare l'utilizzo delle Foreign Key
    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    //Aggiunge un record alla tabella Planner
    fun addPlanner(name : String): Boolean {
        val cv = ContentValues()
        cv.put("Nome", name)
        return writableDatabase.insert("Planner", null, cv) != -1L
    }

    //Restituisce un arrayList contenente tutti i nomi dei Planner
    fun getPlannersName(): ArrayList<String> {
        val cursor = readableDatabase.rawQuery("SELECT Nome FROM Planner", null)
        val ret = ArrayList<String>()

        while (cursor.moveToNext()) {
            ret.add(cursor.getString(0))
        }
        cursor.close()
        return ret
    }

    //Restitusce il numero di elementi contenuti nella tabella Planner
    fun getPlannersCount(): Int {
        val cursor =  readableDatabase.rawQuery("SELECT count(*) FROM Planner", null)
        cursor.moveToFirst()
        val ret = cursor.getInt(0)
        cursor.close()
        return ret
    }

    //Elimina un elemento dalla tabella Plannr
    fun deletePlanner(name : String): Boolean {
        return writableDatabase.delete("Planner", "Nome='$name'", null) != 0
    }

    //Aggiunge un elemento nella tabella Associazione
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

    //Restituisce una lista contenente i viaggi dato un Planner e un giorno
    fun getTrips(planner:String, day: String): MutableList<TripInfo> {
        val cursor = readableDatabase.rawQuery("SELECT Numero, StazionePartenza, StazioneArrivo, OrarioPartenza, OrarioArrivo, Durata, Cambi FROM Associazione WHERE NomePlanner='${planner}' AND Nome='${day}'", null)
        val ret = mutableListOf<TripInfo>()
        var trip : TripInfo

        while (cursor.moveToNext()) {
            trip = TripInfo("")
            trip.plannerName = planner
            trip.trainNumber = cursor.getString(cursor.getColumnIndexOrThrow("Numero"))
            trip.departureStation = cursor.getString(cursor.getColumnIndexOrThrow("StazionePartenza"))
            trip.arrivalStation = cursor.getString(cursor.getColumnIndexOrThrow("StazioneArrivo"))
            trip.departureTime = cursor.getString(cursor.getColumnIndexOrThrow("OrarioPartenza"))
            trip.arrivalTime = cursor.getString(cursor.getColumnIndexOrThrow("OrarioArrivo"))
            trip.duration = cursor.getString(cursor.getColumnIndexOrThrow("Durata"))
            trip.changes = cursor.getString(cursor.getColumnIndexOrThrow("Cambi"))
            ret.add(trip)
        }
        cursor.close()
        return ret
    }

    //Elimina un elemento dalla tabella Viaggio (e quindi automaticamente anche dalla tabella Associazione)
    fun deleteTrip(numero: String): Boolean
    {
        return writableDatabase.delete("Viaggio","NUmero='$numero'", null) != 0
    }

    //Restitusce il numero di viaggi inseriti in un Planner specifico
    fun getPlannerTrains(planner: String): Int
    {
        val cursor =  readableDatabase.rawQuery("SELECT count(*) FROM Associazione WHERE NomePlanner='${planner}'", null)
        cursor.moveToFirst()
        val ret = cursor.getInt(0)
        cursor.close()
        return ret
    }

    //Restituisce i viaggi dato un planner e un giorno
    fun getTripsCount(planner: String, day: String): Int
    {
        val cursor =  readableDatabase.rawQuery("SELECT count(*) FROM Associazione WHERE NomePlanner='${planner}' AND Nome='${day}'", null)
        cursor.moveToFirst()
        val ret = cursor.getInt(0)
        cursor.close()
        return ret
    }

    companion object
    {
        private const val DB_NAME = "mydb.db"
        private const val DB_VERSION = 1
    }

    data class TripInfo(var changes: String)
    {
        var plannerName : String = ""
        var trainNumber : String = ""
        var departureStation : String = ""
        var arrivalStation : String = ""
        var departureTime : String = ""
        var arrivalTime : String = ""
        var duration : String = ""
    }
}