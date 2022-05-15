package com.darkguardsman.mrsmith.data;

import net.minecraft.item.Item;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = DataMinerMod.ID, name = "MrSmith - Data Miner", version = "0.1.0")
public final class DataMinerMod
{
    public static final String ID = "mrsmith-dataminer";

    @Mod.Instance(DataMinerMod.ID)
    public static DataMinerMod INSTANCE;

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        dumpItemRegistry();
        FMLCommonHandler.instance().exitJava(0, false);
    }

    private void dumpItemRegistry() {

        final File saveFolder = new File("./MrSmith/exports/items");
        if(!saveFolder.exists()) {
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
            if(!domainFolder.exists()) {
                domainFolder.mkdirs();
            }

            final File saveFile = new File(domainFolder, "/" + key + ".json"); //TODO lint file name

            //Log file we saved again for fast lookup later
            resourceLocationToFile.put(item.getRegistryName().toString(), saveFile.getPath());

            //TODO write data

        });


    }
}
