import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class Localization_ru_RU extends ResourceBundle {
    private Hashtable<String, String> mRes = null;

    public Localization_ru_RU() {
        mRes = new Hashtable<>();

        mRes.put("apply","Применить");
        mRes.put("Title","ПИП. Лабораторная работа #5. Стягов А. Ю., P3210");
        mRes.put("lastPointFirst","Координаты последней");
        mRes.put("lastPointSecond","установленной точки");
        mRes.put("addPoint","Добавить точку(-и)");
        mRes.put("XCoordinate","Координата X:");
        mRes.put("YCoordinate","Координата Y:");
        mRes.put("add","Добавить");
        mRes.put("SetRadius","Задать радиус");
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