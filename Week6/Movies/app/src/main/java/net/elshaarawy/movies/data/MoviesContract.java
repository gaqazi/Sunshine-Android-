package net.elshaarawy.movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by elshaarawy on 19-Apr-16.
 */
public class MoviesContract {

    // Content provider
    public static final String CONTENT_AUTHORITY = "net.elshaarawy.movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String POPULAR_BATH = "popular";

    public static final String RATED_BATH = "rated";

    public static final class MoviesEntry implements BaseColumns {

        // Database
        public static final String TABLE_NAME_POPULAR = "popular_movies";

        public static final String TABLE_NAME_RATED = "rated_movies";

        public static final String COLUMN_PAGE = "page";

        public static final String COLUMN_IMG_URL = "imgURL";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //Content Provider
        public static final Uri CONTENT_URI_POPULAR = BASE_CONTENT_URI.buildUpon().appendPath(POPULAR_BATH).build();

        public static final Uri CONTENT_URI_rated = BASE_CONTENT_URI.buildUpon().appendPath(RATED_BATH).build();

        //Resolved values
        public static final String POPULAR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+POPULAR_BATH;
        public static final String RATED_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+RATED_BATH;

        //attach _id to URIs

        public static Uri append_idToURI(Long _id , boolean isPopular){
            return isPopular? ContentUris.withAppendedId(CONTENT_URI_POPULAR,_id):
                    ContentUris.withAppendedId(CONTENT_URI_rated,_id);
        }





    }

}
