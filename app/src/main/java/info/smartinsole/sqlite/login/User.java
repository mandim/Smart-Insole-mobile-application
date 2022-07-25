package info.smartinsole.sqlite.login;

/**
 * This is very simple class and it only contains the user attributes and JWToken
 * a constructor and the getters
 */
public class User {

    private int id;
    private String username, expiresAt, token, patientID, refreshtoken, apikey;

    public User(int id, String username, String expiresAt, String token, String patientID, String refreshtoken, String apikey) {
        this.id = id;
        this.username = username;
        this.expiresAt = expiresAt;
        this.token = token;
        this.patientID = patientID;
        this.refreshtoken = refreshtoken;
        this.apikey = apikey;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}