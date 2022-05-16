package com.darkguardsman.mrsmith.data.exports;

import com.darkguardsman.mrsmith.data.DataMinerMod;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class ExportHelpers {
    public static void writeJson(final Gson gson, final JsonElement writeData, final File file) {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(writeData, writer);
        } catch (Exception e) {
            DataMinerMod.LOG.error("Failed to write file: " + file, e);
        }
    }
}
