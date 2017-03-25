import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class Localization_en_EN extends ResourceBundle {
    private Hashtable<String, String> mRes = null;

    public Localization_en_EN() {
        mRes = new Hashtable<>();

        mRes.put("apply","Apply");
        mRes.put("Title","IAPS. Lab #5. Styagov A., P3210");
        mRes.put("lastPointFirst","Last Set Point");
        mRes.put("lastPointSecond","Coordinates");
        mRes.put("addPoint","Add Point");
        mRes.put("XCoordinate","X Coordinate:");
        mRes.put("YCoordinate","Y Coordinate:");
        mRes.put("add","Add");
        mRes.put("SetRadius","Set Radius");
    }

    @Override
    protected Object handleGetObject(String key) throws
            java.util.MissingResourceException {
        return mRes.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return mRes.keys();
    }
}