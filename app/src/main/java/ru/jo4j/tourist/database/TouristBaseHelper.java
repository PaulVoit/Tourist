package ru.jo4j.tourist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TouristBaseHelper extends SQLiteOpenHelper {

    public static final String DB = "tourist.db";
    public static final int VERSION = 1;

    public TouristBaseHelper(Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TouristDbSchema.TouristTable.NAME + " (" +
                        "id integer primary key autoincrement, " +
                        TouristDbSchema.TouristTable.Cols.LATITUDE + " real, " +
                        TouristDbSchema.TouristTable.Cols.LONGITUDE + " real, " +
                        TouristDbSchema.TouristTable.Cols.TITLE + " text " + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}