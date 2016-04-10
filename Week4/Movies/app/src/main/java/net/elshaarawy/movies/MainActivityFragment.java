package net.elshaarawy.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View V;
    private GridAdapter mGridAdapter = null;

    private GridView mGridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        V = inflater.inflate(R.layout.fragment_main, container, false);



        mGridView = (GridView) V.findViewById(R.id.movies_grid);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieEntity mMovieEntity = (MovieEntity) mGridAdapter.getItem(position);

//                Toast.makeText(getContext(),mMovieEntity.getTitle(),Toast.LENGTH_LONG).show();

                Intent mIntent =new MovieDetails().sendIntent(getActivity(), mMovieEntity.getPage(), mMovieEntity.getImgURL(),
                        mMovieEntity.getOverView(), mMovieEntity.getReleaseDate(), mMovieEntity.getTitle(),
                        mMovieEntity.getVoteCount(), mMovieEntity.getVoteAverage());
                startActivity(mIntent);
            }

        });
        return V;
    }



    @Override
    public void onStart() {

        super.onStart();
        findMovies();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMovies();
            }
        });


    }

    private boolean isOnline() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConnectivityManager.getActiveNetworkInfo() != null;
    }

    private void findMovies() {

        if (isOnline()) {
            FindMovies FM = new FindMovies();


            FM.execute();
        } else {
            Snackbar.make(V, "check your Internet Connection", Snackbar.LENGTH_SHORT).show();

        }

    }

    public class FindMovies extends AsyncTask<Void, Void, ArrayList<MovieEntity>> {


        private ArrayList<MovieEntity> parsedData(String JSONString) throws JSONException {

            ArrayList<MovieEntity> data = new ArrayList<>();


            try {

                JSONObject serverResponse = new JSONObject(JSONString);

                JSONArray resultArray = serverResponse.getJSONArray("results");

                Log.v("hint", serverResponse.toString());
                Log.v("hint", resultArray.toString());

                int page = serverResponse.getInt("page");

                String mPosterURL, mOverview, mReleaseDate, mTitle, mVoteCount, mVoteAverage;

                for (int i = 0; i < resultArray.length(); i++) {

                    JSONObject movie = resultArray.getJSONObject(i);

                    MovieEntity ME;



                    mPosterURL = "https://image.tmdb.org/t/p/w396/" + movie.getString("poster_path");

                    mOverview = movie.getString("overview");
                    mReleaseDate = movie.getString("release_date");
                    mTitle = movie.getString("title");
                    mVoteCount = movie.getString("vote_count");
                    mVoteAverage = movie.getString("vote_average");

                    Log.v("parsed", mPosterURL);

                    Log.v("parsed", mTitle);


                    ME = new MovieEntity(page, mPosterURL, mOverview, mReleaseDate, mTitle, mVoteCount, mVoteAverage);

                    data.add(ME);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return data;
        }


        @Override
        protected ArrayList<MovieEntity> doInBackground(Void... params) {

            String moviesString = null;
            BufferedReader mBufferReader = null;
            HttpURLConnection mURLConnection = null;


            try {
                String my_key = "09531d99ec8e1c8dc6594e0b5928b2c6";

                final String baseURL = "https://api.themoviedb.org/3/discover/movie?";
                final String page = "page";
                final String sort_by = "sort_by";
                final String api_key = "api_key";

                String link = Uri.parse(baseURL).buildUpon().appendQueryParameter(page, "1")
                        .appendQueryParameter(sort_by, "popularity.desc")
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

        @Override
        protected void onPostExecute(ArrayList<MovieEntity> moviesList) {
            if (moviesList != null) {

                mGridAdapter = new GridAdapter(getContext(), moviesList);
                mGridView.setAdapter(mGridAdapter);

            }
        }

    }
}
