package info.smartinsole.sqlite.http_client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import info.smartinsole.sqlite.login.SharedPrefManager;
import info.smartinsole.sqlite.login.User;

public class PostJsonRequestQueue {

    private RequestQueue mQueue;
    private Context context;
    private String https_url;
    private final CountDownLatch countDownLatch;
    private User prefs;

    /**
     * Constructor
     * @param context MainActivity Context
     * @param https_url URL to POST data
     */
    public PostJsonRequestQueue(Context context, String https_url, CountDownLatch countDownLatch) {
        this.context = context;
        this.https_url = https_url;
        this.countDownLatch = countDownLatch;
    }

    /**
     * Make a POST Request using Volley
     * @param jsonPack Data in JSON format
     */
    public void postJSONRequest(JSONObject jsonPack, final VolleyCallback callback){
        mQueue = VolleySingleton.getInstance(context).getRequestQueue();
        prefs = SharedPrefManager.getInstance(context).getUser();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, https_url, jsonPack, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Response from POST
                    if (response != null) {
                        callback.onSuccess(response);
                    }
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("JSONObjectRequest: ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    VolleySingleton.getInstance(context).setError(true);
                    callback.onError(error.toString());
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("ERROR RESPONSE BAD: ", error.toString());
            }
        }) {

            @Override
            public int getMethod() {
                return Method.POST;
            }

            @Override
            public Priority getPriority() {
                return Priority.NORMAL;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = prefs.getToken();
                headers.put("Authorization", "Bearer "+ token);
                return headers;
            }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                String token = prefs.getJWToken();
//                //params.put("token", token);
//                params.put("Content-Type", "application/json");
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
        };
        jsonObjectRequest.setTag("POST");
        mQueue.add(jsonObjectRequest);
    }
}
