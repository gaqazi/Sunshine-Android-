package net.elshaarawy.movies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by elshaarawy on 24-Mar-16.
 */
public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MovieEntity> data;


    /* private int[] posters = {R.drawable.p01, R.drawable.p02, R.drawable.p03, R.drawable.p04,
             R.drawable.p05, R.drawable.p06, R.drawable.p07, R.drawable.p08,
             R.drawable.p09, R.drawable.p10, R.drawable.p11, R.drawable.p12};
 */
    public GridAdapter(Context mContext, ArrayList<MovieEntity> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MovieEntity getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class Holder {
        ImageView poster_img;
        TextView poster_txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View v = convertView;
        Holder holder = null;

        if (v == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = mInflater.inflate(R.layout.poster, parent, false);

            holder = new Holder();

            holder.poster_img = (ImageView) v.findViewById(R.id.poster_image_view);
            holder.poster_txt = (TextView) v.findViewById(R.id.poster_text_view);

            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        Picasso.with(mContext).load(data.get(position).getImgURL()).
                placeholder(R.drawable.loading).
                error(R.drawable.noposter).
                into(holder.poster_img);
        holder.poster_txt.setText(data.get(position).getTitle());

        Log.i("imageLink", data.get(position).getTitle() + " poster is:" + data.get(position).getImgURL());

        Log.i("rating",data.get(position).getTitle() + " rating is:" + data.get(position).getVoteAverage());

        return v;
    }


}
