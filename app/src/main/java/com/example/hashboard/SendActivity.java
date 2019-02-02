package com.example.hashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.hashboard.HttpHandler.HttpPost;

public class PostActivity extends AppCompatActivity {

    TextView _topicText;
    TextView _postText;
    ImageButton _sendButton;

    ProgressDialog progressDialog;
    MessagePostTask messagePostTask;
    HttpResponse response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendPost();
            }
        });

        _topicText = findViewById(R.id.edittext_topic);
        _postText = findViewById(R.id.edittext_post);
        _sendButton = findViewById(R.id.button_send);

    }

    private void sendPost() {

        if(!validateFields()){return;}

        _sendButton.setEnabled(false);
        progressDialog = progressDialog.show(this, null, "Sending...", true, true);

        messagePostTask= new MessagePostTask();
        messagePostTask.execute(getString(R.string.send));

    }

    private class MessagePostTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    response = HttpPost(urls[0], buidJsonObject(), getIntent().getStringExtra("token"));
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
            messagePostTask = null;
            progressDialog.dismiss();

            if (success) {
                _sendButton.setEnabled(true);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Something went wrong. :c.", Toast.LENGTH_LONG).show();
                _sendButton.setEnabled(true);
            }
        }
    }


    private JSONObject buidJsonObject() throws JSONException {

        JSONObject postObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        postObject.accumulate("topic", _topicText.getText().toString());
        postObject.accumulate("body",  _postText.getText().toString());
        jsonObject.accumulate("post", postObject);
        return jsonObject;
    }

    private  boolean validateFields() {

        if(_topicText.getText().toString().isEmpty()){
            _topicText.setError("Enter a topic");
            return false;
        }

        if(_postText.getText().toString().isEmpty()){
            _postText.setError("Enter a message");
            return false;
        }

        return true;
    }
}
