package com.example.examplemod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class CompanionMemory {
    private static final String MEMORY_FOLDER = "config/examplemod/memory/";
    private static final Gson gson = new Gson();

    private final UUID companionUUID;
    private final List<String> facts = new ArrayList<>();

    public CompanionMemory(UUID companionUUID) {
        this.companionUUID = companionUUID;
        load();
    }

    public void addFact(String fact) {
        if (!facts.contains(fact)) facts.add(fact);
    }
    public boolean hasFact(String fact) {
        return facts.contains(fact);
    }
    public List<String> getFacts() {
        return facts;
    }

    public void save() {
        try {
            new File(MEMORY_FOLDER).mkdirs();
            FileWriter writer = new FileWriter(MEMORY_FOLDER + companionUUID + ".json");
            gson.toJson(facts, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            File file = new File(MEMORY_FOLDER + companionUUID + ".json");
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type listType = new TypeToken<List<String>>(){}.getType();
                List<String> loaded = gson.fromJson(reader, listType);
                facts.clear();
                if (loaded != null) facts.addAll(loaded);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
