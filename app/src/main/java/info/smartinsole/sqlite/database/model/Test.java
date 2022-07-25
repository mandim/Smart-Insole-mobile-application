package info.smartinsole.sqlite.database.model;

public class Test {
    public static final String TABLE_NAME = "testTable";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_STARTTIMESTAMP = "startTimestamp";
    public static final String COLUMN_STOPTIMESTAMP = "stopTimestamp";
    public static final String COLUMN_TEST_TYPE = "TestType";
    public static final String COLUMN_DURATION = "Duration";
    public static final String COLUMN_SYNCSTATUS = "SyncStatus";

    private int id;
    private String startTimestamp;
    private String stopTimestamp;
    private String testType;
    private String duration;

    private String sync;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STARTTIMESTAMP + " TIMESTAMP,"
                    + COLUMN_STOPTIMESTAMP + " FRAMETIMESTAMP,"
                    + COLUMN_TEST_TYPE + " TESTTYPE,"
                    + COLUMN_DURATION + " DURATION,"
                    + COLUMN_SYNCSTATUS + " SYNCSTATUS"
                    + ")";

    public Test() {
    }

    public Test(int id, String startTimestamp, String stopTimestamp, String testType, String duration, String sync) {
        this.id = id;

        this.startTimestamp = startTimestamp;
        this.stopTimestamp = stopTimestamp;
        this.testType = testType;
        this.duration = duration;
        this.sync = sync;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(String stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public void setTestType(String testType){
        this.testType = testType;
    }

    public  String getTestType(){
        return testType;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }

    public String getDuration(){
        return duration;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }
}
