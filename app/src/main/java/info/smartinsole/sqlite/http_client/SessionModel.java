package info.smartinsole.sqlite.http_client;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import info.smartinsole.sqlite.database.model.Datam;

public class SessionModel {

    private String product;
    private double version;
    private String releaseDate;
    private boolean demo;

    private Session session;

    private List<Datum> data;


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getVersion() {
        return version;
    }

    public SessionModel setVersion(double version) {
        this.version = version;
        return this;
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }
}


