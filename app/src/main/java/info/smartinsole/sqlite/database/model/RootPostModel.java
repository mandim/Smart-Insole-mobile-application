package info.smartinsole.sqlite.database.model;

import java.util.Date;
import java.util.List;

public class RootPostModel {
    public String product;
    public int version;
    public Date releaseDate;
    public boolean demo;
    public Sesion session;
    public List<Datam> data;

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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
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