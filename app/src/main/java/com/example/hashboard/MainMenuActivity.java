package com.example.hashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.hashboard.HttpHandler.HttpGet;

public class MainMenuActivity extends AppCompatActivity {

    private EditText mTopic;
    private Button mGetTopicPostsButton;
    private Button mGetUserPostsButton;
    private Button mSendButton;

    private ProgressDialog mProgressDialog;
    private TopicPostTask mTopicPostTask;
    private HttpResponse mResponse;
    private String mToken;
    private boolean isUserPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToken =  getIntent().getStringExtra(getString(R.string.TOKEN));
        mTopic = findViewById(R.id.edittext_topic);
        mGetTopicPostsButton = findViewById(R.id.button_getTopicPosts);
        mGetUserPostsButton = findViewById(R.id.button_getUserPosts);
        mSendButton = findViewById(R.id.button_post);

        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SendActivity.class);
                intent.putExtra(getString(R.string.TOKEN), mToken);
                startActivity(intent);
            }
        });

        mGetTopicPostsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getTopicsPosts();

            }
        });

        mGetUserPostsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getUserPosts();
            }
        });
    }

    private void getTopicsPosts() {

        isUserPosts = false;
        if(!validateFields()){
            return;
        }
        mGetTopicPostsButton.setEnabled(false);
        mGetUserPostsButton.setEnabled(false);
        mSendButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(this, null, "Fetching...", true, true);

        mTopicPostTask = new TopicPostTask();
        mTopicPostTask.execute(getString(R.string.POST )+ "/" + mTopic.getText().toString());
    }

    private void getUserPosts() {

        isUserPosts = true;
        mGetTopicPostsButton.setEnabled(false);
        mGetUserPostsButton.setEnabled(false);
        mSendButton.setEnabled(false);
        mProgressDialog = ProgressDialog.show(this, null, "Fetching...", true, true);

        mTopicPostTask = new TopicPostTask();
        mTopicPostTask.execute(getString(R.string.POST));
    }

    private boolean validateFields() {

        if (mTopic.getText().toString().isEmpty()) {
            mTopic.setError("What you wanna read about?");
            return false;
        } else {
            mTopic.setError(null);

            return true;
        }
    }


    private class TopicPostTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                try {
                    mResponse = HttpGet(urls[0], mToken);
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
            mTopicPostTask = null;
            mProgressDialog.dismiss();
            if(!success){
                Toast.makeText(getBaseContext(), "These are not the posts you are looking for.", Toast.LENGTH_LONG).show();
                mGetTopicPostsButton.setEnabled(true);
                mGetUserPostsButton.setEnabled(true);
                mSendButton.setEnabled(true);
            }else {
                mGetTopicPostsButton.setEnabled(true);
                mGetUserPostsButton.setEnabled(true);
                mSendButton.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), PostsActivity.class);

                String[] dataset;
                if(isUserPosts){
                    dataset = parseUserData();
                }else{
                    dataset = parseTopicData();
                }
                if(dataset == null){
                    Toast.makeText(getBaseContext(), "These are not the posts you are looking for.", Toast.LENGTH_LONG).show();
                    mGetTopicPostsButton.setEnabled(true);
                    mGetUserPostsButton.setEnabled(true);
                    mSendButton.setEnabled(true);
                }
                intent.putExtra("dataset", dataset);
                intent.putExtra(getString(R.string.TOKEN), mToken);
                startActivity(intent);
            }

        }

        private String[] parseTopicData() {
            JSONArray posts ;
            int n_posts;
            try{
                posts =  mResponse.getJSONObject().getJSONObject("topic").getJSONArray("posts");
                n_posts = mResponse.getJSONObject().getJSONObject("topic").getInt("n_posts");
            } catch (JSONException e){
                return null;
            }
            String[] dataset = new String[n_posts];

            for(int i = 0; i < n_posts; i++){
                try{
                    dataset[i] = posts.getJSONObject(i).getString("body");
                }catch (JSONException e){
                    return null;
                }
            }
            return dataset;
        }

        private String[] parseUserData() {
            JSONArray topics;
            String post ;
            ArrayList<String> datasetList = new ArrayList<>();

            try{
                topics = mResponse.getJSONObject().getJSONArray("topics");
            } catch (JSONException e){
                return null;
            }
            for(int k = 0; k < topics.length(); k++){
                try{
                    post =  topics.getJSONObject(k).getJSONObject("latest_post").getString("body");
                } catch (JSONException e){
                    return null;
                }
                datasetList.add(post);
                }
            return  GetStringArray(datasetList);
        }

        private  String[] GetStringArray(ArrayList<String> arr)
        {
            String str[] = new String[arr.size()];
            for (int j = 0; j < arr.size(); j++) {
                str[j] = arr.get(j);
            }
            return str;
        }
    }
}
