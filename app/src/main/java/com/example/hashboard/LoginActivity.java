package com.example.hashboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;




public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_MAIN = 1;


     EditText _usernameText;
     EditText _passwordText;
     Button _loginButton;
     TextView _signupLink;

    ProgressDialog progressDialog;
    UserLoginTask userLoginTask;
    String token = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _usernameText = findViewById(R.id.login_username);
        _passwordText = findViewById(R.id.login_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }

        _loginButton.setEnabled(false);
        progressDialog = progressDialog.show(this, null, "Authenticating...", true, true);

        userLoginTask = new UserLoginTask();
        userLoginTask.execute(getString(R.string.sign_in));
    }


    private class UserLoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    String result = HttpPost(urls[0]);
                    return !result.equals("Unauthorized");
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
            userLoginTask = null;
            progressDialog.dismiss();

            if (success) {
                _loginButton.setEnabled(true);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("token", token);
                startActivityForResult(intent, REQUEST_MAIN);
            } else {
                Toast.makeText(getBaseContext(), "Invalid credentials.", Toast.LENGTH_LONG).show();
                _loginButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("token", data.getStringExtra("token"));
                startActivityForResult(intent, REQUEST_MAIN);
            }
        }
    }


    private String HttpPost(String myUrl) throws IOException, JSONException {

        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        JSONObject jsonObject = buidJsonObject();

        conn.connect();
        setPostRequestContent(conn, jsonObject);

        if(conn.getResponseCode()==201 || conn.getResponseCode()==200)
        {
            JSONObject response = new JSONObject(InputParser.convertStreamToString(conn.getInputStream()));
            token = response.getString("token");
        }

        return conn.getResponseMessage()+"";

    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject userObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        userObject.accumulate("username", _usernameText.getText().toString());
        userObject.accumulate("password",  _passwordText.getText().toString());
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


    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter an username.");
            valid = false;
        } else {
            _usernameText.setError(null);
        }
        if (password.isEmpty()) {
            _passwordText.setError("Enter a password.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}