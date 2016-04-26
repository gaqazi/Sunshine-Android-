package net.elshaarawy.movies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import net.elshaarawy.movies.data.MoviesContract;

/**
 * Created by elshaarawy on 23-Apr-16.
 */
public class Utility {

    public static final String [] MOVIE_DETAILS_COLUMNS={

            MoviesContract.MoviesEntry.COLUMN_IMG_URL,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE

    };

    public static boolean isPopular(String orderType){
        boolean isPopular;
        switch (orderType){
            case "popular":
                isPopular = true;
                break;
            default:
            case "top_rated":
                isPopular=false;
                break;
        }
        return isPopular;
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConnectivityManager.getActiveNetworkInfo() != null;
    }


}
