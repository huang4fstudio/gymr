package com.forrest.gymr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    private Button fbLoginBtn;
    private ProgressDialog loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            switchGymrActivity();
        }
        fbLoginBtn = (Button) findViewById(R.id.fb_login_btn);
        fbLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(v);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handles A Login Click on the Facebook Login Button
     * @param v Reference to the Login Button
     */
    public void onLoginClick(View v) {
        loginStatus = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                loginStatus.dismiss();
                // Sets a Toast to display the Result of a Login, Toast is a small pop up text box
                if (parseUser == null) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                } else if (parseUser.isNew()) {
                    Toast.makeText(getBaseContext(), R.string.login_new_user, Toast.LENGTH_LONG).show();
                    switchConfigProfileActivity();
                } else {

                    Toast.makeText(getBaseContext(), R.string.login_successful, Toast.LENGTH_LONG).show();
                    switchGymrActivity();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void switchMatchingActivity() {
        Intent i = new Intent(this, MatchingCriteriaActivity.class);
        startActivity(i);
    }

    /**
     * Swtich Directly to Gymr Activity, used when it is not the first time that the User start the app
     */
    private void switchGymrActivity() {
        Intent i = new Intent(this, GymrActivity.class);
        startActivity(i);
    }

    /**
     * Switch To ConfigProfileActivity, used to Configure User Profile in Gymr for the First time
     */

    private void switchConfigProfileActivity() {
        Intent i = new Intent(this, ConfigProfileActivity.class);
        startActivity(i);
    }

}
