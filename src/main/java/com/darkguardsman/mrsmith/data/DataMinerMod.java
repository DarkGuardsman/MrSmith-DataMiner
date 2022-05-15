package com.darkguardsman.mrsmith.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = DataMinerMod.ID, name = "MrSmith - Data Miner", version = "0.1.0")
public final class DataMinerMod {

    private static final Logger LOG = LogManager.getLogger();
    public static final String ID = "mrsmith-dataminer";

    @Mod.Instance(DataMinerMod.ID)
    public static DataMinerMod INSTANCE;

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        dumpItemRegistry();
        FMLCommonHandler.instance().exitJava(0, false);
    }

    private void dumpItemRegistry() {

        final Gson gson = new Gson();

        final File saveFolder = new File("./MrSmith/exports/items");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }

        final Map<String, String> resourceLocationToFile = new HashMap();

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

            final File saveFile = new File(domainFolder, "/" + key + ".json"); //TODO lint file name

            //Log file we saved again for fast lookup later
            resourceLocationToFile.put(item.getRegistryName().toString(), saveFile.getPath());

            final JsonObject fileEntry = new JsonObject();
            fileEntry.addProperty("key", item.getRegistryName().toString());
            fileEntry.addProperty("name", item.getItemStackDisplayName(item.getDefaultInstance()));
            fileEntry.addProperty("localization", item.getUnlocalizedName(item.getDefaultInstance()));
            fileEntry.addProperty("image", item.getRegistryName().getResourceDomain() + "_" + item.getItemStackDisplayName(item.getDefaultInstance()).replaceAll("\\s+", "_").toLowerCase());

            writeJson(gson, fileEntry, saveFile);


            //Handle subtypes
            if(item.getHasSubtypes()) {
                final NonNullList<ItemStack> items = NonNullList.create(); //TODO handle duplicates, NBT data, etc
                for(CreativeTabs tab : item.getCreativeTabs()) {
                    item.getSubItems(tab, items);
                }

                items.forEach(subItem -> {
                    final String subTypeKey = item.getRegistryName().toString() + "@" + subItem.getItemDamage();
                    final File subTypeSaveFile = new File(domainFolder, "/" + key + "_" + subItem.getItemDamage()  + ".json"); //TODO lint file name

                    //Log file we saved again for fast lookup later
                    resourceLocationToFile.put(subTypeKey, subTypeSaveFile.getPath());

                    final JsonObject subTypeEntry = new JsonObject();
                    subTypeEntry.addProperty("key", subTypeKey);
                    subTypeEntry.addProperty("parent", item.getRegistryName().toString());
                    subTypeEntry.addProperty("name", item.getItemStackDisplayName(subItem));
                    subTypeEntry.addProperty("localization", item.getUnlocalizedName(subItem));
                    fileEntry.addProperty("image", item.getRegistryName().getResourceDomain() + "_" + item.getItemStackDisplayName(subItem).replaceAll("\\s+", "_").toLowerCase());

                    writeJson(gson, subTypeEntry, subTypeSaveFile);
                });
            }

        });

        final JsonArray fileList = new JsonArray();

        resourceLocationToFile.forEach((key, value) -> {
            final JsonObject fileEntry = new JsonObject();
            fileEntry.addProperty("key", key);
            fileEntry.addProperty("file", value);
            fileList.add(fileEntry);
        });

        final File itemsFile = new File(saveFolder, "items.json");
        writeJson(gson, fileList, itemsFile);
    }

    private void writeJson(final Gson gson, final JsonElement writeData, final File file) {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(writeData, writer);
        } catch (Exception e) {
            LOG.error("Failed to write file: " + file, e);
        }
    }
}
