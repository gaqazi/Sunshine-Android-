package net.elshaarawy.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
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
}
