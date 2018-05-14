package com.bharanee.android.cinemaguide;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
    private MovieDBHelper movieDBHelper;
    public static final int MOVIES=100;
    public static final int MOVIES_WITH_ID=101;
    public static final UriMatcher sUriMatcher=buildMatcher();
    public static UriMatcher buildMatcher(){
        UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.PATH_MOVIES,MOVIES);
        matcher.addURI(FavoriteMovieContract.AUTHORITY,FavoriteMovieContract.PATH_MOVIES+"/#",MOVIES_WITH_ID);
        return  matcher;
    }
    @Override
    public boolean onCreate() {
        Context context=getContext();
        movieDBHelper=new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase mDB= movieDBHelper.getReadableDatabase();
        int match=sUriMatcher.match(uri);
        Cursor cursor=null;

        switch (match){
            case MOVIES:
                cursor=mDB.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        null,
                        null,
                       null,
                        null,
                        null,
                        null);
                break;
            case MOVIES_WITH_ID:
                String movieid=uri.getPathSegments().get(1);
                cursor=mDB.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        new String[]{FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID},
                        FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" =? ",
                        new String[]{movieid},
                        null,
                        null,
                        null);

                break;
            default:
                throw new UnsupportedOperationException("Un supported operation");

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase mDb=movieDBHelper.getWritableDatabase();
        int match=sUriMatcher.match(uri);
        Uri returnUri=null;
        switch (match){
            case MOVIES:
                long query_id=mDb.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,null,values);
                if (query_id>0)
                    returnUri= ContentUris.withAppendedId(uri,query_id);
                else
                    throw new SQLiteException("Error inserting data in DataBase");
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase mDb=movieDBHelper.getWritableDatabase();
        int match=sUriMatcher.match(uri);
        String movieId= uri.getPathSegments().get(1);
        int result=-1;
        switch (match) {
            case MOVIES_WITH_ID:
                result=mDb.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" =?",
                        new String[]{movieId});
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
                return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
