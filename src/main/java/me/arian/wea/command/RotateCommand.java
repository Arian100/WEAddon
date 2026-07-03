package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.adventure.PaperAdventure;
import me.arian.wea.util.CommonCompletions;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class RotateCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rotate").requires(source -> source.getBukkitSender().hasPermission("wea.rotate"))
            .then(Commands.argument("player", EntityArgument.player())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(CommonCompletions.players(), builder))
                .then(Commands.argument("degrees", FloatArgumentType.floatArg(0, 360))
                    .suggests((context, builder) -> SharedSuggestionProvider.suggest(CommonCompletions.numbersFromTo(0, 360), builder))
                    .executes(ctx -> rotate(ctx, EntityArgument.getPlayer(ctx, "player"), FloatArgumentType.getFloat(ctx, "degrees"))))));
    }

    private static int rotate(CommandContext<CommandSourceStack> ctx, ServerPlayer player, float degrees) {
        player.getBukkitEntity().setRotation(degrees, player.getBukkitEntity().getPitch());

        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
            "<dark_gray>Rotated <red>%s <dark_gray>by <aqua>%s <dark_gray> degrees".formatted(player.getGameProfile().getName(), degrees)
        )), false);
        return Command.SINGLE_SUCCESS;
    }
}
