package it.unipd.dei.esp2022.app_embedded.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    override fun onCreate(db: SQLiteDatabase)
    {
        val trainTable = "CREATE TABLE Treno(\n" +
                "    Numero INTEGER NOT NULL PRIMARY KEY CHECK(Numero>0),\n" +
                "    Stazione_partenza VARCHAR(30),\n" +
                "    Stazione_arrivo VARCHAR(30)\n" +
                ");"

        val plannerTable = "CREATE TABLE Planner(\n" +
                "    Nome VARCHAR(30) NOT NULL PRIMARY KEY\n" +
                ");"

        val dateTable = "CREATE TABLE Giorno(\n" +
                "    Nome VARCHAR(10) NOT NULL PRIMARY KEY\n" +
                ");"

        val combinationTable  = "CREATE TABLE Associazione(\n" +
                "Numero INTEGER NOT NULL CHECK(Numero>0),\n" +
                "NomePlanner VARCHAR(30),\n" +
                "Nome VARCHAR(10) NOT NULL,\n" +
                "PRIMARY KEY (Numero, NomePlanner, Nome),\n" +
                "FOREIGN KEY(Numero) REFERENCES Treno(Numero)\n" +
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
            db.execSQL("DROP TABLE IF EXISTS Treno")
            db.execSQL("DROP TABLE IF EXISTS Giorno")
            db.execSQL("DROP TABLE IF EXISTS Associazione")
            onCreate(db)
        }
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

    companion object
    {
        private const val DB_NAME = "mydb.db"
        private const val DB_VERSION = 1
    }
}