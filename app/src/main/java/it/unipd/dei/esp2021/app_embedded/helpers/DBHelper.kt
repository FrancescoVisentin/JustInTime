package it.unipd.dei.esp2021.app_embedded.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    override fun onCreate(db: SQLiteDatabase)
    {
        val table1 = "CREATE TABLE Treno(\n" +
                "    Numero INTEGER NOT NULL PRIMARY KEY CHECK(Numero>0),\n" +
                "    Stazione_partenza VARCHAR(30),\n" +
                "    Stazione_arrivo VARCHAR(30)\n" +
                ");"
        val table2 = "CREATE TABLE Planner(\n" +
                "    Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT CHECK(Id>0),\n" +
                "    Nome VARCHAR(30)\n" +
                ");"
        val table3 = "CREATE TABLE Giorno(\n" +
                "    Nome VARCHAR(10) NOT NULL PRIMARY KEY\n" +
                ");"
        val table4 = "CREATE TABLE Associazione(\n" +
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
        val insert1 = "INSERT INTO Giorno(Nome) VALUES\n" +
                "('Lunedi'),\n" +
                "('Martedi'),\n" +
                "('Mercoledi'),\n" +
                "('Giovedi'),\n" +
                "('Venerdi'),\n" +
                "('Sabato'),\n" +
                "('Domenica');"
        db.execSQL(table1)
        db.execSQL(table2)
        db.execSQL(table3)
        db.execSQL(table4)
        db.execSQL(insert1)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {

    }
    companion object
    {
        private const val DB_NAME = "mydb.db"
        private const val DB_VERSION = 1
    }
}