package com.example.jokklan.finalproject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkService {
    private static NetworkService networkServiceInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private NetworkService(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized NetworkService getInstance(Context context) {
        if (networkServiceInstance == null) {
            networkServiceInstance = new NetworkService(context);
        }
        return networkServiceInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
