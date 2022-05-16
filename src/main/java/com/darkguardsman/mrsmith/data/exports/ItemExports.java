package com.darkguardsman.mrsmith.data.exports;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemExports {
    private static final Gson gson = new Gson();

    public static void exportItemRegistry(final File rootFolder) {

        //Create save folder
        final File saveFolder = new File(rootFolder, "/items");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }

        //Collect and export items
        final Map<String, String> resourceLocationToFile = new HashMap();
        exportItemFiles(saveFolder, resourceLocationToFile);


        //Generate list of files
        exportItemsList(saveFolder, resourceLocationToFile);
    }

    private static void exportItemsList(final File saveFolder, final Map<String, String> resourceLocationToFile) {
        final JsonArray fileList = new JsonArray();
        resourceLocationToFile.forEach((key, value) -> {
            final JsonObject fileEntry = new JsonObject();
            fileEntry.addProperty("key", key);
            fileEntry.addProperty("file", value);
            fileList.add(fileEntry);
        });

        final File itemsFile = new File(saveFolder, "items.json");
        ExportHelpers.writeJson(gson, fileList, itemsFile);
    }

    private static void exportItemFiles(final File saveFolder, final Map<String, String> resourceLocationToFile) {
        //Dump individual items
        final IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;
        itemRegistry.forEach(item -> {
            final String domain = item.getRegistryName().getResourceDomain();
            final String key = item.getRegistryName().getResourcePath();

            //Make domain folder if it doesn't exist
            final File domainFolder = new File(saveFolder, "/" + domain);
            if (!domainFolder.exists()) {
                domainFolder.mkdirs();
            }

            //Save base item
            final File saveFile = new File(domainFolder, "/" + key + ".json"); //TODO lint file name
            resourceLocationToFile.put(item.getRegistryName().toString(), saveFile.getPath());
            writeBaseItem(saveFile, item);

            //Handle subtypes
            if (item.getHasSubtypes()) {
                collectSubtypes(item).forEach(itemStack -> {
                    final String subTypeKey = item.getRegistryName().toString() + "@" + itemStack.getItemDamage();
                    final File subTypeSaveFile = new File(domainFolder, "/" + key + "_" + itemStack.getItemDamage() + ".json"); //TODO lint file name
                    resourceLocationToFile.put(subTypeKey, subTypeSaveFile.getPath());
                    writeSubType(item, itemStack, subTypeKey, subTypeSaveFile);
                });
            }
        });
    }

    private static void writeBaseItem(final File saveFile , final Item item) {

        final JsonObject fileEntry = new JsonObject();
        fileEntry.addProperty("key", item.getRegistryName().toString());
        fileEntry.addProperty("domain", item.getRegistryName().getResourceDomain());
        fileEntry.addProperty("name", item.getItemStackDisplayName(item.getDefaultInstance()));
        fileEntry.addProperty("localization", item.getUnlocalizedName(item.getDefaultInstance()));
        fileEntry.addProperty("image", item.getRegistryName().getResourceDomain() + "_" + item.getItemStackDisplayName(item.getDefaultInstance()).replaceAll("\\s+", "_").toLowerCase());

        ExportHelpers.writeJson(gson, fileEntry, saveFile);
    }

    private static NonNullList<ItemStack> collectSubtypes(final Item item) {
        final NonNullList<ItemStack> items = NonNullList.create(); //TODO handle duplicates, NBT data, etc
        for (CreativeTabs tab : item.getCreativeTabs()) {
            item.getSubItems(tab, items);
        }
        return items;
    }

    private static void writeSubType(final Item item, final ItemStack itemStack,  final String subTypeKey, final File subTypeSaveFile) {
        final JsonObject subTypeEntry = new JsonObject();
        subTypeEntry.addProperty("key", subTypeKey);
        subTypeEntry.addProperty("domain", item.getRegistryName().getResourceDomain());
        subTypeEntry.addProperty("parent", item.getRegistryName().toString());
        subTypeEntry.addProperty("name", item.getItemStackDisplayName(itemStack));
        subTypeEntry.addProperty("localization", item.getUnlocalizedName(itemStack));
        subTypeEntry.addProperty("image", item.getRegistryName().getResourceDomain() + "_" + item.getItemStackDisplayName(itemStack).replaceAll("\\s+", "_").toLowerCase());

        ExportHelpers.writeJson(gson, subTypeEntry, subTypeSaveFile);
    }
}
