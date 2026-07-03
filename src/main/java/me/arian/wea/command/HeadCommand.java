package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.arian.wea.util.CommonCompletions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class HeadCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(Commands.literal("head")
            .requires(source -> source.getBukkitSender().hasPermission("wea.head"))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(CommonCompletions.players(), builder))
                .executes(ctx -> giveSkull(ctx, StringArgumentType.getString(ctx, "player")))));

        dispatcher.register(Commands.literal("skull").requires(source -> source.getBukkitSender().hasPermission("wea.head")).redirect(root));
    }

    private static int giveSkull(CommandContext<CommandSourceStack> ctx, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(Bukkit.createProfile(name));
        head.setItemMeta(meta);

        final Player player = Objects.requireNonNull(ctx.getSource().getPlayer(), "Only a player can do this!").getBukkitEntity();
        if (player.getInventory().firstEmpty() == -1) {
            ctx.getSource().sendFailure(Component.literal("Your inventory is full!"), true);
        } else {
            player.getInventory().addItem(head);
        }

        return Command.SINGLE_SUCCESS;
    }
}
