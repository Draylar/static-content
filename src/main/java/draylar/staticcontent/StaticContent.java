package draylar.staticcontent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import draylar.staticcontent.api.ContentData;
import draylar.staticcontent.gson.IdentifierDeserializer;
import io.github.cottonmc.staticdata.StaticData;
import io.github.cottonmc.staticdata.StaticDataItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StaticContent implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Static Content");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Identifier.class, new IdentifierDeserializer()).create();
    private static final List<String> USED_DIRECTORIES = new ArrayList<>();

    @Override
    public void onInitialize() {

    }

    public static void load(Identifier directory, Class<? extends ContentData> dataClass) {
        if(USED_DIRECTORIES.contains(directory.toString())) {
            throw new UnsupportedOperationException(String.format("%s was already registered as a data directory key!", directory));
        } else {
            Set<StaticDataItem> dataSet = StaticData.getAllInDirectory(String.format("%s/%s", directory.getNamespace(), directory.getPath()));

            // load each discovered data file
            for(StaticDataItem data : dataSet) {
                try {
                    InputStreamReader reader = new InputStreamReader(data.createInputStream(), StandardCharsets.UTF_8);
                    StaticContent.GSON.fromJson(reader, dataClass).register(data.getIdentifier());
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // log data about loading static content
            StaticContent.LOGGER.info(String.format("Loaded %d Static Content file%s for %s", dataSet.size(), dataSet.size() == 1 ? "" : "s", directory));

            // add directory key to used directories (helps squash out conflict errors)
            USED_DIRECTORIES.add(directory.toString());
        }
    }
}
