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
import java.io.IOException;
import static com.example.hashboard.HttpHandler.HttpPost;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

     EditText _usernameText;
     EditText _passwordText;
     EditText _confirmText;
     Button _signupButton;
     TextView _loginLink;

    ProgressDialog progressDialog;
    UserCreateTask userCreateTask;
    HttpResponse response;

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
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            return;
        }

        _signupButton.setEnabled(false);

        _signupButton.setEnabled(false);
        progressDialog = progressDialog.show(this, null, "Creating account...", true, true);

        userCreateTask = new UserCreateTask();
        userCreateTask.execute(getString(R.string.sign_up));
    }

    public boolean validate() {

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmation = _confirmText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("Username must be at least 3 characters long.");
            return false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("Password must be at least 4 characters long.");
            return false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmation.isEmpty() || !confirmation.equals(password)) {
            _confirmText.setError("Passwords are not equal.");
            return false;
        } else {
            _confirmText.setError(null);
        }
        return true;
    }

    private class UserCreateTask extends AsyncTask<String, Void, Boolean> {
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
            userCreateTask = null;
            progressDialog.dismiss();

            if (success) {
                Intent intent = new Intent();
                try{
                    intent.putExtra("token", response.getJSONObject().getString("token"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK, intent);
                _signupButton.setEnabled(true);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Sign up error.", Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
            }
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
    }

}