package me.arian.wea;

import com.mojang.brigadier.CommandDispatcher;
import me.arian.wea.command.*;
import me.arian.wea.command.DisplayCommand;
import me.arian.wea.worldgen.CustomDesertChunkGenerator;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class WEAddon extends JavaPlugin {

    private static WEAddon inst;

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onEnable() {
        createDatapack();

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockPlaceToolCommand(), this);

        final CommandDispatcher<CommandSourceStack> dispatcher = DedicatedServer.getServer().getCommands().getDispatcher();
        final CommandBuildContext registryAccess =
            CommandBuildContext.configurable(DedicatedServer.getServer().registryAccess(), FeatureFlagSet.of(FeatureFlags.VANILLA, FeatureFlags.BUNDLE));
        BlockPlaceToolCommand.register(dispatcher, registryAccess);
        ChunkResetCommand.register(dispatcher);
        DisplayCommand.register(dispatcher, registryAccess);
        ExportWorldCommand.register(dispatcher);
        HeadCommand.register(dispatcher);
        NmsBiomeCommand.register(dispatcher, registryAccess);
        RotateCommand.register(dispatcher);
        SetStoneCommand.register(dispatcher, registryAccess);
        WorldCommand.register(dispatcher);
    }

    public static WEAddon get() {
        return inst;
    }

    private void createDatapack() {
        Path folderPath = Paths.get("world/datapacks/weaddon/data/weaddon");
        Path rootDp = Paths.get("world/datapacks/weaddon");
        Path mcmeta = rootDp.resolve("pack.mcmeta");
        Path biomeFolder = folderPath.resolve("worldgen/biome");

        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);
                getLogger().info("Created datapack folder!");
            }

            if (Files.notExists(biomeFolder)) {
                Files.createDirectories(biomeFolder);
                getLogger().info("Created biome folder!");
            }


            if (Files.notExists(mcmeta)) {
                String content = """
                    {
                      "pack": {
                        "pack_format": 15,
                        "description": "WEAddon Data"
                      }
                    }""";

                Files.writeString(mcmeta, content);
                getLogger().info("Created pack.mcmeta!");
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Unable to create datapack!", e);
        }
    }
}
