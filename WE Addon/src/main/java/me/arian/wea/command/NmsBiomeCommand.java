package me.arian.wea.command;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import com.mojang.serialization.Lifecycle;
import io.papermc.paper.adventure.PaperAdventure;
import me.arian.wea.WEAddon;
import me.arian.wea.util.NonNulls;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

import static net.minecraft.commands.Commands.*;

public class NmsBiomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        createDatapack();

        dispatcher.register(
            literal("biome").requires(source -> source.getBukkitSender().hasPermission("wea.biome"))
                .then(literal("create")
                    .then(argument("name", StringArgumentType.word())
                        .then(argument("skyColor", StringArgumentType.word())
                            .then(argument("fogColor", StringArgumentType.word())
                                .then(argument("waterColor", StringArgumentType.word())
                                    .then(argument("waterFogColor", StringArgumentType.word())
                                        .then(argument("foliageColor", StringArgumentType.word())
                                            .then(argument("downfall", FloatArgumentType.floatArg(0F, 1F))
                                                .then(argument("temperature", FloatArgumentType.floatArg(0F, 1F))
                                                    .executes(context -> create(
                                                        context,
                                                        StringArgumentType.getString(context, "name"),
                                                        hexToInt(StringArgumentType.getString(context, "skyColor")),
                                                        hexToInt(StringArgumentType.getString(context, "fogColor")),
                                                        hexToInt(StringArgumentType.getString(context, "waterColor")),
                                                        hexToInt(StringArgumentType.getString(context, "waterFogColor")),
                                                        hexToInt(StringArgumentType.getString(context, "foliageColor")),
                                                        FloatArgumentType.getFloat(context, "downfall"),
                                                        FloatArgumentType.getFloat(context, "temperature")
                                                    )))))))))))
                .then(literal("delete").then(argument("biome", StringArgumentType.word()).suggests((context, builder) -> {
                    List<String> biomes = new ArrayList<>();

                    Path biomesFolder = Paths.get("world/datapacks/weaddon/data/weaddon/worldgen/biome");

                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(biomesFolder)) {
                        for (Path filePath : stream) {
                            if (Files.isRegularFile(filePath)) {
                                biomes.add(filePath.getFileName().toString().replace(".json", ""));
                            }
                        }
                    } catch (IOException e) {
                        WEAddon.get().getLogger().log(Level.SEVERE, "Unable get custom biomes!", e);
                    }

                    return SharedSuggestionProvider.suggest(biomes, builder);
                }).executes(context -> delete(context, StringArgumentType.getString(context, "biome")))))
                .then(literal("get").then(argument("biome", ResourceArgument.resource(registryAccess, Registries.BIOME)).executes(context -> get(context, ResourceArgument.getResource(context, "biome", Registries.BIOME).value()))))
                .then(literal("list").executes(NmsBiomeCommand::list))
        );
    }

    private static int delete(CommandContext<CommandSourceStack> context, String biome) {
        Path biomeFile = Paths.get("world/datapacks/weaddon/data/weaddon/worldgen/biome/%s.json".formatted(biome));
        try {
            Files.delete(biomeFile);
        } catch (IOException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to delete Biome!", e);
        }

        context.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<dark_gray>Deleted biome <red>weaddon:%s \n <green>Restart the server to apply the changes".formatted(biome)
        )), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int create(CommandContext<CommandSourceStack> context, String name, int skyColor, int fogColor, int waterColor, int waterFogColor, int foliageColor, float downfall, float temperature) {
        Frozen access = DedicatedServer.getServer().registryAccess();
        MappedRegistry<Biome> biomeWritableRegistry = (MappedRegistry<Biome>) access.registry(Registries.BIOME).get();

        Biome biome = new Biome.BiomeBuilder()
            .generationSettings(
                new BiomeGenerationSettings.Builder(
                    access.registry(Registries.PLACED_FEATURE).get().asLookup(),
                    access.registry(Registries.CONFIGURED_CARVER).get().asLookup()
                )
                    .build()
            )
            .hasPrecipitation(false)
            .mobSpawnSettings(
                new MobSpawnSettings.Builder()
                    .build()
            )
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .skyColor(skyColor)
                    .fogColor(fogColor)
                    .waterColor(waterColor)
                    .waterFogColor(waterFogColor)
                    .foliageColorOverride(foliageColor)
                    .build()
            )
            .downfall(downfall)
            .temperature(temperature)
            .build();

        try {
            Field frozen = MappedRegistry.class.getDeclaredField("l");
            frozen.setAccessible(true);
            frozen.set(biomeWritableRegistry, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to unfreeze registry", e);
        }

        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, new ResourceLocation("weaddon:" + name));

        biomeWritableRegistry.register(key, biome, Lifecycle.stable());

        biomeWritableRegistry.freeze();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonBiome = new JsonObject();

        jsonBiome.add("carvers", new JsonObject());
        jsonBiome.addProperty("downfall", downfall);

        JsonObject effects = new JsonObject();
        effects.addProperty("fog_color", fogColor);
        effects.addProperty("sky_color", skyColor);
        effects.addProperty("water_color", waterColor);
        effects.addProperty("water_fog_color", waterFogColor);
        effects.addProperty("foliage_color", foliageColor);
        effects.addProperty("grass_color", foliageColor);

        JsonObject moodSound = new JsonObject();
        moodSound.addProperty("sound", "minecraft:music.overworld.flower_forest");
        moodSound.addProperty("tick_delay", 200);
        moodSound.addProperty("offset", 2.0);
        moodSound.addProperty("block_search_extent", 24);
        effects.add("mood_sound", moodSound);

        jsonBiome.add("effects", effects);
        jsonBiome.addProperty("has_precipitation", biome.hasPrecipitation());
        jsonBiome.addProperty("temperature", temperature);

        JsonArray features = new JsonArray();
        for (int i = 0; i < 11; i++) {
            features.add(new JsonArray());
        }
        jsonBiome.add("features", features);

        jsonBiome.add("spawners", new JsonObject());
        jsonBiome.add("spawn_costs", new JsonObject());

        File biomeFile = new File("world/datapacks/valed/data/valed/worldgen/biome", name + ".json");

        try (FileWriter writer = new FileWriter(biomeFile)) {
            gson.toJson(jsonBiome, writer);

            writer.flush();
        } catch (IOException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to create custom biome file!", e);
        }

        context.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize("<green>Created <dark_gray>Biome <aqua>" + name)), true);

        ClientboundUpdateEnabledFeaturesPacket packet = new ClientboundUpdateEnabledFeaturesPacket(Set.of(new ResourceLocation("valed", name)));
        for (final Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }

        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<dark_gray>You need to <green>reconnect <dark_gray>to apply the changes!"));
        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<CommandSourceStack> context, Biome biome) {
        MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) DedicatedServer.getServer().registryAccess().registry(Registries.BIOME).get();

        NonNulls.nonNull(biome, "Biome is null!", biome1 -> context.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<gray>%s \n <#%s>Fog Color: #%s \n <#%s>Foliage Color: #%s \n <#%s>Sky Color: #%s \n <#%s>Water Color: #%s \n <#%s>Water Fog Color: #%s"
                .formatted(
                    biomeRegistry.getKey(biome1),
                    Integer.toHexString(biome1.getFogColor()),
                    Integer.toHexString(biome1.getFogColor()),
                    Integer.toHexString(biome1.getFoliageColor()),
                    Integer.toHexString(biome1.getFoliageColor()),
                    Integer.toHexString(biome1.getSkyColor()),
                    Integer.toHexString(biome1.getSkyColor()),
                    Integer.toHexString(biome1.getWaterColor()),
                    Integer.toHexString(biome1.getWaterColor()),
                    Integer.toHexString(biome1.getWaterFogColor()),
                    Integer.toHexString(biome1.getWaterFogColor())
                )
        )), false));
        return Command.SINGLE_SUCCESS;
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) DedicatedServer.getServer().registryAccess().registry(Registries.BIOME).get();

        biomeRegistry.stream().forEach(biome -> NonNulls.nonNull(biome, "Biome is null!", biome1 -> context.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<gray>%s \n <#%s>Fog Color: #%s \n <#%s>Foliage Color: #%s \n <#%s>Sky Color: #%s \n <#%s>Water Color: #%s \n <#%s>Water Fog Color: #%s"
                .formatted(
                    biomeRegistry.getKey(biome1),
                    Integer.toHexString(biome1.getFogColor()),
                    Integer.toHexString(biome1.getFogColor()),
                    Integer.toHexString(biome1.getFoliageColor()),
                    Integer.toHexString(biome1.getFoliageColor()),
                    Integer.toHexString(biome1.getSkyColor()),
                    Integer.toHexString(biome1.getSkyColor()),
                    Integer.toHexString(biome1.getWaterColor()),
                    Integer.toHexString(biome1.getWaterColor()),
                    Integer.toHexString(biome1.getWaterFogColor()),
                    Integer.toHexString(biome1.getWaterFogColor())
                )
        )), false)));
        return Command.SINGLE_SUCCESS;
    }

    private static int hexToInt(String hex) {
        String parse;
        if (hex.startsWith("#")) {
            parse = hex.replace("#", "");
        } else {
            parse = hex;
        }
        return Integer.parseInt(parse, 16);
    }

    private static void createDatapack() {
        Path folderPath = Paths.get("world/datapacks/valed/data/valed");
        Path rootDp = Paths.get("world/datapacks/valed");
        Path mcmeta = rootDp.resolve("pack.mcmeta");
        Path biomeFolder = folderPath.resolve("worldgen/biome");

        try {
            if (Files.notExists(folderPath)) {
                Files.createDirectories(folderPath);
                WEAddon.get().getLogger().info("Created datapack folder!");
            }

            if (Files.notExists(biomeFolder)) {
                Files.createDirectories(biomeFolder);
                WEAddon.get().getLogger().info("Created biome folder!");
            }


            if (Files.notExists(mcmeta)) {
                String content = """
                    {
                      "pack": {
                        "pack_format": 15,
                        "description": "Valed Data"
                      }
                    }""";

                Files.writeString(mcmeta, content);
                WEAddon.get().getLogger().info("Created pack.mcmeta!");
            }
        } catch (IOException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to create datapack!", e);
        }
    }
}
