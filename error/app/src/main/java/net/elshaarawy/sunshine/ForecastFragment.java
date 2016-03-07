package net.elshaarawy.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==R.id.action_refresh)
        {
            FetchWeatherTask WT = new FetchWeatherTask();
            WT.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {String [] forecastArray = {"Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30",
            "Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30","Day - sunny - 25/30"};
        View V = inflater.inflate(R.layout.fragment_main,container,false);

        ArrayAdapter AD = new ArrayAdapter(getActivity(),R.layout.forecast_list_item,R.id.forecast_lis_item_textview,forecastArray);

        ListView LV = (ListView) V.findViewById(R.id.forecasr_listView);

        LV.setAdapter(AD);



        return V;
    }
    private class FetchWeatherTask extends AsyncTask<Void, Void, Void>
    {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=30.9733502&lon=31.1441323&cnt=7&mode=json&APPID=88d1c681c8761123026e5a34857459bd");

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
            return null;

        }
    }
}
