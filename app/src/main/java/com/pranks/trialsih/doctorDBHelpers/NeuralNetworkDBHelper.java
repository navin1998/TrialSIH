package com.pranks.trialsih.doctorDBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NeuralNetworkDBHelper extends SQLiteOpenHelper {


    private Context context;

    private static final String DATABASE_NAME = "neuralNetworks.db";

    public static final String TABLE_NAME = "NeuralNetworks";

    public static final String COL_1 = "PresName";
    public static final String COL_2 = "PresSymptoms";
    public static final String COL_3 = "PresDiagnosis";
    public static final String COL_4 = "PresAdvices";

    public NeuralNetworkDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_QUERY =

                "create table " + TABLE_NAME + " (" + COL_1 + " text, " +
                        COL_2 + " text, " +
                        COL_3 + " text, " +
                        COL_4 + " text);";

        db.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);

    }


    public boolean addName(String name)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = 0;
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, name);

        result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }

    }



    public ArrayList<String> getNames()
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<String> namesList = new ArrayList<>();

        String query = "select * from " + TABLE_NAME;

        Cursor res = db.rawQuery(query, null);

        while (res.moveToNext())
        {
            namesList.add(res.getString(res.getColumnIndex(COL_1)));
        }

        return namesList;
    }


}
