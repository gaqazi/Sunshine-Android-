package net.elshaarawy.movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.elshaarawy.movies.data.MoviesContract;

import java.util.concurrent.ExecutionException;


/**
 * Created by elshaarawy on 25-Apr-16.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Intent mIntent;
    TextView headingTV, date_TV, voteCount_TV, rating_TV, overview_TV;
    Button favorite_BTN, trailer_BTN;
    ImageView poster_imgV;
    public static ProgressBar progressBar;

    private String movieID;

    private FetchTrailer FT;

    private static final int MOVIE_DETAILS_LOADER_ID = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntent = getActivity().getIntent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_details, container, false);

        //text views
        headingTV = (TextView) view.findViewById(R.id.heading);
        date_TV = (TextView) view.findViewById(R.id.date);
        voteCount_TV = (TextView) view.findViewById(R.id.vote_count);
        rating_TV = (TextView) view.findViewById(R.id.rating);
        overview_TV = (TextView) view.findViewById(R.id.overview);
        //Buttons
        favorite_BTN = (Button) view.findViewById(R.id.favorite);
        trailer_BTN = (Button) view.findViewById(R.id.trailer);
        //image view
        poster_imgV = (ImageView) view.findViewById(R.id.poster);
        //progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progress);



        trailer_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FT= new FetchTrailer();
                FT.execute(movieID);
                Uri trailerURI=null;
                try {
                    trailerURI = FT.get()  ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                playTrailer(trailerURI);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setProgress(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mIntent == null) {
            return null;
        }
        return new CursorLoader(getContext(), mIntent.getData(), Utility.MOVIE_DETAILS_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }
        String heading, date, voteCount, rating, overview, posterURL;
        heading = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
        date = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE));
        voteCount = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT))+" votes";
        rating = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)) + "/10";
        overview = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW));
        posterURL = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMG_URL));
        movieID = data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID));

        headingTV.setText(heading);
        date_TV.setText(date.substring(0, 4));
        voteCount_TV.setText(voteCount);
        rating_TV.setText(rating);
        overview_TV.setText(overview);
        Picasso.with(getContext()).load(posterURL).
                placeholder(R.drawable.loading).
                error(R.drawable.noposter).
                into(poster_imgV);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public Intent sendIntent(Context context, Uri uri) {
        Intent mIntent = new Intent(context, MovieDetailsActivity.class);
        mIntent.setData(uri);
        return mIntent;
    }

    private void playTrailer (Uri uri){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
        trailerIntent.setData(uri);
        if (trailerIntent.resolveActivity(getActivity().getPackageManager())!=null){
            startActivity(trailerIntent);
        }
    }
}
