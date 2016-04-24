package net.elshaarawy.movies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import net.elshaarawy.movies.data.MoviesContract.MoviesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by elshaarawy on 23-Apr-16.
 */
public class FetchMovies extends AsyncTask<String, Void, ArrayList<MovieEntity>> {

    private Context mContext;
    private String ORDER_TYPE;
    private Utility utility = new Utility();
    public FetchMovies( Context context,String order) {
        this.mContext  = context;
        this.ORDER_TYPE = order;
    }

    private ArrayList<MovieEntity> parsedData(String JSONString) throws JSONException {

        ArrayList<MovieEntity> data = new ArrayList<>();


        try {

            JSONObject serverResponse = new JSONObject(JSONString);

            JSONArray resultArray = serverResponse.getJSONArray("results");

            int resultArrayLength = resultArray.length();

            Log.v("hint", serverResponse.toString());
            Log.v("hint", resultArray.toString());

            int page = serverResponse.getInt("page");

            String mPosterURL, mOverview, mReleaseDate, mTitle, mVoteCount, mVoteAverage;

            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(resultArrayLength);

            for (int i = 0; i < resultArrayLength; i++) {

                JSONObject movie = resultArray.getJSONObject(i);

                MovieEntity ME;



                mPosterURL = "https://image.tmdb.org/t/p/w396/" + movie.getString("poster_path");

                mOverview = movie.getString("overview");
                mReleaseDate = movie.getString("release_date");
                mTitle = movie.getString("title");
                mVoteCount = movie.getString("vote_count");
                mVoteAverage = movie.getString("vote_average");

                ContentValues movieValues = new ContentValues();


                movieValues.put(MoviesEntry.COLUMN_PAGE,page);
                movieValues.put(MoviesEntry.COLUMN_IMG_URL,mPosterURL);
                movieValues.put(MoviesEntry.COLUMN_OVERVIEW,mOverview);
                movieValues.put(MoviesEntry.COLUMN_RELEASE_DATE,mReleaseDate);
                movieValues.put(MoviesEntry.COLUMN_TITLE,mTitle);
                movieValues.put(MoviesEntry.COLUMN_VOTE_COUNT,mVoteCount);
                movieValues.put(MoviesEntry.COLUMN_VOTE_AVERAGE,mVoteAverage);

                contentValuesVector.add(movieValues);


                Log.v("parsed", mPosterURL);

                Log.v("parsed", mTitle);


//                ME = new MovieEntity( m_id ,page, mPosterURL, mOverview, mReleaseDate, mTitle, mVoteCount, mVoteAverage);
//
//                data.add(ME);
            }

            if (contentValuesVector.size()>0){
                ContentValues [] contentValuesArray = new ContentValues[contentValuesVector.size()];

                contentValuesVector.toArray(contentValuesArray);
                int del ;

                if (utility.isPopular(ORDER_TYPE)){
                    del= mContext.getContentResolver().delete(MoviesEntry.CONTENT_URI_POPULAR, MoviesEntry._ID, null);
                    mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI_POPULAR, contentValuesArray);
                }
                else {
                    del= mContext.getContentResolver().delete(MoviesEntry.CONTENT_URI_RATED, MoviesEntry._ID, null);
                    mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI_RATED, contentValuesArray);
                }

                Log.v("DELETR", " delete count = " + del);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }


    @Override
    protected ArrayList<MovieEntity> doInBackground(String... params) {

        String moviesString = null;
        BufferedReader mBufferReader = null;
        HttpURLConnection mURLConnection = null;


        try {
            String my_key = "09531d99ec8e1c8dc6594e0b5928b2c6";

            final String baseURL = "https://api.themoviedb.org/3/movie/";

            final String page = "page";
            final String sort_by = "sort_by";
            final String api_key = "api_key";

            final String page_number = params[1].equals("0")?"1":params[1];

            String link = Uri.parse(baseURL).buildUpon().appendPath(params[0]).appendQueryParameter(page, page_number)
                    .appendQueryParameter(api_key, my_key).build().toString();

            URL url = new URL(link);

            Log.v("URL", url.toString());


            mURLConnection = (HttpURLConnection) url.openConnection();
            mURLConnection.setRequestMethod("GET");
            mURLConnection.connect();

            InputStream mInputStream = mURLConnection.getInputStream();

            mBufferReader = new BufferedReader(new InputStreamReader(mInputStream));

            StringBuffer mStringBuffer = new StringBuffer();

            String line;
            while ((line = mBufferReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                mStringBuffer.append(line + "\n");
            }

            moviesString = mStringBuffer.toString();

            Log.v("moh", moviesString);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (mURLConnection != null) {
                mURLConnection.disconnect();
            }
            if (mBufferReader != null) {

                try {
                    mBufferReader.close();
                } catch (final IOException e) {
                    Log.e("elshaarawy", "Error closing stream", e);
                }

            }
        }

        try {
            return parsedData(moviesString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
