package io.github.berehum.damagevalueconverter;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class JsonUtils {

    //convert results
    public static final int SUCCEEDED = 0;
    public static final int JSON_ERROR = 1;
    public static final int FILE_ERROR = 2;

    private final Gson gson = new Gson();
    private final MainApplication application;

    public JsonUtils(MainApplication application) {
        this.application = application;
    }


    public synchronized int convert(String path) {
        return convert(new File(path));
    }

    public synchronized int convert(File file) {
        if (!file.exists() || file.isDirectory()) return FILE_ERROR;

        Map<?, ?> map;

        try (FileReader reader = new FileReader(file)) {
            map = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            return FILE_ERROR;
        }

        if (map == null) return JSON_ERROR;

        ArrayList<?> list = (ArrayList<?>) map.get("overrides");

        if (list == null) return JSON_ERROR;
        for (Object o : list) {
            application.log(o.toString());
        }

        //debug
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        return SUCCEEDED;
    }

}
