package de.feigl.warmupcalculator.Database;

/**
 * Created by max on 20.01.15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WeightsDatabase extends SQLiteOpenHelper {

    public static final String TABLE_WEIGHTS = "weights";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WEIGHT = "weight";

    public static final String TABLE_BAR = "bar_table";
    public static final String COLUMN_BAR_WEIGHT = "bar_weight";

    private static final String DATABASE_NAME = "weights.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_WEIGHTS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WEIGHT + " double not null);";

    private static final String CREATE_BAR_TABLE =
            "create table " + TABLE_BAR
                    + "(" + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_BAR_WEIGHT + " double default 20);";

    public WeightsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(CREATE_BAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WeightsDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        onCreate(db);
    }

    public void updateBarWeight(Double weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BAR_WEIGHT, weight);

        db.update(TABLE_BAR, values, COLUMN_ID + "=" + 1, null);
    }

    public void setBarWeight() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BAR_WEIGHT, 20.0);

        db.insert(TABLE_BAR, null, values);
    }

    public double getBarWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        Double weight;

        Cursor cursor = db.query(TABLE_BAR, new String[]{COLUMN_ID,
                        COLUMN_BAR_WEIGHT}, COLUMN_ID + "=?",
                new String[]{String.valueOf(1)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.getCount() != 0) {
            weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_BAR_WEIGHT));
        } else {
            setBarWeight();
            weight = 20.0;
        }

        return weight;
    }

    public void addWeight(Double weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (!weightExists(weight)) {
            values.put(COLUMN_WEIGHT, weight);
            // Inserting Row
            db.insert(TABLE_WEIGHTS, null, values);
        }
    }

    public Boolean weightExists(double weight) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEIGHTS, new String[]{COLUMN_ID,
                        COLUMN_WEIGHT}, COLUMN_WEIGHT + "=?",
                new String[]{String.valueOf(weight)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.getCount() != 0) {
            return true;
        }
        return false;
    }

    public ArrayList<Double> getAllWeights() {
        ArrayList<Double> weightsList = new ArrayList<Double>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEIGHTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                double weight = cursor.getDouble(cursor.getColumnIndex(COLUMN_WEIGHT));
                weightsList.add(weight);
            } while (cursor.moveToNext());
        }
//        db.close();
        Collections.sort(weightsList, new Comparator<Double>() {
            @Override
            public int compare(Double c1, Double c2) {
                return Double.compare(c2, c1);
            }
        });
        return weightsList;
    }

    public void deleteWeight(Double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_WEIGHT + " = ?",
                new String[]{String.valueOf(weight)});
//        db.close();
    }

}