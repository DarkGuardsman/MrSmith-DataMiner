package com.darkguardsman.mrsmith.data.config;

import com.darkguardsman.mrsmith.data.DataMinerMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;

@Config(modid = DataMinerMod.ID, name = "mrsmith/data/main")
@Config.LangKey("config.mrsmith-dataminer:main.title")
@Mod.EventBusSubscriber(modid = DataMinerMod.ID)
public class ConfigMain {

    @Config.Name("export_items")
    @Config.Comment("Exports information (id, name, lang) from the item registry for use in other systems")
    public static boolean EXPORT_ITEMS = true;

    @Config.Name("exit_on_complete")
    @Config.Comment("Close the game when complete")
    public static boolean EXIT_ON_COMPLETE = false;

    @Config.Name("export_folder")
    @Config.Comment("Folder inside minecraft instance to export files inside, Ex: '.minecraft/MrSmith/exports/'")
    public static String EXPORT_FOLDER = "./MrSmith/exports";
}
