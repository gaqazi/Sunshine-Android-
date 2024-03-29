package net.elshaarawy.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter AD;
    private View V;


    private boolean isOnline (){

        ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean status = CM.getActiveNetworkInfo()!= null;
        return status;
    }


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onStart() {
        super.onStart();



        if (isOnline()){
            updateWeather();

        }
        else {
            Snackbar.make(V,"check internet connection",Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                if (isOnline()){
                    updateWeather();

                }
                else {
                    Snackbar.make(V,"check internet connection",Snackbar.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;

            case R.id.action_on_map:

                gotoMap();
                return true;


        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ArrayList<String> forecasrlist = new ArrayList<>();
        V = inflater.inflate(R.layout.fragment_main, container, false);

        AD = new ArrayAdapter(getActivity(), R.layout.forecast_list_item, R.id.forecast_lis_item_textview, forecasrlist);

        ListView LV = (ListView) V.findViewById(R.id.forecasr_listView);


        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item_forecast = AD.getItem(position).toString();
                Toast.makeText(getActivity(), item_forecast, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), DetailsActivity.class);

                intent.putExtra("Details", item_forecast);

                startActivity(intent);

            }
        });

        LV.setAdapter(AD);


        return V;
    }

    private void updateWeather() {

        FetchWeatherTask WT = new FetchWeatherTask();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Location = SP.getString(getString(R.string.pref_edittext_key), getString(R.string.pref_edittext_value));
        String Unit = SP.getString(getString(R.string.pref_unit_key), getString(R.string.metric_value));
        //Toast.makeText(getActivity(),Location+" - "+Unit+" Unit",Toast.LENGTH_SHORT).show();
        Snackbar.make(V, Location + " - " + Unit + " Unit", Snackbar.LENGTH_SHORT).show();
        WT.execute(Location, Unit);
    }

    private void gotoMap(){

        Intent map_intent = new Intent(Intent.ACTION_VIEW);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String city_name = SP.getString(getString(R.string.pref_edittext_key), getString(R.string.pref_edittext_value));

        Uri city_uri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",city_name).build();
        map_intent.setData(city_uri);

        startActivity(map_intent);



    }



    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            //SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String MorI=sPref.getString(getString(R.string.pref_unit_key),getString(R.string.metric_value));

            //high = MorI.equals(getString(R.string.imperial_value))?(high*1.8)+32:high;
            //low = MorI.equals(getString(R.string.imperial_value))?(low*1.8)+32:low;

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " \n " + description + " \n " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v("elshaarawy", "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String json = "json";
            String metric = "metric";
            int week = 7;
            String ID = "88d1c681c8761123026e5a34857459bd";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String base_url = "http://api.openweathermap.org/data/2.5/forecast/daily?";

                final String q = "q";
                final String cnt = "cnt";
                final String mode = "mode";
                final String units = "units";
                final String APPID = "APPID";
                String URL = Uri.parse(base_url).buildUpon().appendQueryParameter(q, params[0]).appendQueryParameter(cnt, Integer.toString(week))
                        .appendQueryParameter(mode, json).appendQueryParameter(units, params[1]).appendQueryParameter(APPID, ID).build().toString();
                URL url = new URL(URL);
                Log.v("elshaarawy", "URL is :" + URL);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            try {
                return getWeatherDataFromJson(forecastJsonStr, week);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                AD.clear();//clear adapter before
                for (String i : strings) {
                    AD.add(i);
                }
            }
        }
    }
}
