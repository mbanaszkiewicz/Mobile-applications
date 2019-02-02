package com.example.hashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.hashboard.HttpHandler.HttpPost;

public class SendActivity extends AppCompatActivity {

    private TextView mPostText;
    private ImageButton mSendButton;

    private ProgressDialog mProgressDialog;
    private PostSendTask mPostSendTask;
    private HttpResponse mResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendPost();
            }
        });

        mPostText = findViewById(R.id.edittext_post);
        mSendButton = findViewById(R.id.button_send);
    }

    private void sendPost() {

        if(!validateFields()){return;}

        mSendButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(this, null, "Sending...", true, true);

        mPostSendTask = new PostSendTask();
        mPostSendTask.execute(getString(R.string.POST));
    }

    private  boolean validateFields() {

        String postText = mPostText.getText().toString();
        if (postText.isEmpty()) {
            mPostText.setError("Speak your mind, bucko! ");
            return false;
        }
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(postText);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (count != 1) {
            mPostText.setError("One hashtag per post, hashtag army technology is not here.");
            return false;
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PostSendTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    mResponse = HttpPost(urls[0], buidJsonObject(), getIntent().getStringExtra(getString(R.string.TOKEN)));
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
            mPostSendTask = null;
            mProgressDialog.dismiss();

            if (success) {
                mSendButton.setEnabled(true);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Something went wrong :c.", Toast.LENGTH_LONG).show();
                mSendButton.setEnabled(true);
            }
        }

        private JSONObject buidJsonObject() throws JSONException {

            JSONObject postObject = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            postObject.accumulate("body",  mPostText.getText().toString());
            jsonObject.accumulate("post", postObject);
            return jsonObject;
        }
    }
}
