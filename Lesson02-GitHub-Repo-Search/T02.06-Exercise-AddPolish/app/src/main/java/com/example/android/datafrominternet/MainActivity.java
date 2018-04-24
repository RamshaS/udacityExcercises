/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;
    // COMPLETED (12) Create a variable to store a reference to the error message TextView
    private TextView tv_err_message;
    // COMPLETED (24) Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar pb_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String weatherJsonString = "{\n" +
                "   \"temp\": {\n" +
                "      \"min\":11.34,\n" +
                "      \"max\":19.01\n" +
                "   },\n" +
                "   \"weather\": {\n" +
                "      \"id\":801,\n" +
                "      \"condition\":\"Clouds\",\n" +
                "      \"description\":\"few clouds\"\n" +
                "   },\n" +
                "   \"pressure\":1023.51,\n" +
                "   \"humidity\":87\n" +
                "}";
        retrieveWeatherCondition(weatherJsonString);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);

        // COMPLETED (13) Get a reference to the error TextView using findViewById
tv_err_message = (TextView) findViewById(R.id.tv_error_message_display);
pb_loading = (ProgressBar) findViewById(R.id.pd_loading_indicator);
        // COMPLETED (25) Get a reference to the ProgressBar using findViewById
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
        new GithubQueryTask().execute(githubSearchUrl);
    }

    // COMPLETED (14) Create a method called showJsonDataView to show the data and hide the error
    public void showJsonDataView(){
        mSearchResultsTextView.setVisibility(View.VISIBLE);
        tv_err_message.setVisibility(View.GONE);
    }


    // COMPLETED (15) Create a method called showErrorMessage to show the error and hide the data
    public void showErrorMessage(){
        mSearchResultsTextView.setVisibility(View.GONE);
        tv_err_message.setVisibility(View.VISIBLE);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible


        @Override
        protected void onPreExecute() {
            pb_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            pb_loading.setVisibility(View.GONE);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(githubSearchResults);
            }else{
                showErrorMessage();
            }
            // TODO (16) Call showErrorMessage if the result is null in onPostExecute

        }
    }
    public String retrieveWeatherCondition(String jsonString){

        try {
            JSONObject forecast = new JSONObject(jsonString);
            JSONObject  weather = forecast.getJSONObject("weather");
            String condition = weather.getString("condition");
            return condition;
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
