package com.forrest.gymr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;


public class ConfigProfileActivity extends Activity {

    private ParseUser currentUser;
    private String initName;
    private String initGender;
    private EditText editName;
    private EditText editWeight;
    private EditText editHeight;
    private EditText editBench;
    private EditText editSquat;
    private EditText editDeadlift;
    private RadioButton sameGender;
    private Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_profile);
        currentUser = ParseUser.getCurrentUser();
        editWeight = (EditText) findViewById(R.id.config_edit_weight);
        editHeight = (EditText) findViewById(R.id.config_edit_height);
        editName = (EditText) findViewById(R.id.config_edit_name);
        editBench = (EditText) findViewById(R.id.config_edit_bench);
        editSquat = (EditText) findViewById(R.id.config_edit_squat);
        editDeadlift = (EditText) findViewById(R.id.config_edit_deadlift);
        sameGender = (RadioButton) findViewById(R.id.config_gender_pref_radio);

        confirmButton = (Button) findViewById(R.id.config_confirm_btn);
        cancelButton = (Button) findViewById(R.id.config_cancel_btn);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmProfile();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLoginActivity();
            }
        });

        makeMeRequest();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_config_profile, menu);
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

    /**
     * Makes Request to Facebook API to fetch Name of User
     */
    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {

                            try {
                                currentUser.put("facebookId", jsonObject.getLong("id"));
                                initName = jsonObject.getString("name");

                                if (jsonObject.getString("gender") != null) {
                                    initGender = jsonObject.getString("gender");
                                };
                                // Update User Profile Config
                                updateProfileConfigFields();
                            } catch (JSONException e) {
                                Log.d(GymrApplication.TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(GymrApplication.TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(GymrApplication.TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(GymrApplication.TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,gender,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void updateProfileConfigFields() {
        editName.setText(initName);
    }

    /**
     * Validates the Values entered by the user to initialize the User Profile
     */
    private void confirmProfile() {

        if (editHeight.getText().length() == 0 || editWeight.getText().length() == 0 || editBench.getText().length() == 0 || editSquat.getText().length() == 0 || editDeadlift.getText().length() == 0) {
            Toast.makeText(getBaseContext(), R.string.config_error_string, Toast.LENGTH_LONG);
            return;
        }

        currentUser.put("name", editName.getText().toString());
        currentUser.put("height", Integer.parseInt(editHeight.getText().toString()));
        currentUser.put("weight", Integer.parseInt(editWeight.getText().toString()));
        currentUser.put("benchWeight", Integer.parseInt(editBench.getText().toString()));
        currentUser.put("squatWeight", Integer.parseInt(editSquat.getText().toString()));
        currentUser.put("deadliftWeight", Integer.parseInt(editDeadlift.getText().toString()));
        currentUser.put("oppositeSex", !sameGender.isSelected());

        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switchGymrActivity();
    }

    /**
     * Jump to main activity of Gymr
     */
    private void switchGymrActivity() {
        Intent i = new Intent(this, GymrActivity.class);
        startActivity(i);
    }

    /**
     * Return back to the Login Activity
     */
    private void switchLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
