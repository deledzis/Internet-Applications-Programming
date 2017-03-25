import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class Localization_fi_FI extends ResourceBundle {
    private Hashtable<String, String> mRes = null;

    public Localization_fi_FI() {
        mRes = new Hashtable<>();

        mRes.put("apply", "Käyttää");
        mRes.put("Title", "ISO. Laboratoriotyöskentely #5. Stjagow A., P3210");
        mRes.put("lastPointFirst", "Koordinaatit viimeksi");
        mRes.put("lastPointSecond", "asetettu piste");
        mRes.put("addPoint", "Lisätäksesi kohta");
        mRes.put("XCoordinate", "X-koordinaatti:");
        mRes.put("YCoordinate", "Y-koordinaatti:");
        mRes.put("add", "Lisätäksesi");
        mRes.put("SetRadius", "Vaihtaa säde");
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