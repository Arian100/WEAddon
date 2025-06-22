package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;

public class WorldCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world").requires(source -> source.getBukkitSender().hasPermission("wea.world"))
            .then(Commands.argument("world", DimensionArgument.dimension())
                .executes(ctx -> teleport(ctx, List.of(ctx.getSource().getPlayer()), DimensionArgument.getDimension(ctx, "world"), ctx.getSource().getPlayer().position()))
                .then(Commands.argument("players", EntityArgument.players())
                    .executes(ctx -> teleport(ctx, EntityArgument.getPlayers(ctx, "players"), DimensionArgument.getDimension(ctx, "world"), ctx.getSource().getPlayer().position())))));
    }

    private static int teleport(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, ServerLevel world, Vec3 pos) {
        for (final ServerPlayer player : players) {
            player.getBukkitEntity().teleport(new Location(world.getWorld(), pos.x(), pos.y(), pos.z()), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize("<dark_gray>Teleported <aqua>%s <dark_gray>to <green>%s <dark_gray>at <green>%s %s %s"
            .formatted(players.stream().map(player -> player.getGameProfile().getName()).toList(), world.dimension().location(), pos.x(), pos.y(), pos.z()))), false);
        return Command.SINGLE_SUCCESS;
    }
}
