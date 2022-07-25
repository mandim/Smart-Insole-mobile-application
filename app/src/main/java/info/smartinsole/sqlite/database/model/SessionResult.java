package info.smartinsole.sqlite.database.model;

public class SessionResult
{
    private String sessionId;
    private String patientId;
    private String insoleId;
    private String startTime;
    private String endTime;
    private String isPart;
    private String sessionType;

    public SessionResult(String sessionId, String patientId, String insoleId, String startTime, String endTime, String isPart, String sessionType) {


    }

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

    public String getIsPart() {
        return isPart;
    }

    public void setIsPart(String isPart) {
        this.isPart = isPart;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }
}
