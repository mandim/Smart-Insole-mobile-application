package info.smartinsole.sqlite.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import info.smartinsole.sqlite.database.model.Sole;
import info.smartinsole.sqlite.database.model.Test;
import info.smartinsole.sqlite.view.TestFragment;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQL Error";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "soles_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create Sole table
        db.execSQL(Sole.CREATE_TABLE);
        db.execSQL(Test.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + Sole.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Test.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /**
     *  CODE for SOLE TABLE
     */
    public long insertSoleData(String sole) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(Sole.COLUMN_USERID, "USERID");
        values.put(Sole.COLUMN_TIMESTAMP, "2018-02-21 00:15:42");
        values.put(Sole.COLUMN_SOLE, "SOLE");
        values.put(Sole.COLUMN_R_L, "RIGHT_LEFT");
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
        values.put(Sole.COLUMN_PE4, "PE4");
        values.put(Sole.COLUMN_PE5, "PE5");
        values.put(Sole.COLUMN_PE6, "PE6");
        values.put(Sole.COLUMN_PE7, "PE7");
        values.put(Sole.COLUMN_PE8, "PE8");
        values.put(Sole.COLUMN_PE9, "PE9");
        values.put(Sole.COLUMN_PE10, "PE10");
        values.put(Sole.COLUMN_PE11, "PE11");
        values.put(Sole.COLUMN_PE12, "PE12");
        values.put(Sole.COLUMN_PE13, "PE13");
        values.put(Sole.COLUMN_PE14, "PE14");
        values.put(Sole.COLUMN_PE15, "PE15");
        values.put(Sole.COLUMN_PE16, "PE16");
        values.put(Sole.COLUMN_GTF, "GTF");

        // insert row
        long id = db.insert(Sole.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    /**
     * Insert data from test, multiple rows
      */
    public void insertMultipleSoleData(List<Sole> soles) {
        int size = soles.size();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i=0; i<size; i++) {
                ContentValues values = new ContentValues();
                values.put(Sole.COLUMN_USERID, "USER_ID");
                values.put(Sole.COLUMN_TIMESTAMP, soles.get(i).getTimestamp());
                values.put(Sole.COLUMN_SOLE, "SOLE");
                values.put(Sole.COLUMN_R_L, soles.get(i).getRL());
                values.put(Sole.COLUMN_SYNCSTATUS, "SYNC");
                values.put(Sole.COLUMN_ACCLXAXIS, soles.get(i).getAccx());
                values.put(Sole.COLUMN_ACCLYAXIS, soles.get(i).getAccy());
                values.put(Sole.COLUMN_ACCLZAXIS, soles.get(i).getAccz());
                values.put(Sole.COLUMN_GYROROLL, soles.get(i).getGyropoll());
                values.put(Sole.COLUMN_GYROPITCH, soles.get(i).getGyropitch());
                values.put(Sole.COLUMN_GYROYAW, soles.get(i).getGyroyaw());
                values.put(Sole.COLUMN_MAGNXAXIS, soles.get(i).getMagnx());
                values.put(Sole.COLUMN_MAGNYAXIS, soles.get(i).getMagny());
                values.put(Sole.COLUMN_MAGNZAXIS, soles.get(i).getMagnz());
                values.put(Sole.COLUMN_PE1, soles.get(i).getPe1());
                values.put(Sole.COLUMN_PE2, soles.get(i).getPe2());
                values.put(Sole.COLUMN_PE3, soles.get(i).getPe3());
                values.put(Sole.COLUMN_PE4, soles.get(i).getPe4());
                values.put(Sole.COLUMN_PE5, soles.get(i).getPe5());
                values.put(Sole.COLUMN_PE6, soles.get(i).getPe6());
                values.put(Sole.COLUMN_PE7, soles.get(i).getPe7());
                values.put(Sole.COLUMN_PE8, soles.get(i).getPe8());
                values.put(Sole.COLUMN_PE9, soles.get(i).getPe9());
                values.put(Sole.COLUMN_PE10, soles.get(i).getPe10());
                values.put(Sole.COLUMN_PE11, soles.get(i).getPe11());
                values.put(Sole.COLUMN_PE12, soles.get(i).getPe12());
                values.put(Sole.COLUMN_PE13, soles.get(i).getPe13());
                values.put(Sole.COLUMN_PE14, soles.get(i).getPe14());
                values.put(Sole.COLUMN_PE15, soles.get(i).getPe15());
                values.put(Sole.COLUMN_PE16, soles.get(i).getPe16());
                values.put(Sole.COLUMN_GTF, soles.get(i).getGtf());
                // insert row
                long id = db.insert(Sole.TABLE_NAME, null, values);
            }
            // close db connection
            db.setTransactionSuccessful();
        } catch (Exception e){
            Log.e("Problem", e.getMessage());
        } finally {
            db.endTransaction();
        }


    }

    /**
     * get Sole by Id
     */
    public Sole getSole(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Sole.TABLE_NAME,
                new String[]{Sole.COLUMN_ID, Sole.COLUMN_USERID, Sole.COLUMN_SOLE, Sole.COLUMN_TIMESTAMP, Sole.COLUMN_R_L, Sole.COLUMN_SYNCSTATUS,
                        Sole.COLUMN_ACCLXAXIS, Sole.COLUMN_ACCLYAXIS, Sole.COLUMN_ACCLZAXIS,
                        Sole.COLUMN_GYROROLL, Sole.COLUMN_GYROPITCH, Sole.COLUMN_GYROYAW,
                        Sole.COLUMN_MAGNXAXIS, Sole.COLUMN_MAGNYAXIS, Sole.COLUMN_MAGNZAXIS,
                        Sole.COLUMN_PE1, Sole.COLUMN_PE2, Sole.COLUMN_PE3, Sole.COLUMN_PE4,
                        Sole.COLUMN_PE5, Sole.COLUMN_PE6, Sole.COLUMN_PE7, Sole.COLUMN_PE8,
                        Sole.COLUMN_PE9, Sole.COLUMN_PE10, Sole.COLUMN_PE11, Sole.COLUMN_PE12,
                        Sole.COLUMN_PE13, Sole.COLUMN_PE14, Sole.COLUMN_PE15, Sole.COLUMN_PE16, Sole.COLUMN_GTF},
                Sole.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Sole sole = null;

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                // prepare sole object
                sole = new Sole(
                        cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_USERID)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_R_L)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SYNCSTATUS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLXAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLYAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLZAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROROLL)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROPITCH)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROYAW)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNXAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNYAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNZAXIS)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE1)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE2)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE3)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE4)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE5)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE6)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE7)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE8)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE9)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE10)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE11)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE12)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE13)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE14)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE15)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE16)),
                        cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GTF))
                );

                // close the db connection
                cursor.close();

                if (sole.checkSoleValidity())
                {
                    return null;
                }

            }
        }

        return sole;
    }

    /**
     * Get Sole data from db between the starttime and stop of the test
     * @param startTimeStamp
     * @param stopTimestamp
     */
    public List<Sole> getSoleDatFromTest(String startTimeStamp, String stopTimestamp)
    {

        //TODO: Only for testing using insoletable.scv, remove for the live
        //yyyy-mm-dd hh:mm:ss[.fffffffff]
        List<Sole> soles = new ArrayList<>();


        // Select test sole data Query
        String selectQuery = "SELECT * " +
                "FROM " + Sole.TABLE_NAME +
                " WHERE " + Sole.COLUMN_TIMESTAMP +
                " BETWEEN " +
                "strftime('%Y-%m-%d %H:%M:%f', '" + startTimeStamp + "') AND " +
                "strftime('%Y-%m-%d %H:%M:%f', '" + stopTimestamp + "')";


        //String selectQuery = "SELECT  * FROM " +  Sole.TABLE_NAME +  " WHERE " + Sole.COLUMN_TIMESTAMP + " BETWEEN " + startTimeStamp + " AND " + stopTimestamp;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Sole sole = new Sole();
                sole.setId(cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)));
                sole.setUserid(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_USERID)));
                sole.setSole(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)));
                sole.setTimestamp(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)));
                sole.setRL(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_R_L)));
                sole.setSync(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SYNCSTATUS)));
                sole.setAccx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLXAXIS)));
                sole.setAccy(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLYAXIS)));
                sole.setAccz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLZAXIS)));
                sole.setGyropoll(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROROLL)));
                sole.setGyropitch(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROPITCH)));
                sole.setGyroyaw(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROYAW)));
                sole.setMagnx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNXAXIS)));
                sole.setMagny(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNYAXIS)));
                sole.setMagnz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNZAXIS)));
                sole.setPe1(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE1)));
                sole.setPe2(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE2)));
                sole.setPe3(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE3)));
                sole.setPe4(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE4)));
                sole.setPe5(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE5)));
                sole.setPe6(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE6)));
                sole.setPe7(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE7)));
                sole.setPe8(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE8)));
                sole.setPe9(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE9)));
                sole.setPe10(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE10)));
                sole.setPe11(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE11)));
                sole.setPe12(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE12)));
                sole.setPe13(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE13)));
                sole.setPe14(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE14)));
                sole.setPe15(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE15)));
                sole.setPe16(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE16)));
                sole.setGtf(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GTF)));

                // if valid values
                if (sole.checkSoleValidity())
                {
                    soles.add(sole);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        // close db connection
        db.close();

        // return soles list
        return soles;
    }

    /**
     * Get All soles in DB
     */
    public List<Sole> getAllSoles() {
        List<Sole> soles = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Sole.TABLE_NAME + " ORDER BY " +
                Sole.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery(selectQuery, null);
//        try {

            // looping through all rows and adding to list
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Sole sole = new Sole();
                    sole.setId(cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)));
                    sole.setUserid(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_USERID)));
                    sole.setSole(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)));
                    sole.setTimestamp(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)));
                    sole.setRL(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_R_L)));
                    sole.setSync(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SYNCSTATUS)));
                    sole.setAccx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLXAXIS)));
                    sole.setAccy(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLYAXIS)));
                    sole.setAccz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLZAXIS)));
                    sole.setGyropoll(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROROLL)));
                    sole.setGyropitch(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROPITCH)));
                    sole.setGyroyaw(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROYAW)));
                    sole.setMagnx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNXAXIS)));
                    sole.setMagny(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNYAXIS)));
                    sole.setMagnz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNZAXIS)));
                    sole.setPe1(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE1)));
                    sole.setPe2(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE2)));
                    sole.setPe3(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE3)));
                    sole.setPe4(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE4)));
                    sole.setPe5(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE5)));
                    sole.setPe6(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE6)));
                    sole.setPe7(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE7)));
                    sole.setPe8(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE8)));
                    sole.setPe9(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE9)));
                    sole.setPe10(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE10)));
                    sole.setPe11(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE11)));
                    sole.setPe12(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE12)));
                    sole.setPe13(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE13)));
                    sole.setPe14(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE14)));
                    sole.setPe15(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE15)));
                    sole.setPe16(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE16)));
                    sole.setGtf(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GTF)));

                    // if valid values
                    if (sole.checkSoleValidity())
                    {
                        soles.add(sole);
                    }

                } while (cursor.moveToNext());

                cursor.close();
            }

            // close db connection
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.e("Problem", e.getMessage());
//        } finally {
//            db.endTransaction();
//        }

        // return soles list
        return soles;
    }

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
        values.put(Sole.COLUMN_USERID, sole.getUserid());
        values.put(Sole.COLUMN_TIMESTAMP, sole.getTimestamp());
        values.put(Sole.COLUMN_R_L, sole.getRL());
        values.put(Sole.COLUMN_SYNCSTATUS, sole.getSync());
        values.put(Sole.COLUMN_ACCLXAXIS, sole.getAccx());
        values.put(Sole.COLUMN_ACCLYAXIS, sole.getAccy());
        values.put(Sole.COLUMN_ACCLZAXIS, sole.getAccz());
        values.put(Sole.COLUMN_GYROROLL, sole.getGyropoll());
        values.put(Sole.COLUMN_GYROPITCH, sole.getGyropitch());
        values.put(Sole.COLUMN_GYROYAW, sole.getGyroyaw());
        values.put(Sole.COLUMN_MAGNXAXIS, sole.getMagnx());
        values.put(Sole.COLUMN_MAGNYAXIS, sole.getMagny());
        values.put(Sole.COLUMN_MAGNZAXIS, sole.getMagnz());
        values.put(Sole.COLUMN_PE1, sole.getPe1());
        values.put(Sole.COLUMN_PE2, sole.getPe2());
        values.put(Sole.COLUMN_PE3, sole.getPe3());
        values.put(Sole.COLUMN_PE4, sole.getPe4());
        values.put(Sole.COLUMN_PE5, sole.getPe5());
        values.put(Sole.COLUMN_PE6, sole.getPe6());
        values.put(Sole.COLUMN_PE7, sole.getPe7());
        values.put(Sole.COLUMN_PE8, sole.getPe8());
        values.put(Sole.COLUMN_PE9, sole.getPe9());
        values.put(Sole.COLUMN_PE10, sole.getPe10());
        values.put(Sole.COLUMN_PE11, sole.getPe11());
        values.put(Sole.COLUMN_PE12, sole.getPe12());
        values.put(Sole.COLUMN_PE13, sole.getPe13());
        values.put(Sole.COLUMN_PE14, sole.getPe14());
        values.put(Sole.COLUMN_PE15, sole.getPe15());
        values.put(Sole.COLUMN_PE16, sole.getPe16());
        values.put(Sole.COLUMN_GTF, sole.getGtf());
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

    /**
     * Get data for a specific test
     * @param startTimestamp start timestamp from the test
     * @param stopTimestamp stop timestamp from the test
     * @param rl_sole Right OR Left test
     * @return Soles in a form of list with the specified data
     */
    public List<Sole> getSpecificTest(String startTimestamp, String stopTimestamp, String rl_sole){
        List<Sole> soles = new ArrayList<>();

        // Select Query
        String selectQuery = "SELECT * " +
                "FROM " + Sole.TABLE_NAME +
                " WHERE " + Sole.COLUMN_R_L + " = '" + rl_sole + "' AND " +
                Sole.COLUMN_TIMESTAMP + " BETWEEN " +
                "strftime('%Y-%m-%d %H:%M:%f', '" + startTimestamp + "') AND " +
                "strftime('%Y-%m-%d %H:%M:%f', '" + stopTimestamp + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Sole sole = new Sole();
                sole.setId(cursor.getInt(cursor.getColumnIndex(Sole.COLUMN_ID)));
                sole.setUserid(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_USERID)));
                sole.setSole(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SOLE)));
                sole.setTimestamp(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_TIMESTAMP)));
                sole.setRL(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_R_L)));
                sole.setSync(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_SYNCSTATUS)));
                sole.setAccx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLXAXIS)));
                sole.setAccy(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLYAXIS)));
                sole.setAccz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_ACCLZAXIS)));
                sole.setGyropoll(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROROLL)));
                sole.setGyropitch(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROPITCH)));
                sole.setGyroyaw(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GYROYAW)));
                sole.setMagnx(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNXAXIS)));
                sole.setMagny(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNYAXIS)));
                sole.setMagnz(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_MAGNZAXIS)));
                sole.setPe1(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE1)));
                sole.setPe2(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE2)));
                sole.setPe3(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE3)));
                sole.setPe4(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE4)));
                sole.setPe5(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE5)));
                sole.setPe6(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE6)));
                sole.setPe7(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE7)));
                sole.setPe8(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE8)));
                sole.setPe9(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE9)));
                sole.setPe10(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE10)));
                sole.setPe11(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE11)));
                sole.setPe12(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE12)));
                sole.setPe13(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE13)));
                sole.setPe14(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE14)));
                sole.setPe15(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE15)));
                sole.setPe16(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_PE16)));
                sole.setGtf(cursor.getString(cursor.getColumnIndex(Sole.COLUMN_GTF)));

                // if valid values
                if (sole.checkSoleValidity())
                {
                    soles.add(sole);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        // close db connection
        db.close();
        return soles;
    }

    /**
     * Delete test fom SQLite
     * @param startTimestamp
     * @param stopTimestamp
     */
    public void deleteSpecificTest(String startTimestamp, String stopTimestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Sole.TABLE_NAME, Sole.COLUMN_TIMESTAMP + " BETWEEN " +
                        "strftime('%Y-%m-%d %H:%M:%f', '" + startTimestamp + "') AND " +
                        "strftime('%Y-%m-%d %H:%M:%f', '" + stopTimestamp + "')",
                null);
        db.close();
    }

    /**
     *  CODE for TEST TABLE  =======================================================================
     */
    public long insertTestData() {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Test.COLUMN_STARTTIMESTAMP, "2018-02-21 00:15:42");
        values.put(Test.COLUMN_STOPTIMESTAMP, "2018-02-21 00:15:42");
        values.put(Test.COLUMN_TEST_TYPE, "TESTTYPE");
        values.put(Test.COLUMN_DURATION, "DURATION");
        values.put(Test.COLUMN_SYNCSTATUS, "SYNC");

        // insert row
        long id = db.insert(Test.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Test getTest(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Test.TABLE_NAME,
                new String[]{Test.COLUMN_ID, Test.COLUMN_STARTTIMESTAMP, Test.COLUMN_STOPTIMESTAMP,
                        Test.COLUMN_TEST_TYPE, Test.COLUMN_DURATION, Test.COLUMN_SYNCSTATUS,},
                Test.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare sole object
        Test test = new Test(
                cursor.getInt(cursor.getColumnIndex(Test.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Test.COLUMN_STARTTIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(Test.COLUMN_STOPTIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(Test.COLUMN_TEST_TYPE)),
                cursor.getString(cursor.getColumnIndex(Test.COLUMN_DURATION)),
                cursor.getString(cursor.getColumnIndex(Test.COLUMN_SYNCSTATUS))
        );

        // close the db connection
        cursor.close();

        return test;
    }

    public int getTestsCount() {
        String countQuery = "SELECT  * FROM " + Test.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public List<Test> getAllTests() {
        List<Test> tests = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Test.TABLE_NAME + " ORDER BY " +
                Test.COLUMN_STARTTIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(cursor.getColumnIndex(Test.COLUMN_ID)));
                test.setStartTimestamp(cursor.getString(cursor.getColumnIndex(Test.COLUMN_STARTTIMESTAMP)));
                test.setStopTimestamp(cursor.getString(cursor.getColumnIndex(Test.COLUMN_STOPTIMESTAMP)));
                test.setTestType(cursor.getString(cursor.getColumnIndex(Test.COLUMN_TEST_TYPE)));
                test.setDuration(cursor.getString(cursor.getColumnIndex(Test.COLUMN_DURATION)));
                test.setSync(cursor.getString(cursor.getColumnIndex(Test.COLUMN_SYNCSTATUS)));

                tests.add(test);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return soles list
        return tests;
    }

    /**
     * Update Test in DB
     * @param test new test
     * @return updating row
     */
    public int updateTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Test.COLUMN_STARTTIMESTAMP, test.getStartTimestamp());
        values.put(Test.COLUMN_STOPTIMESTAMP, test.getStopTimestamp());
        values.put(Test.COLUMN_TEST_TYPE, test.getTestType());
        values.put(Test.COLUMN_DURATION, test.getDuration());
        values.put(Test.COLUMN_SYNCSTATUS, test.getSync());
        // updating row
        return db.update(Test.TABLE_NAME, values, Test.COLUMN_ID + " = ?",
                new String[]{String.valueOf(test.getId())});
    }

    public void deleteTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Test.TABLE_NAME, Test.COLUMN_ID + " = ?",
                new String[]{String.valueOf(test.getId())});
        db.close();
    }
}