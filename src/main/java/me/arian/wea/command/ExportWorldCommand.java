package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.arian.wea.WEAddon;
import me.arian.wea.worldgen.ExportWorldGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldCreator;

public class ExportWorldCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("createexportworld").requires(source -> source.getBukkitSender().hasPermission("wea.createexportworld"))
            .then(Commands.argument("name", StringArgumentType.word()).executes(ctx -> {
                String name = StringArgumentType.getString(ctx, "name");
                new WorldCreator(new NamespacedKey(WEAddon.get(), name)).generator(new ExportWorldGenerator()).generateStructures(false).createWorld();

                ctx.getSource().sendSuccess(() -> Component.literal("Created world " + name).withStyle(ChatFormatting.GREEN), true);
                return Command.SINGLE_SUCCESS;
            })));
    }
}
