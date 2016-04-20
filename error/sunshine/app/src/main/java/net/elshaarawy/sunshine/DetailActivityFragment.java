package net.elshaarawy.sunshine;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



import net.elshaarawy.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    View V ;
    String details;

    private ShareActionProvider SAP;

    private static final int FORECAST_LOADER_ID =0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        V = inflater.inflate(R.layout.fragment_detail, container, false);
        return V;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);

        MenuItem my_item = menu.findItem(R.id.item_share);

        SAP = (ShareActionProvider) MenuItemCompat.getActionProvider(my_item);

        if (details!=null)
            SAP.setShareIntent(share_intent());else {Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT);}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Toast.makeText(getContext(), "pressed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            /*case R.id.item_share:
                startActivity(share_intent());
                return true;*/

        }
        return super.onOptionsItemSelected(item);
    }

    private Intent share_intent (){

        Intent my_intent = new Intent(Intent.ACTION_SEND);

        my_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        my_intent.setType("text/plain");

        my_intent.putExtra(Intent.EXTRA_TEXT, details);

        return my_intent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER_ID,null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent mIntent  = getActivity().getIntent();
        if(mIntent==null)
            return null;
        Toast.makeText(getContext(),"onCreatLoader",Toast.LENGTH_LONG).show();
        return new CursorLoader(getContext(),mIntent.getData(),FORECAST_COLUMNS,null,null,null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()){return;}

        String Date = Utility.formatDate(ForecastFragment.COL_WEATHER_DATE);
        String desc = data.getString(ForecastFragment.COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getContext());
        String high = Utility.formatTemperature(ForecastFragment.COL_WEATHER_MAX_TEMP, isMetric);
        String low = Utility.formatTemperature(ForecastFragment.COL_WEATHER_MIN_TEMP,isMetric);

        details = Date+"-"+desc+"-"+high+"/"+low;
        TextView details_tv = (TextView) V.findViewById(R.id.details_TextView);

        details_tv.setText(details+"235");

        if (SAP!=null)
            SAP.setShareIntent(share_intent());else {
            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT);}
        Toast.makeText(getContext(),"onLoadFinished",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
