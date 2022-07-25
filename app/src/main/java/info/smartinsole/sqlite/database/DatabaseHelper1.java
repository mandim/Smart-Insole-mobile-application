package info.smartinsole.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.smartinsole.sqlite.database.model.Sole;

public class DatabaseHelper1 extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "soles_db";


    public DatabaseHelper1(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create Sole table
        db.execSQL(Sole.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Sole.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertSoleData(String sole) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(Sole.COLUMN_USERID, "USERID");
        values.put(Sole.COLUMN_TIMESTAMP, "2018-02-21 00:15:42");
        values.put(Sole.COLUMN_SOLE, "SOLE");
        values.put(Sole.COLUMN_SYNCSTATUS, "SYNC");
        values.put(Sole.COLUMN_ACCLXAXIS, "ACCLX");
        values.put(Sole.COLUMN_ACCLYAXIS, "ACCLY");
        values.put(Sole.COLUMN_ACCLZAXIS, "ACCLZ");
        values.put(Sole.COLUMN_GYROROLL, "GYROROLL");
        values.put(Sole.COLUMN_GYROPITCH, "GYROPITCH");
        values.put(Sole.COLUMN_GYROYAW, "GYROYAW");
        values.put(Sole.COLUMN_MAGNXAXIS, "MAGNXAXIS");
        values.put(Sole.COLUMN_MAGNYAXIS, "MAGNYAXIS");
        values.put(Sole.COLUMN_MAGNZAXIS, "MAGNZAXIS");
        values.put(Sole.COLUMN_PE1, "PE1");
        values.put(Sole.COLUMN_PE2, "PE2");
        values.put(Sole.COLUMN_PE3, "PE3");


        // insert row
        long id = db.insert(Sole.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Sole getSole(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Sole.TABLE_NAME,
                new String[]{Sole.COLUMN_ID, Sole.COLUMN_USERID, Sole.COLUMN_SOLE, Sole.COLUMN_TIMESTAMP, Sole.COLUMN_SYNCSTATUS, Sole.COLUMN_ACCLXAXIS, Sole.COLUMN_ACCLYAXIS, Sole.COLUMN_ACCLZAXIS, Sole.COLUMN_GYROROLL, Sole.COLUMN_GYROPITCH, Sole.COLUMN_GYROYAW, Sole.COLUMN_MAGNXAXIS, Sole.COLUMN_MAGNYAXIS, Sole.COLUMN_MAGNZAXIS, Sole.COLUMN_PE1, Sole.COLUMN_PE2, Sole.COLUMN_PE3},
                Sole.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare sole object
        Sole sole = new Sole();
//                cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_USERID)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SYNCSTATUS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLXAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLYAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLZAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROROLL)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROPITCH)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROYAW)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNXAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNYAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNZAXIS)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE1)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE2)),
//                cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE3))
//                );

        // close the db connection
        cursor.close();

        return sole;
    }

//    public List<Sole> getAllSoles() {
//        List<Sole> soles = new ArrayList<>();
//
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + Sole.TABLE_NAME + " ORDER BY " +
//                Sole.COLUMN_TIMESTAMP + " DESC";
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Sole sole = new Sole();
//                sole.setId(cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)));
//                sole.setSole(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)));
//                sole.setTimestamp(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)));
//
//                soles.add(sole);
//            } while (cursor.moveToNext());
//        }
//
//        // close db connection
//        db.close();
//
//        // return soles list
//        return soles;
//    }

    public int getSolesCount() {
        String countQuery = "SELECT  * FROM " + Sole.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateSole(Sole sole) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Sole.COLUMN_SOLE, sole.getSole());

        // updating row
        return db.update(Sole.TABLE_NAME, values, Sole.COLUMN_ID + " = ?",
                new String[]{String.valueOf(sole.getId())});
    }

    public void deleteSole(Sole sole) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Sole.TABLE_NAME, Sole.COLUMN_ID + " = ?",
                new String[]{String.valueOf(sole.getId())});
        db.close();
    }
}
