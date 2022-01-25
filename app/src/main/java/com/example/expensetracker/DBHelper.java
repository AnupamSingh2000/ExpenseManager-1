package com.example.expensetracker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "expenses.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table expensetable(expense TEXT primary key, amount TEXT, date TEXT,location TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists expensetable");
    }

    public Boolean insertuserdata(String expense, String amount, String date,String location)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expense", expense);
        contentValues.put("amount", amount);
        contentValues.put("date", date);
        contentValues.put("location",location);
        long result=DB.insert("expensetable", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }


    public Boolean updateuserdata(String expense, String amount, String date,String location) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", amount);
        contentValues.put("date", date);
        contentValues.put("location",location);

        Cursor cursor = DB.rawQuery("Select * from expensetable where expense = ?", new String[]{expense});
        if (cursor.getCount() > 0) {
            long result = DB.update("expensetable", contentValues, "expense=?", new String[]{expense});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }}


    public Boolean deletedata (String expense)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from expensetable where expense = ?", new String[]{expense});
        if (cursor.getCount() > 0) {
            long result = DB.delete("expensetable", "expense=?", new String[]{expense});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from expensetable", null);
        return cursor;

    }
}
