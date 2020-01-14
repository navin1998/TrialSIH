package com.pranks.trialsih.doctorDBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DoctorCredentialsDBHelper extends SQLiteOpenHelper {


    private Context mContext;

    private static final String DATABASE_NAME = "doctorCredentials.db";

    public static final String TABLE_NAME = "Doctor";

    public static final String COL_1 = "DoctorRegNumber";


    public DoctorCredentialsDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_QUERY =
                "create table " + TABLE_NAME + " (" + COL_1 + " text);";

        db.execSQL(CREATE_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);

    }


    public boolean addToDatabase(String regNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = 0;

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, regNumber);

        result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }


    public void deleteDatabase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }


    public String getRegNumber()
    {

        String regNumber = null;

        SQLiteDatabase db = null;

        Cursor res = null;

        try {

            db = this.getWritableDatabase();
            res = db.rawQuery("select * from " + TABLE_NAME, null);


            if (res.moveToNext() && res.getCount() == 1)
            {
                regNumber = res.getString(0);
            }
        }
        catch (Exception e)
        {

        }

        return regNumber;

    }


}
