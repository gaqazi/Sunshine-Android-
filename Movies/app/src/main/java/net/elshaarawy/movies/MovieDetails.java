package net.elshaarawy.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetails extends AppCompatActivity {

    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIntent=getIntent();

        ImageView poster_imgv = (ImageView) findViewById(R.id.d_poster);
        TextView title_txv = (TextView) findViewById(R.id.d_title);
        TextView date_txv = (TextView) findViewById(R.id.d_date);
        RatingBar voteAverage_rb=(RatingBar) findViewById(R.id.d_ratingBar);
        TextView overview_txv=(TextView) findViewById(R.id.d_overview);

//        String url = mIntent.getStringExtra("imgURL");
//        Picasso.with(this.getApplicationContext()).load(url).
//                placeholder(R.drawable.loading).
//                error(R.drawable.noposter).
//                into(poster_imgv);
//
//        title_txv.setText(mIntent.getStringExtra("title"));
//        date_txv.setText(mIntent.getStringExtra("releaseDate"));
//        voteAverage_rb.setRating(Float.parseFloat(mIntent.getStringExtra("voteAverage")) / 2);
//
//        overview_txv.setText(mIntent.getStringExtra("overView"));

        String uri = mIntent.getData().toString();
        Toast.makeText(this,uri,Toast.LENGTH_LONG).show();


    }

    public Intent sendIntent(Context context,Uri uri){
        Intent mIntent = new Intent(context,MovieDetails.class);
        mIntent.setData(uri);
//        mIntent.putE
        return mIntent;

    }

}
