package it.unipd.dei.esp2021.app_embedded.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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
                "    Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK(Id>0),\n" +
                "    Nome VARCHAR(30)\n" +
                ");"

        val dateTable = "CREATE TABLE Giorno(\n" +
                "    Nome VARCHAR(10) NOT NULL PRIMARY KEY\n" +
                ");"

        val combinationTable  = "CREATE TABLE Associazione(\n" +
                "Numero INTEGER NOT NULL CHECK(Numero>0),\n" +
                "Id INTEGER NOT NULL CHECK(Id>0),\n" +
                "Nome VARCHAR(10) NOT NULL,\n" +
                "PRIMARY KEY (Numero, Id, Nome),\n" +
                "FOREIGN KEY(Numero) REFERENCES Treno(Numero)\n" +
                "ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "FOREIGN KEY(Id) REFERENCES Planner(Id)\n" +
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun addPlanner(name : String): Boolean {
        val id = getPlannersCount()
        Log.e("ID:", "$id")

        val cv = ContentValues()
        cv.put("Id", id)
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
        return ret+1
    }

    companion object
    {
        private const val DB_NAME = "mydb.db"
        private const val DB_VERSION = 1
    }
}