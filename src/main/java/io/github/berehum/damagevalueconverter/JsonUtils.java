package io.github.berehum.damagevalueconverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class JsonUtils {

    //convert results
    public static final int SUCCEEDED = 0;
    public static final int JSON_ERROR = 1;
    public static final int FILE_ERROR = 2;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final MainApplication application;

    public JsonUtils(MainApplication application) {
        this.application = application;
    }

    public synchronized int convert(String path) {
        return convert(new File(path));
    }

    public synchronized int convert(File file) {
        if (!file.exists() || file.isDirectory()) return FILE_ERROR;

        final String prefix = file.getName() + " | ";

        application.log(prefix+ "Started converting");

        Map<?, ?> map;

        try (FileReader reader = new FileReader(file)) {
            map = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            return FILE_ERROR;
        }

        if (map == null) return JSON_ERROR;

        //Change Json Values

        int modelData = 1;

        try {
            ArrayList<?> list = (ArrayList<?>) map.get("overrides");

            if (list == null) return JSON_ERROR;
            for (Object o : list) {
                if (!(o instanceof LinkedTreeMap<?, ?>)) continue;
                LinkedTreeMap<?, ?> overridesMap = (LinkedTreeMap<?, ?>) o;

                Object o2 = overridesMap.get("predicate");
                if (!(o2 instanceof LinkedTreeMap<?, ?>)) continue;
                LinkedTreeMap<Object, Object> predicateMap = (LinkedTreeMap<Object, Object>) o2;
                if (predicateMap.containsKey("damage") && predicateMap.containsKey("damaged")) {
                    predicateMap.remove("damaged");
                    predicateMap.remove("damage");
                    predicateMap.put("custom_model_data", modelData);
                    modelData++;
                }
            }
        } catch (Exception e) {
            return JSON_ERROR;
        }

        //--

        //Writing to file

        File convertedFile = new File(file.getParentFile().getAbsolutePath() + "/converted/" + file.getName());
        convertedFile.getParentFile().mkdirs();
        application.log(prefix+"Creating: " + convertedFile.getPath());
        
        try {
            if (!convertedFile.exists()) convertedFile.createNewFile();
            FileWriter writer = new FileWriter(convertedFile);
            gson.toJson(map, writer);
            writer.close();
            application.log(prefix+"Finished converting");
        } catch (IOException e) {
            application.log(prefix+"Error whilst creating: " + convertedFile.getAbsolutePath());
            application.log(prefix+"ERROR: " + e.getMessage());
            return FILE_ERROR;
        }
        
        return SUCCEEDED;

        //
    }

}
