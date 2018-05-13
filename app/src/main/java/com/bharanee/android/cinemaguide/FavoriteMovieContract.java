package com.bharanee.android.cinemaguide;

import android.net.Uri;
import android.provider.BaseColumns;

public  class FavoriteMovieContract {
    public static final String AUTHORITY="com.bharanee.android.cinemaguide";
    public static final String SCHEME="content://";
    public static final Uri BASE_CONTENT_URI=Uri.parse(SCHEME+AUTHORITY);
    public static final String PATH_MOVIES="FavoriteMovie";

    public static class FavoriteMovieEntry implements BaseColumns{
        public static String TABLE_NAME="FavoriteMovie";
        public static String COLUMN_MOVIE_ID="MovieId";
        public static String COLUMN_MOVIE_NAME="MovieName";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

    }
}
