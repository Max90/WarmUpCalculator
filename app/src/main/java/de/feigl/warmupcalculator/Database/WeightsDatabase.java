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
import java.util.List;

public class WeightsDatabase extends SQLiteOpenHelper {

    public static final String TABLE_WEIGHTS = "weights";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WEIGHT = "weight";

    private static final String DATABASE_NAME = "weights.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_WEIGHTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_WEIGHT
            + " double not null);";

    public WeightsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WeightsDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        onCreate(db);
    }

    public void addWeight(Double weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);

        // Inserting Row
        db.insert(TABLE_WEIGHTS, null, values);
        db.close(); // Closing database connection
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