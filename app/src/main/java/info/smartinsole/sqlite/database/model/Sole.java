package info.smartinsole.sqlite.database.model;

public class Sole {
    public static final String TABLE_NAME = "insoleTable";

    //public static final String COLUMN_ID = "id";
    //public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERID = "UserId";
    public static final String COLUMN_SOLE = "Sole";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_R_L = "Right_Left";
    public static final String COLUMN_SYNCSTATUS = "SyncStatus";
    public static final String COLUMN_ACCLXAXIS = "AcclXAxis";
    public static final String COLUMN_ACCLYAXIS = "AcclYAxis";
    public static final String COLUMN_ACCLZAXIS = "AcclZAxis";
    public static final String COLUMN_GYROROLL = "GyroRoll";
    public static final String COLUMN_GYROPITCH = "GyroPitch";
    public static final String COLUMN_GYROYAW = "GyroYaw";
    public static final String COLUMN_MAGNXAXIS = "MagnXAxis";
    public static final String COLUMN_MAGNYAXIS = "MagnYAxis";
    public static final String COLUMN_MAGNZAXIS = "MagnZAxis";
    public static final String COLUMN_PE1 = "Pe1";
    public static final String COLUMN_PE2 = "Pe2";
    public static final String COLUMN_PE3 = "Pe3";
    public static final String COLUMN_PE4 = "Pe4";
    public static final String COLUMN_PE5 = "Pe5";
    public static final String COLUMN_PE6 = "Pe6";
    public static final String COLUMN_PE7 = "Pe7";
    public static final String COLUMN_PE8 = "Pe8";
    public static final String COLUMN_PE9 = "Pe9";
    public static final String COLUMN_PE10 = "Pe10";
    public static final String COLUMN_PE11 = "Pe11";
    public static final String COLUMN_PE12 = "Pe12";
    public static final String COLUMN_PE13 = "Pe13";
    public static final String COLUMN_PE14 = "Pe14";
    public static final String COLUMN_PE15 = "Pe15";
    public static final String COLUMN_PE16 = "Pe16";
    public static final String COLUMN_GTF = "gtf";

    private int id;
    private String userid;
    private String sole;
    private String timestamp;
    private String r_l;
    private String sync;
    private String accx;
    private String accy;
    private String accz;
    private String gyropoll;
    private String gyropitch;
    private String gyroyaw;
    private String magnx;
    private String magny;
    private String magnz;
    private String pe1;
    private String pe2;
    private String pe3;
    private String pe4;
    private String pe5;
    private String pe6;
    private String pe7;
    private String pe8;
    private String pe9;
    private String pe10;
    private String pe11;
    private String pe12;
    private String pe13;
    private String pe14;
    private String pe15;
    private String pe16;
    private String gtf;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERID + " USERID,"
                    + COLUMN_SOLE + " SOLE,"
                    + COLUMN_TIMESTAMP + " TIMESTAMP,"
                    + COLUMN_R_L + " RL,"
                    + COLUMN_SYNCSTATUS + " SYNCSTATUS,"
                    + COLUMN_ACCLXAXIS + " ACCLXAXIS,"
                    + COLUMN_ACCLYAXIS + " ACCLYAXIS,"
                    + COLUMN_ACCLZAXIS + " ACCLZAXIS,"
                    + COLUMN_GYROROLL + " GYROROLL,"
                    + COLUMN_GYROPITCH + " GYROPITCH,"
                    + COLUMN_GYROYAW + " GYROYAW,"
                    + COLUMN_MAGNXAXIS + " MAGNXAXIS,"
                    + COLUMN_MAGNYAXIS + " MAGNYAXIS,"
                    + COLUMN_MAGNZAXIS + " MAGNZAXIS,"
                    + COLUMN_PE1 + " PE1,"
                    + COLUMN_PE2 + " PE2,"
                    + COLUMN_PE3 + " PE3,"
                    + COLUMN_PE4 + " PE4,"
                    + COLUMN_PE5 + " PE5,"
                    + COLUMN_PE6 + " PE6,"
                    + COLUMN_PE7 + " PE7,"
                    + COLUMN_PE8 + " PE8,"
                    + COLUMN_PE9 + " PE9,"
                    + COLUMN_PE10 + " PE10,"
                    + COLUMN_PE11 + " PE11,"
                    + COLUMN_PE12 + " PE12,"
                    + COLUMN_PE13 + " PE13,"
                    + COLUMN_PE14 + " PE14,"
                    + COLUMN_PE15 + " PE15,"
                    + COLUMN_PE16 + " PE16,"
                    + COLUMN_GTF + " GTF"
                    + ")";

    public Sole() {
    }

    public Sole(int id, String sole, String userid, String timestamp, String r_l, String sync,
                String accx, String accy, String accz, String gyropoll, String gyropitch, String gyroyaw,
                String magnx, String magny, String magnz, String pe1, String pe2, String pe3, String pe4,
                String pe5, String pe6, String pe7, String pe8, String pe9, String pe10, String pe11,
                String pe12, String pe13, String pe14, String pe15, String pe16, String gtf) {

        this.id = id;
        this.sole = sole;
        this.userid = userid;
        this.timestamp = timestamp;
        this.r_l = r_l;
        this.sync = sync;
        this.accx = accx;
        this.accy = accy;
        this.accz = accz;
        this.gyropoll = gyropoll;
        this.gyropitch = gyropitch;
        this.gyroyaw = gyroyaw;
        this.magnx = magnx;
        this.magny = magny;
        this.magnz = magnz;
        this.pe1 = pe1;
        this.pe2 = pe2;
        this.pe3 = pe3;
        this.pe4 = pe4;
        this.pe5 = pe5;
        this.pe6 = pe6;
        this.pe7 = pe7;
        this.pe8 = pe8;
        this.pe9 = pe9;
        this.pe10 = pe10;
        this.pe11 = pe11;
        this.pe12 = pe12;
        this.pe13 = pe13;
        this.pe14 = pe14;
        this.pe15 = pe15;
        this.pe16 = pe16;
        this.gtf = gtf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSole() {
        return sole;
    }

    public void setSole(String sole) {
        this.sole = sole;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRL() {
        return r_l;
    }

    public void setRL(String r_l) {
        this.r_l = r_l;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getAccx() {
        return accx;
    }

    public void setAccx(String accx) {
        this.accx = accx;
    }

    public String getAccy() {
        return accy;
    }

    public void setAccy(String accy) {
        this.accy = accy;
    }

    public String getAccz() {
        return accz;
    }

    public void setAccz(String accz) {
        this.accz = accz;
    }

    public String getGyropoll() {
        return gyropoll;
    }

    public void setGyropoll(String gyropoll) {
        this.gyropoll = gyropoll;
    }

    public String getGyropitch() {
        return gyropitch;
    }

    public void setGyropitch(String gyropitch) {
        this.gyropitch = gyropitch;
    }

    public String getGyroyaw() {
        return gyroyaw;
    }

    public void setGyroyaw(String gyroyaw) {
        this.gyroyaw = gyroyaw;
    }

    public String getMagnx() {
        return magnx;
    }

    public void setMagnx(String magnx) {
        this.magnx = magnx;
    }

    public String getMagny() {
        return magny;
    }

    public void setMagny(String magny) {
        this.magny = magny;
    }

    public String getMagnz() {
        return magnz;
    }

    public void setMagnz(String magnz) {
        this.magnz = magnz;
    }

    public String getPe1() {
        return pe1;
    }

    public void setPe1(String pe1) {
        this.pe1 = pe1;
    }

    public String getPe2() {
        return pe2;
    }

    public void setPe2(String pe2) {
        this.pe2 = pe2;
    }

    public String getPe3() {
        return pe3;
    }

    public void setPe3(String pe3) {
        this.pe3 = pe3;
    }

    public String getPe4() {
        return pe4;
    }

    public void setPe4(String pe4) {
        this.pe4 = pe4;
    }

    public String getPe5() {
        return pe5;
    }

    public void setPe5(String pe5) {
        this.pe5 = pe5;
    }

    public String getPe6() {
        return pe6;
    }

    public void setPe6(String pe6) {
        this.pe6 = pe6;
    }

    public String getPe7() {
        return pe7;
    }

    public void setPe7(String pe7) {
        this.pe7 = pe7;
    }

    public String getPe8() {
        return pe8;
    }

    public void setPe8(String pe8) {
        this.pe8 = pe8;
    }

    public String getPe9() {
        return pe9;
    }

    public void setPe9(String pe9) {
        this.pe9 = pe9;
    }

    public String getPe10() {
        return pe10;
    }

    public void setPe10(String pe10) {
        this.pe10 = pe10;
    }

    public String getPe11() {
        return pe11;
    }

    public void setPe11(String pe11) {
        this.pe11 = pe11;
    }

    public String getPe12() {
        return pe12;
    }

    public void setPe12(String pe12) {
        this.pe12 = pe12;
    }

    public String getPe13() {
        return pe13;
    }

    public void setPe13(String pe13) {
        this.pe13 = pe13;
    }

    public String getPe14() {
        return pe14;
    }

    public void setPe14(String pe14) {
        this.pe14 = pe14;
    }

    public String getPe15() {
        return pe15;
    }

    public void setPe15(String pe15) {
        this.pe15 = pe15;
    }

    public String getPe16() {
        return pe16;
    }

    public void setPe16(String pe16) {
        this.pe16 = pe16;
    }

    public String getGtf() {
        return gtf;
    }

    public void setGtf(String gtf) {
        this.gtf = gtf;
    }

    public String getValueByPosition(int position){
        switch (position) {
            case 0:
                return timestamp;
            case 1:
                return pe1;
            case 2:
                return pe2;
            case 3:
                return pe3;
            case 4:
                return pe4;
            case 5:
                return pe5;
            case 6:
                return pe6;
            case 7:
                return pe7;
            case 8:
                return pe8;
            case 9:
                return pe9;
            case 10:
                return pe10;
            case 11:
                return pe11;
            case 12:
                return pe12;
            case 13:
                return pe13;
            case 14:
                return pe14;
            case 15:
                return pe15;
            case 16:
                return pe16;
            case 17:
                return accx;
            case 18:
                return accy;
            case 19:
                return accz;
            case 20:
                return gyropoll;
            case 21:
                return gyropitch;
            case 22:
                return gyroyaw;
            case 23:
                return magnx;
            case 24:
                return magny;
            case 25:
                return magnz;
        }
        return "ERROR";
    }

    public boolean checkSoleValidity(){

        // id is not null => auto-incremented

        for (int i=0; i<=25; i++){
            if (getValueByPosition(i) == null)
                return false;
        }

        return true;
    }
}