package ru.jo4j.tourist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.jo4j.tourist.model.Mark;

public class SQLStore {
    private SQLiteDatabase store;
    private Context context;
    private List<Mark> marks = new ArrayList<>();


    public SQLStore(Context context) {
        this.context = context;
        store = new TouristBaseHelper(this.context).getWritableDatabase();
    }

    public List<Mark> getMarks() {
        Cursor cursor = this.store.query(
                TouristDbSchema.TouristTable.NAME,
                null, null, null,
                null, null, null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            marks.add(new Mark(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getDouble(cursor.getColumnIndex(TouristDbSchema.TouristTable.Cols.LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(TouristDbSchema.TouristTable.Cols.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(TouristDbSchema.TouristTable.Cols.TITLE))
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return marks;
    }

    private static ContentValues getContentValues(Mark mark) {
        ContentValues values = new ContentValues();
        values.put(TouristDbSchema.TouristTable.Cols.LATITUDE, mark.getLatitude());
        values.put(TouristDbSchema.TouristTable.Cols.LONGITUDE, mark.getLongitude());
        values.put(TouristDbSchema.TouristTable.Cols.TITLE, mark.getTitle());
        return values;
    }

    public void addMark(Mark mark) {
        ContentValues values = getContentValues(mark);
        store.insert(TouristDbSchema.TouristTable.NAME, null, values);
    }

    public Mark findMarkByID(int id) {
        return marks.get(getPositionOfTaskById(id));
    }

    public int getPositionOfTaskById(int id) {
        for (int index = 0; index < marks.size(); index++) {
            if (marks.get(index).getId() == id) {
                return index;
            }
        }
        return -1;
    }
}
