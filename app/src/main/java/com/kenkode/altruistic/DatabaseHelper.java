package com.kenkode.altruistic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "altruistic.db";
    public static final String TABLE_NAME = "altruistic_tbl";
    public static final String ID = "id";
    public static final String USERID = "user_id";
    public static final String BOARDINGID = "boarding_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USERID+" INTEGER, "+BOARDINGID+" INTEGER) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    public boolean insertData(int userId, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERID,userId);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else{
//            Toast.makeText(context, "Inserted!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean updateData(int userId, int boardingId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOARDINGID,boardingId);
        contentValues.put(USERID,userId);
        long result = db.update(TABLE_NAME,contentValues,"user_id = ?", new String[] {String.valueOf(userId)});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public int getUserID(){
        String selectQuery = "SELECT  * FROM "+TABLE_NAME+" ORDER BY "+ID+" DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        int userID = 0;
        if (cursor.moveToFirst()) {
            userID = cursor.getInt(1);
        }
        // close inserting data from database
        db.close();

        return userID;
    }

    public void deleteRecipe(int id) {
        // Delete Query by id

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] {String.valueOf(id)});
        // looping through all rows and adding to list

        // close inserting data from database
        db.close();
    }
}
