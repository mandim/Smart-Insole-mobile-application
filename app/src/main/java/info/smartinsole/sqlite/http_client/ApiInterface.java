package info.smartinsole.sqlite.http_client;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import info.smartinsole.sqlite.database.model.Sesion;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("/api/sessions")
    @Headers("Content-Type: application/json")
    Call<Sesion> postSession(@Header("x-token") String token,
                             @Header("x-patient-api-key") String patientApiKey,
                             @Body String body);


}