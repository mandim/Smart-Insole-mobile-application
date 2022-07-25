package info.smartinsole.sqlite.http_client;


import com.google.gson.annotations.SerializedName;

public class Session {
    private String sessionId;

    private String patientId;

    private String insoleId;

    private String startTime;

    private String endTime;



    @SerializedName("isPart")
    private boolean isPart;

    private int sessionType;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getInsoleId() {
        return insoleId;
    }

    public void setInsoleId(String insoleId) {
        this.insoleId = insoleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isPart() {
        return isPart;
    }

    public Session setPart(boolean part) {
        isPart = part;
        return this;
    }


    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }
}
