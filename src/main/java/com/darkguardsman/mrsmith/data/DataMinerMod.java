package com.darkguardsman.mrsmith.data;

import com.darkguardsman.mrsmith.data.config.ConfigMain;
import com.darkguardsman.mrsmith.data.exports.ItemExports;
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

    public static final Logger LOG = LogManager.getLogger();
    public static final String ID = "mrsmith-dataminer";

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (ConfigMain.EXPORT_ITEMS) {
            ItemExports.exportItemRegistry(new File(ConfigMain.EXPORT_FOLDER));
        }
        if (ConfigMain.EXIT_ON_COMPLETE) {
            FMLCommonHandler.instance().exitJava(0, false);
        }
    }
}
