package net.elshaarawy.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View V;

    private SharedPreferences SP;
    private String ORDER_TYPE;
    private String PAGE_NUMBER;
    private static final int LOADER_ID = 1;
    private GridCursorAdapter mGridCursorAdapter;

    private static boolean FAVORITE_FLAG;
    private static String activityTitle;
    FetchMovies FM;

    private GridView mGridView;

    Utility utility = new Utility();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movies_menu, menu);

        MenuItem item = menu.findItem(R.id.action_favorite);

        if (FAVORITE_FLAG) {
            item.setIcon(android.support.design.R.drawable.abc_btn_rating_star_on_mtrl_alpha);

        } else {
            item.setIcon(android.support.design.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.action_favorite: {
                SP.edit().putString(getString(R.string.pref_favorite_key), FAVORITE_FLAG?"false":"true").commit();
                if (FAVORITE_FLAG) {
                    item.setIcon(android.support.design.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                } else {
                    item.setIcon(android.support.design.R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                }
                onStart();
                getActivity().setTitle(activityTitle);
                return true;
            }


        }

        return super.onOptionsItemSelected(item);

    }

    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        V = inflater.inflate(R.layout.fragment_main, container, false);

        SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ORDER_TYPE = SP.getString(getString(R.string.pref_ordering_key), getString(R.string.pref_popular_value));
        PAGE_NUMBER = SP.getString(getString(R.string.pref_page_key), getString(R.string.pref_page_dValue));

        mGridCursorAdapter = new GridCursorAdapter(getContext(), null, 0);


        mGridView = (GridView) V.findViewById(R.id.movies_grid);

        mGridView.setAdapter(mGridCursorAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Cursor itemCursor = (Cursor) parent.getItemAtPosition(position);
                String _id = itemCursor.getString(itemCursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
                String ID = itemCursor.getString(itemCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID));
                Intent mIntent;
                if (!FAVORITE_FLAG) {
                    mIntent = utility.isPopular(ORDER_TYPE) ? new MovieDetailsFragment().sendIntent(getActivity(), MoviesContract.MoviesEntry.itemUri(_id, true), ID)
                            : new MovieDetailsFragment().sendIntent(getActivity(), MoviesContract.MoviesEntry.itemUri(_id, false), ID);
                } else {
                    mIntent = new MovieDetailsFragment().sendIntent(getActivity(), Uri.withAppendedPath(MoviesContract.MoviesEntry.CONTENT_URI_FAVORITE, ID), ID);
                }
                startActivity(mIntent);
            }

        });
        return V;
    }


    @Override
    public void onStart() {

        super.onStart();
        ORDER_TYPE = SP.getString(getString(R.string.pref_ordering_key), getString(R.string.pref_popular_value));
        PAGE_NUMBER = SP.getString(getString(R.string.pref_page_key), getString(R.string.pref_page_dValue));
        //SPCL stands for shared Preference Change Listener
        SharedPreferences.OnSharedPreferenceChangeListener orderingSPCL, pageSPCL, favoriteSPCL;
        orderingSPCL = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                getLoaderManager().restartLoader(LOADER_ID, null, MainActivityFragment.this);
            }
        };

        pageSPCL = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                findMovies();
            }
        };
        favoriteSPCL = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                 String s= sharedPreferences.getString(getString(R.string.pref_favorite_key),
                        "false");
                FAVORITE_FLAG = s.equals("true")?true:false;
                getLoaderManager().restartLoader(LOADER_ID, null, MainActivityFragment.this);

            }
        };
        orderingSPCL.onSharedPreferenceChanged(SP, getString(R.string.pref_ordering_key));
        pageSPCL.onSharedPreferenceChanged(SP, getString(R.string.pref_page_key));
        favoriteSPCL.onSharedPreferenceChanged(SP, getString(R.string.pref_favorite_key));
        String s= SP.getString(getString(R.string.pref_favorite_key),
                "false");
        FAVORITE_FLAG = s.equals("true")?true:false;

        if (!FAVORITE_FLAG){
            activityTitle = Utility.isPopular(ORDER_TYPE) ? "Popular Movies" : "Rated Movies";
        }else {
            activityTitle = "My Favorite";
        }

        getActivity().setTitle(activityTitle);

//        findMovies();
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMovies();
            }
        });
    }


    private void findMovies() {


        if (Utility.isOnline(getActivity())) {
            FM = new FetchMovies(getContext(), ORDER_TYPE);


            FM.execute(ORDER_TYPE, PAGE_NUMBER);
        } else {
            Snackbar.make(V, "check your Internet Connection", Snackbar.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;
        if (!FAVORITE_FLAG) {
            Boolean flag = utility.isPopular(ORDER_TYPE);
            cursorLoader = flag ? new CursorLoader(getContext(), MoviesContract.MoviesEntry.CONTENT_URI_POPULAR, null, null, null, null)
                    : new CursorLoader(getContext(), MoviesContract.MoviesEntry.CONTENT_URI_RATED, null, null, null, null);
            activityTitle = flag ? "Popular Movies" : "Rated Movies";
        } else {
            cursorLoader = new CursorLoader(getContext(), MoviesContract.MoviesEntry.CONTENT_URI_FAVORITE, null, null, null, null);
            activityTitle = "My Favorite";
        }


        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGridCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridCursorAdapter.swapCursor(null);
    }
}
