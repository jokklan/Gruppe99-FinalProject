package com.example.jokklan.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.Settings.Secure;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONObject;

import java.util.HashMap;

public class LocationResultActivity extends AppCompatActivity {
    public static final String PLACE_EXTRA = "com.example.jokklan.finalproject.extra.place";
    private static String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_result);
        Log.d("LocationResultActivity", "I'm running");

        Intent intent = getIntent();
        this.place = intent.getStringExtra(PLACE_EXTRA);
        Log.d("LocationResultActivity", "place: " + place);

        TextView textView = (TextView) findViewById(R.id.locationResultMessage);
        textView.setText("Du er/har lige været her: " + place +". Har du købt noget? Hvis ja indskriv beløb nedenfor og send:");
    }


    public void sendResult(View button) {
        EditText purchaseText = (EditText) findViewById(R.id.purchase);
        TextView textView = (TextView) findViewById(R.id.locationResultMessage);
        String purchase = purchaseText.getText().toString();

        button.setVisibility(View.GONE);
        purchaseText.setVisibility(View.GONE);
        textView.setText("Submitting results, please wait...");

        sendDataToServer(purchase);
    }

    public void sendDataToServer(String purchase) {
        String url = "https://group-99-api.herokuapp.com/tests";
        HashMap<String, String> body = new HashMap<String, String>();

        String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        body.put("assignment", "3");
        body.put("user_id", android_id);
        body.put("place", place);
        body.put("purchase", purchase);

        JSONObject json = new JSONObject(body);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());
                        TextView textView = (TextView) findViewById(R.id.locationResultMessage);
                        textView.setText("Result submitted. Thanks for completing our test!");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSON", error.toString());
                    }
                });

        // Access the RequestQueue through the singleton NetworkService class
        NetworkService.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
