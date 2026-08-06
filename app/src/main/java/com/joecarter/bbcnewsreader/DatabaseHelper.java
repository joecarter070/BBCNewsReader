package com.joecarter.bbcnewsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bbcnews.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_FAVOURITES = "favourites";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LINK = "link";
    public static final String COL_DATE = "pubDate";
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_FAVOURITES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_LINK + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
    }

    public void insertFavourite(String title, String description, String link, String pubDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_LINK, link);
        values.put(COL_DATE, pubDate);

        db.insert(TABLE_FAVOURITES, null, values);
    }

    public ArrayList<Article> getAllFavourites() {
        ArrayList<Article> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Article a = new Article();
            a.title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            a.description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
            a.link = cursor.getString(cursor.getColumnIndexOrThrow(COL_LINK));
            a.pubDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
            list.add(a);
        }

        cursor.close();
        return list;
    }
    public void deleteFavourite(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, COL_TITLE + "=?", new String[]{title});
    }

}
