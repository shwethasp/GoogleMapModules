package shwethasp.com.googlemapmodules.model;

/**
 * Created by anshikas on 14-01-2016.
 */
public class MarkerModelClass {
    private double latValue;
    private double longValue;
    private String title;

    public MarkerModelClass() {

    }

    public MarkerModelClass(double latValue, double longValue, String title) {
        this.latValue = latValue;
        this.longValue = longValue;
        this.title = title;
    }


    public double getLatValue() {
        return latValue;
    }

    public void setLatValue(double latValue) {
        this.latValue = latValue;
    }

    public double getLongValue() {
        return longValue;
    }

    public void setLongValue(double longValue) {
        this.longValue = longValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}