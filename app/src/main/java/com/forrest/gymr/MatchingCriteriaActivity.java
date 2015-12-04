package com.forrest.gymr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * The Activity that handles confirmation of matching Criteria
 */
public class MatchingCriteriaActivity extends Activity {

    private Spinner bodyPartSpinner;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_criteria);
        bodyPartSpinner = (Spinner) findViewById(R.id.body_part_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.body_parts_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyPartSpinner.setAdapter(adapter);

        confirmButton = (Button) findViewById(R.id.matching_confirm_btn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.getCurrentUser().put("pairedToday", new ArrayList<>());
                ParseUser.getCurrentUser().put("configuredDate", new Date());
                ParseUser.getCurrentUser().put("bodyPart", bodyPartSpinner.getSelectedItem().toString());
                try {
                    ParseUser.getCurrentUser().save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matching_criteria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
