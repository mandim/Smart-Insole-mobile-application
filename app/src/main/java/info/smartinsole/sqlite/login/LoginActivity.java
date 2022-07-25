package info.smartinsole.sqlite.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.http_client.VolleySingleton;
import info.smartinsole.sqlite.view.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextInputLayout emailError, passError;
    private TextView pass_frg;
    private String login_url, register_patient_url, patient_id_url, register_sole_url, pair_patient_sole_url;
    private User prefs;
    private String tempPatID, tempSoleID;
    private User user;
    private String testId = "3999991431";

    private SharedPreferences username;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //construct urls
        initUrls();


        // If user already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // check if token has expired
//            if (!(new UserHelper(this)).hasExpired()){
//                finish();
//                startActivity(new Intent(this, MainActivity.class));
//            }
        }

        username = this.getPreferences(Context.MODE_PRIVATE);
        editor = username.edit();

        Button login;

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);
        pass_frg = (TextView) findViewById(R.id.passRecover);

        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();

                // TODO Remove it - for testing purpose
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                finish();
//                startActivity(intent);
            }
        });

        email.setText(username.getString("email", ""));

        //Application Permissions check
        checkPermission();

        // Login if Enter key pressed on keyboard
        email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    SetValidation();
                    return true;
                }
                return false;
            }
        });
        // Login if Enter key pressed on keyboard
        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    SetValidation();
                    return true;
                }
                return false;
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        pass_frg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Please contact an authorized personnel.")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do nothing
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.setTitle("Password Recovery");
                builder.show();
            }
        });
    }

    private String initSoleID() {

        //TODO: uncomment
        //long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        //tempSoleID = String.valueOf(number);

        //For testing
        tempSoleID = testId;

        return tempSoleID;
    }

    private String initPatientID() {
        //TODO: uncomment
//        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
//        tempPatID = String.valueOf(number);

        //For testing
        tempPatID = testId;

        return tempPatID;

    }

    private void initUrls() {
        login_url = getResources().getString(R.string.base_url) + getResources().getString(R.string.login_url);
        register_patient_url = getResources().getString(R.string.base_url) + getResources().getString(R.string.register_patient_url);
        register_sole_url = getResources().getString(R.string.base_url) + getResources().getString(R.string.register_sole_url);
        patient_id_url = getResources().getString(R.string.base_url) + "?api/patients/" + initPatientID() + "/api-key";
        pair_patient_sole_url = getResources().getString(R.string.base_url) + getResources().getString(R.string.pair_patient_sole_url);

    }

    /**
     * Email and Password Validator
     */
    public void SetValidation() {
        boolean isEmailValid, isPasswordValid;
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
//            emailError.setError(getResources().getString(R.string.error_invalid_email));
//            isEmailValid = false;
        } else if (email.getText().length() < 4) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            // if internet connection exist
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                // userLogin();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Login and retrieve token
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    public void userLogin() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objson = new JSONObject(response);
                    // get new response
                    prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                    if (objson.has("accessToken")) {
                        //creating a new user object
                        user = new User(1,
                                email.getText().toString(),
                                "",
                                objson.getString("accessToken"),
                                "",
                                objson.getString("refreshToken"),
                                "");
                        //store the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        // Save Username
                        editor.putString("email", email.getText().toString());
                        editor.apply();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", objson.getString("accessToken"));

                        editor.apply();

//                        //Create and assign patientId to user
//                        if(prefs.getPatientID() == null){
//                            //Get patientID
                        userRegister();
//                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * Register patient and get apiKey
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */

    public void userRegister() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, register_patient_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                    // get new response
                    JSONObject objson = new JSONObject(response);
                    if (objson.has("id")) {

                        //update the user object
                        prefs.setPatientID(objson.getString("id"));
                        prefs.setApikey(objson.getString("apiKey"));

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("apiKey", objson.getString("apiKey"));
                        editor.putString("id", objson.getString("id"));
                        editor.apply();


                        user.setApikey(objson.getString("apiKey"));
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        editor.apply();

                        soleRegister();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                if (statusCode.equals("401")) {
                    //refreshToken();
                }

                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");


                } catch (UnsupportedEncodingException e) {
                    // exception
                }

                //do stuff with the body...
            }


        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                prefs.setPatientID(tempPatID);

                editor.apply();
                params.put("patientId", tempPatID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                String token = prefs.getToken();
                headers.put("x-token", token);
                return headers;
            }
        };
        mQueue.add(request);
    }

    /**
     * Register insole
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    public void soleRegister() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, register_sole_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                    // get new response
                    JSONObject objson = new JSONObject(response);
                    if (objson.has("id")) {

                        pairPatientSole();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                if (statusCode.equals("401")) {
                    //refreshToken();
                }

                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");


                } catch (UnsupportedEncodingException e) {
                    // exception
                }

                //do stuff with the body...
            }


        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


//                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
//                prefs.setPatientID(tempPatID);
//
//                editor.apply();


                params.put("insoleId", initSoleID());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                String token = prefs.getToken();
                headers.put("x-token", token);
                return headers;
            }
        };
        mQueue.add(request);
    }

    /**
     * Pair patient with sole
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    public void pairPatientSole() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.POST, pair_patient_sole_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                    // get new response
                    JSONObject objson = new JSONObject(response);
                    if (objson.has("patientId")) {


                        //getPatientID();
                        // Start Main Activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(intent);


                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                if (statusCode.equals("401")) {
                    //refreshToken();
                }

                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");


                } catch (UnsupportedEncodingException e) {
                    // exception
                }

                //do stuff with the body...
            }


        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("patientId", tempPatID);
                editor.putString("insoleId", tempSoleID);
                editor.apply();


                params.put("patientId", tempPatID);
                params.put("insoleId", tempSoleID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                String token = prefs.getToken();
                headers.put("x-token", token);
                return headers;
            }
        };
        mQueue.add(request);
    }

    /**
     * Retrieve patient api key
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    public void getPatientID() {
        RequestQueue mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();


        StringRequest request = new StringRequest(Request.Method.GET, patient_id_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                try {
                    // get new response
                    JSONObject objson = new JSONObject(response);
                    if (objson.has("id")) {

                        //update the user object
                        prefs.setPatientID(objson.getString("id"));
                        prefs.setApikey(objson.getString("apiKey"));

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("apiKey", objson.getString("apiKey"));

                        editor.putString("id", objson.getString("id"));
                        editor.apply();


                        user.setApikey(objson.getString("apiKey"));
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        editor.apply();

                        // Start Main Activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                if (statusCode.equals("401")) {
                    refreshToken();
                }

                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");


                } catch (UnsupportedEncodingException e) {
                    // exception
                }

                //do stuff with the body...
            }


        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                prefs.setPatientID(tempPatID);

                editor.apply();
                params.put("patientId", tempPatID);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                prefs = SharedPrefManager.getInstance(getApplicationContext()).getUser();

                String token = prefs.getToken();
                headers.put("x-token", token);
                return headers;
            }
        };
        mQueue.add(request);
    }


    /**
     * Refresh token when expired
     * with Volley Android Library
     * after v19 (Android 5), TLS 1.2 is enabled by default.
     */
    private void refreshToken() {
    }

    /**
     * Check for all permission for the app
     */
    private void checkPermission() {

        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        int resultGET_ACCOUNTS = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        int resultBLUETOOTH = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int resultBLUETOOTH_ADMIN = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int resultACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int resultACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int resultINTERNET = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);


        //If the app does have this permission, then return true//
        //If the app does not have this permission, then ask permission//
        ArrayList<String> missingPerms = new ArrayList<>();

        if (resultGET_ACCOUNTS == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.GET_ACCOUNTS);
        if (resultBLUETOOTH == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.BLUETOOTH);
        if (resultBLUETOOTH_ADMIN == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (resultACCESS_COARSE_LOCATION == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (resultACCESS_NETWORK_STATE == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if (resultINTERNET == PackageManager.PERMISSION_DENIED)
            missingPerms.add(Manifest.permission.INTERNET);

        if (!missingPerms.isEmpty()) {
            String[] missingPermsArray = missingPerms.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, (missingPermsArray), 1);
        }
    }
}