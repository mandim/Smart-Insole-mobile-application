package info.smartinsole.sqlite.http_client;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.smartinsole.sqlite.login.LoginActivity;
import info.smartinsole.sqlite.login.SharedPrefManager;
import info.smartinsole.sqlite.login.User;

public class GetLogoutRequest {

    private RequestQueue mQueue;
    private Context context;
    private String https_url;
    private User prefs;

    public GetLogoutRequest(Context context, String https_url){
        this.context = context;
        this.https_url = https_url;
    }

    public void userLogout(){
        mQueue = VolleySingleton.getInstance(context).getRequestQueue();
        prefs = SharedPrefManager.getInstance(context).getUser();

        // Cancel pending requests
        mQueue.cancelAll("POST");

        // Request a string response from the provided URL.
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, https_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // remove user from Shared Preferences and go to Login Activity
                        SharedPrefManager.getInstance(context).logout();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = prefs.getToken();
                //String auth = "Bearer "
                //        + Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                //headers.put("Accept","application/json");
                headers.put("Authorization", "Bearer "+ token);
                return headers;
            }

        };

        mQueue.add(deleteRequest);
    }
}
