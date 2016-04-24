package net.elshaarawy.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import net.elshaarawy.movies.data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View V;

    private SharedPreferences SP;
    private String ORDER_TYPE;
    private String PAGE_NUMBER;
    private GridCursorAdapter mGridCursorAdapter ;

    private GridView mGridView;

    Utility utility = new Utility();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ORDER_TYPE= SP.getString(getString(R.string.pref_ordering_key), getString(R.string.pref_popular_value));
        PAGE_NUMBER = SP.getString(getString(R.string.pref_page_key),getString(R.string.pref_page_dValue));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movies_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings:
                startActivity(new Intent(getActivity(),SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        V = inflater.inflate(R.layout.fragment_main, container, false);

        final Cursor cursor;

        cursor = utility.isPopular(ORDER_TYPE)?getActivity().getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI_POPULAR,null,null,null,null)
                :getActivity().getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI_RATED,null,null,null,null);


        mGridCursorAdapter= new GridCursorAdapter(getContext(),cursor,0);

        mGridView = (GridView) V.findViewById(R.id.movies_grid);

        mGridView.setAdapter(mGridCursorAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Cursor itemCursor = (Cursor) parent.getItemAtPosition(position);
                String _id = itemCursor.getString(itemCursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
                Intent mIntent = utility.isPopular(ORDER_TYPE)? new MovieDetails().sendIntent(getActivity(), MoviesContract.MoviesEntry.itemUri(_id,true))
                        :new MovieDetails().sendIntent(getActivity(),MoviesContract.MoviesEntry.itemUri(_id,false));
                startActivity(mIntent);
            }

        });
        return V;
    }



    @Override
    public void onStart() {

        super.onStart();
//        findMovies();
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
            FetchMovies FM = new FetchMovies(getContext(),ORDER_TYPE);


            FM.execute(ORDER_TYPE,PAGE_NUMBER);
        } else {
            Snackbar.make(V, "check your Internet Connection", Snackbar.LENGTH_SHORT).show();

        }

    }


}
