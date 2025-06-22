package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.adventure.PaperAdventure;
import me.arian.wea.WEAddon;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

public class BlockPlaceToolCommand implements Listener {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(Commands.literal("blockstateplacer").requires(source -> source.getBukkitSender().hasPermission("wea.blockstateplacer"))
            .then(Commands.argument("block", BlockStateArgument.block(registryAccess))
                .executes(ctx -> giveTool(ctx, BlockStateArgument.getBlock(ctx, "block")))));

        dispatcher.register(Commands.literal("bsp").requires(source -> source.getBukkitSender().hasPermission("wea.blockstateplacer")).redirect(root));
    }

    private static int giveTool(CommandContext<CommandSourceStack> ctx, BlockInput block) {
        try {
            Constructor<CraftBlockData> constructor = CraftBlockData.class.getDeclaredConstructor(BlockState.class);
            constructor.setAccessible(true);
            CraftBlockData data = constructor.newInstance(block.getState());

            NamespacedKey key = new NamespacedKey(WEAddon.get(), "state");

            ItemStack tool = new ItemStack(Material.BARRIER);
            ItemMeta meta = tool.getItemMeta();
            meta.displayName(net.kyori.adventure.text.Component.text("Block Place Tool (%s)".formatted(
                data.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ")
            )).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                net.kyori.adventure.text.Component.empty(),
                net.kyori.adventure.text.Component.text("Right click to place").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                net.kyori.adventure.text.Component.text(data.getAsString()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                net.kyori.adventure.text.Component.empty()
            ));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data.getAsString());
            tool.setItemMeta(meta);

            final Player player = Objects.requireNonNull(ctx.getSource().getPlayer(), "Only a player can do this!").getBukkitEntity();
            if (player.getInventory().firstEmpty() == -1) {
                ctx.getSource().sendFailure(Component.literal("Your inventory is full!"), true);
            } else {
                player.getInventory().addItem(tool);
            }

            ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                "<green>Created <dark_gray>tool with <aqua>" + data.getAsString()
            )), false);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to access CraftBlockState!", e);
            ctx.getSource().sendFailure(Component.literal("Failed to create blockstate tool \n" + e), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType().equals(Material.BARRIER) && event.getItemInHand().hasItemMeta()) {
            final PersistentDataContainer pdc = event.getItemInHand().getItemMeta().getPersistentDataContainer();
            final NamespacedKey key = new NamespacedKey(WEAddon.get(), "state");
            if (pdc.has(key, PersistentDataType.STRING)) {
                final BlockData data = Bukkit.createBlockData(Objects.requireNonNull(pdc.get(
                    key, PersistentDataType.STRING), "The item hasn't any state stored!"));
                event.getBlockPlaced().setBlockData(data);
            }
        }
    }
}
