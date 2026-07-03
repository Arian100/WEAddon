package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;

public class ChunkResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chunkreset").requires(source -> source.getBukkitSender().hasPermission("wea.chunkreset"))
            .executes(ctx -> {
                final CraftPlayer player = ctx.getSource().getPlayer().getBukkitEntity();
                final Chunk chunk = player.getChunk();

                return resetChunk(ctx, ctx.getSource().getLevel(), chunk.getX(), chunk.getZ());
            })
            .then(Commands.argument("chunkX", IntegerArgumentType.integer())
                .then(Commands.argument("chunkZ", IntegerArgumentType.integer()).executes(ctx -> {
                    final int x = IntegerArgumentType.getInteger(ctx, "chunkX");
                    final int z = IntegerArgumentType.getInteger(ctx, "chunkZ");

                    return resetChunk(ctx, ctx.getSource().getLevel(), x, z);
                })
                    .then(Commands.argument("world", DimensionArgument.dimension())
                        .executes(ctx -> {
                            final int x = IntegerArgumentType.getInteger(ctx, "chunkX");
                            final int z = IntegerArgumentType.getInteger(ctx, "chunkZ");
                            final ServerLevel world = DimensionArgument.getDimension(ctx, "world");

                            return resetChunk(ctx, world, x, z);
                        }))
                ))
        );
    }

    private static int resetChunk(CommandContext<CommandSourceStack> ctx, ServerLevel world, int x, int z) {
        world.getWorld().regenerateChunk(x, z);
        world.getWorld().refreshChunk(x, z);
        world.getWorld().unloadChunk(x, z);
        world.getWorld().loadChunk(x, z);

        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<green>Reset <dark_gray>chunk at <aqua>%s %s <dark_gray>in <aqua>%s".formatted(x, z, world.dimension().location())
        )), false);
        return Command.SINGLE_SUCCESS;
    }
}
