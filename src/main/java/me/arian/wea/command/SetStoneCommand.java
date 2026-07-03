package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;

public class SetStoneCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(Commands.literal("setstone")
            .requires(source -> source.getBukkitSender().hasPermission("wea.setblock"))
            .executes(ctx -> setStone(ctx, "minecraft:stone"))
            .then(Commands.argument("block", ResourceArgument.resource(registryAccess, Registries.BLOCK))
                .executes(ctx -> setStone(ctx, BuiltInRegistries.BLOCK.getKey(ResourceArgument.getResource(ctx, "block", Registries.BLOCK).value()).toString()))));

        dispatcher.register(Commands.literal("sb").requires(source -> source.getBukkitSender().hasPermission("wea.setblock")).redirect(root));
    }

    private static int setStone(CommandContext<CommandSourceStack> ctx, String data) {
        Vec3 loc = ctx.getSource().getPosition();
        ctx.getSource().getLevel().getWorld().setBlockData(CraftLocation.toBukkit(loc), Bukkit.createBlockData(data));
        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<dark_gray>Set block at <aqua>%s %s %s <dark_gray>to <green>%s".formatted((int) loc.x(), (int) loc.y(), (int) loc.z(),
                data)
        )), false);
        return Command.SINGLE_SUCCESS;
    }
}
