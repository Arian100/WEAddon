package me.arian.wea.worldgen;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ExportWorldGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        if (chunkX == 0 && chunkZ == 0) {
            chunkData.setBlock(7, 64, 7, Material.BEDROCK);
            chunkData.setBlock(8, 64, 8, Material.BEDROCK);
            chunkData.setBlock(7, 64, 8, Material.BEDROCK);
            chunkData.setBlock(8, 64, 7, Material.BEDROCK);
        }
    }
}
