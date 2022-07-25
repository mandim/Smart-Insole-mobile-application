package info.smartinsole.sqlite.database.model;

import com.google.gson.annotations.SerializedName;

public class Sesion {
    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("patientId")
    private String patientId;

    @SerializedName("insoleId")
    private String insoleId;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("isPart")
    private boolean isPart;

    @SerializedName("sessionType")
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

    public void setPart(boolean part) {
        isPart = part;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }
}
