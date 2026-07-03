package me.arian.wea.util;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockBox {

    private final List<Block> blocks = new ArrayList<>();

    private final Block block1;
    private final Block block2;

    public BlockBox(Block block1, Block block2) {
        this.block1 = block1;
        this.block2 = block2;

        int minX = Math.min(block1.getX(), block2.getX());
        int maxX = Math.max(block1.getX(), block2.getX());
        int minY = Math.min(block1.getY(), block2.getY());
        int maxY = Math.max(block1.getY(), block2.getY());
        int minZ = Math.min(block1.getZ(), block2.getZ());
        int maxZ = Math.max(block1.getZ(), block2.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(block1.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    @NotNull
    public final List<Block> blocks() {
        return blocks;
    }

    public Block getFirstPos() {
        return block1;
    }

    public Block getSecondPos() {
        return block2;
    }
}
