package info.smartinsole.sqlite.database.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionModel {

    @SerializedName("product")
    private String product;

    @SerializedName("version")
    private int version;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("demo")
    private boolean demo;

    @SerializedName("Sesion")
    private Sesion session;

    @SerializedName("data")
    private List<Datam> data;


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    public Sesion getSession() {
        return session;
    }

    public void setSession(Sesion session) {
        this.session = session;
    }

    public List<Datam> getData() {
        return data;
    }

    public void setData(List<Datam> data) {
        this.data = data;
    }
}

