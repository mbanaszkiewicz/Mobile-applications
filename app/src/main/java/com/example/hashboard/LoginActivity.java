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

import java.io.IOException;

import static com.example.hashboard.HttpHandler.HttpPost;


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
    HttpResponse response;

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

        if (!validateFields()) {
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
                    response = HttpPost(urls[0], buidJsonObject());
                    return response.isSuccesful();
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
                try{
                    intent.putExtra("token", response.getJSONObject().getString("token"));
                } catch(JSONException e) {
                    e.printStackTrace();
                }

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

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject userObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        userObject.accumulate("username", _usernameText.getText().toString());
        userObject.accumulate("password",  _passwordText.getText().toString());
        jsonObject.accumulate("user", userObject);
        return jsonObject;
    }

    private boolean validateFields() {
        if (_usernameText.getText().toString().isEmpty()) {
            _usernameText.setError("Enter an username.");
            return false;
        } else {
            _usernameText.setError(null);
        }
        if (_passwordText.getText().toString().isEmpty()) {
            _passwordText.setError("Enter a password.");
            return false;
        } else {
            _passwordText.setError(null);
        }
        return true;
    }
}