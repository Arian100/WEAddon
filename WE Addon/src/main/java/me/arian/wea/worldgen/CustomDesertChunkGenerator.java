package me.arian.wea.worldgen;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CustomDesertChunkGenerator extends ChunkGenerator {

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(worldInfo.getSeed(), 8);
        generator.setScale(0.005D);

        for (int X = 0; X < 16; X++)
            for (int Z = 0; Z < 16; Z++) {
                int currentHeight = (int) (generator.noise(chunkX * 16 + X, chunkZ * 16 + Z, 0.5D, 0.5D) * 15D + 50D);
                chunkData.setBlock(X, currentHeight, Z, Material.SAND);
                chunkData.setBlock(X, currentHeight-1, Z, Material.SANDSTONE);
                for (int i = currentHeight - 2; i > 0; i--)
                    chunkData.setBlock(X, i, Z, Material.STONE);
                chunkData.setBlock(X, 0, Z, Material.BEDROCK);
            }
    }
}
