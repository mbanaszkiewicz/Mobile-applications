package com.example.hashboard;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmationText;
    private Button mSignupButton;
    private TextView mLoginLink;

    private ProgressDialog mProgressDialog;
    private UserCreateTask mUserCreateTask;
    private HttpResponse mResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameText = findViewById(R.id.signup_username);
        mPasswordText = findViewById(R.id.signup_password);
        mConfirmationText = findViewById(R.id.signup_confirm);
        mSignupButton = findViewById(R.id.btn_signup);
        mLoginLink = findViewById(R.id.link_login);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            return;
        }

        mSignupButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(this, null, "Creating account...", true, true);

        mUserCreateTask = new UserCreateTask();
        mUserCreateTask.execute(getString(R.string.SIGN_UP));
    }

    public boolean validate() {

        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String confirmation = mConfirmationText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            mUsernameText.setError("What is your name?");
            return false;
        } else {
            mUsernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            mPasswordText.setError("What is your quest?");
            return false;
        } else {
            mPasswordText.setError(null);
        }

        if (confirmation.isEmpty() || !confirmation.equals(password)) {
            mConfirmationText.setError("What is the airspeed velocity of an unladen swallow?");
            return false;
        } else {
            mConfirmationText.setError(null);
        }

        return true;
    }

    private class UserCreateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    mResponse = HttpPost(urls[0], buidJsonObject(), null);
                    return mResponse.isSuccesful();
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

            mUserCreateTask = null;
            mProgressDialog.dismiss();

            if (success) {
                Intent intent = new Intent();
                try{
                    intent.putExtra(getString(R.string.TOKEN), mResponse.getJSONObject().getString("token"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
                setResult(Activity.RESULT_OK, intent);
                mSignupButton.setEnabled(true);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "E44O4", Toast.LENGTH_LONG).show();
                mSignupButton.setEnabled(true);
            }
        }

        private JSONObject buidJsonObject() throws JSONException {

            JSONObject jsonObject = new JSONObject();
            JSONObject userJsonObject = new JSONObject();
            userJsonObject.accumulate("username", mUsernameText.getText().toString());
            userJsonObject.accumulate("password",  mPasswordText.getText().toString());
            userJsonObject.accumulate("password_confirmation",  mPasswordText.getText().toString());
            jsonObject.accumulate("user", userJsonObject);
            return jsonObject;
        }
    }

}