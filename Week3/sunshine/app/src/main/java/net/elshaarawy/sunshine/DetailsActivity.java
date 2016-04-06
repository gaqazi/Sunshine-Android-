package net.elshaarawy.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {

    Intent intent;

    String details;

    private ShareActionProvider SAP;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        intent = getIntent();

        details = intent.getStringExtra("Details");

        TextView details_tv = (TextView) findViewById(R.id.details_TextView);

        details_tv.setText(details);


    }

    private Intent share_intent (){

        Intent my_intent = new Intent(Intent.ACTION_SEND);

        my_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        my_intent.setType("text/plain");

        my_intent.putExtra(Intent.EXTRA_TEXT,details);

        return my_intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_details, menu);

        MenuItem my_item = menu.findItem(R.id.item_share);

        SAP = (ShareActionProvider) MenuItemCompat.getActionProvider(my_item);

        if (SAP!=null)
        SAP.setShareIntent(share_intent());else {Toast.makeText(this,"null",Toast.LENGTH_SHORT);}

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Toast.makeText(this, "pressed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            /*case R.id.item_share:
                startActivity(share_intent());
                return true;*/

        }
        return super.onOptionsItemSelected(item);
    }




}
