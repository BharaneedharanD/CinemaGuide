package com.bharanee.android.cinemaguide.DatabasePackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bharanee.android.cinemaguide.DatabasePackage.FavoriteMovieContract;

public class MovieDBHelper extends SQLiteOpenHelper {
    private static int version=2;
    public static String DATABASE_NAME="FavoriteMovie.db";


    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
final String createQuery="CREATE TABLE "+ FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME
        +" ( "+ FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" INTEGER PRIMARY KEY , "
        + FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME+" TEXT NOT NULL ,"
        + FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH +" TEXT NOT NULL );";
db.execSQL(createQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(" DROP TABLE IF EXISTS "+ FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
    onCreate(db);
    }
}
