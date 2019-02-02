package com.example.hashboard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import static com.example.hashboard.HttpHandler.HttpPost;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_MAIN = 1;

    private EditText mUsernameText;
    private EditText mPasswordText;
    private Button mLoginButton;

    private ProgressDialog mProgressDialog;
    private UserLoginTask mUserLoginTask;
    private HttpResponse mResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameText = findViewById(R.id.login_username);
        mPasswordText = findViewById(R.id.login_password);
        mLoginButton = findViewById(R.id.btn_login);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        findViewById(R.id.link_signup).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void startLogin() {

        if (!validateFields()) {
            return;
        }

        mLoginButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(this, null, "Authenticating...", true, true);

        mUserLoginTask = new UserLoginTask();
        mUserLoginTask.execute(getString(R.string.SIGN_IN));
    }

    private boolean validateFields() {

        if (mUsernameText.getText().toString().isEmpty()) {
            mUsernameText.setError("Who are you?");
            return false;
        } else {
            mUsernameText.setError(null);
        }

        if (mPasswordText.getText().toString().isEmpty()) {
            mPasswordText.setError("What's the password, outlander?");
            return false;
        } else {
            mPasswordText.setError(null);
        }

        return true;
    }

    private class UserLoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    mResponse = HttpPost(urls[0], buidJsonObject(),null);
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
            mUserLoginTask = null;
            mProgressDialog.dismiss();

            if (success) {
                mLoginButton.setEnabled(true);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                try{
                    intent.putExtra(getString(R.string.TOKEN), mResponse.getJSONObject().getString("token"));
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                startActivityForResult(intent, REQUEST_MAIN);
            } else {
                Toast.makeText(getBaseContext(), "You're not on the list, buddy", Toast.LENGTH_LONG).show();
                mLoginButton.setEnabled(true);
            }
        }

        private JSONObject buidJsonObject() throws JSONException {

            JSONObject userJsonObject = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            userJsonObject.accumulate("username", mUsernameText.getText().toString());
            userJsonObject.accumulate("password",  mPasswordText.getText().toString());
            jsonObject.accumulate("user", userJsonObject);
            return jsonObject;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.putExtra(getString(R.string.TOKEN), data.getStringExtra(getString(R.string.TOKEN)));
                startActivityForResult(intent, REQUEST_MAIN);
            }
        }
    }
}