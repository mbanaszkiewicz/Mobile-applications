package com.example.hashboard;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;




public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

     EditText _usernameText;
     EditText _passwordText;
     EditText _confirmText;
     Button _signupButton;
     TextView _loginLink;

    ProgressDialog progressDialog;
    UserCreateTask userCreateTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _usernameText = findViewById(R.id.signup_username);
        _passwordText = findViewById(R.id.signup_password);
        _confirmText = findViewById(R.id.signup_confirm);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        _signupButton.setEnabled(false);
        progressDialog = progressDialog.show(this, null, "Creating account...", true, true);

        userCreateTask = new UserCreateTask();
        userCreateTask.execute(getString(R.string.sign_up));
    }

    private class UserCreateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    HttpPost(urls[0]);
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            userCreateTask = null;
            progressDialog.dismiss();

            if (success) {
                _signupButton.setEnabled(true);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Eror.", Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
            }
        }
    }
        private String HttpPost(String myUrl) throws IOException, JSONException {

            URL url = new URL(myUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject jsonObject = buidJsonObject();

            setPostRequestContent(conn, jsonObject);

            conn.connect();

            if(conn.getResponseCode()==201 || conn.getResponseCode()==200)
            {
                Intent intent = new Intent();
                intent.putExtra("token", InputParser.convertStreamToString(conn.getInputStream()));
                setResult(Activity.RESULT_OK, intent);
            }

            return conn.getResponseMessage()+"";

        }

        private JSONObject buidJsonObject() throws JSONException {

            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            userObject.accumulate("username", _usernameText.getText().toString());
            userObject.accumulate("password",  _passwordText.getText().toString());
            userObject.accumulate("password_confirmation",  _passwordText.getText().toString());
            jsonObject.accumulate("user", userObject);
            return jsonObject;
        }

        private void setPostRequestContent(HttpURLConnection conn,
                                           JSONObject jsonObject) throws IOException {

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            Log.i(MainActivity.class.toString(), jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }
    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;


        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirm = _confirmText.getText().toString();


        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("Username must be at least 3 characters long.");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("Password must be at least 4 characters long.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirm.isEmpty() || !confirm.equals(password)) {
            _confirmText.setError("Passwords are not equal.");
            valid = false;
        } else {
            _confirmText.setError(null);
        }

        return valid;
    }
}