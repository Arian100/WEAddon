package me.arian.wea.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.adventure.PaperAdventure;
import me.arian.wea.WEAddon;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static net.minecraft.commands.Commands.*;

public class DisplayCommand {

    private static final Map<String, String> POSSIBLE_DATA_BLOCK = new HashMap<>() {{
            put("view_range", "Maximum view range of the entity. When the distance is more than <view_range> * <entityDistanceScaling> * 64, the entity is not rendered. Defaults to 1.0.");
            put("shadow_radius", "Shadow radius. Value is treated as 64 when higher than 64. If less than or equal to 0, the entity has no shadow. Defaults to 1. Interpolated.");
            put("shadow_strength", "Controls the opacity of the shadow as a function of distance to block below. Defaults to 1. Interpolated. Allowed values are 0 to 1.");
            put("glow_color", "The color in witch the display will glow. The color is a hex string without the #. Using Google's hex color picker is recommend");
            put("glow_color_override", "Overrides the glow border color. If 0, uses the color of the team that the display entity is in. Defaults to 0.");
            put("brightness_sky", "If present, overrides light values used for rendering. Omited by default (which means rendering uses values from entity position). Both the block light level, with values from 0-15 and the skylight level, with values from 0-15 are required.");
            put("brightness_block", "If present, overrides light values used for rendering. Omited by default (which means rendering uses values from entity position). Both the block light level, with values from 0-15 and the skylight level, with values from 0-15 are required.");
            put("width", "The maximum width of the entity. Rendering culling bounding box spans horizontally width/2 from entity position, and the part beyond will be culled. If set to 0, no culling on both vertical and horizonal directions. Defaults to 0.");
            put("height", "The maximum height of the entity. Rendering culling bounding box spans vertically y to y+height, and the part beyond will be culled. If set to 0, no culling on both vertical and horizonal directions. Defaults to 0.");
            put("billboard", "Controls if this entity should pivot to face player when rendered. It can be fixed (both vertical and horizontal angles are fixed), vertical (faces player around vertical axis), horizontal (pivots around horizonal axis), and center (pivots around center point). Defaults to fixed.");
            put("start_interpolation", "Interpolation start time. If less than 0, sets to the current game time.");
            put("interpolation_duration", "The duration in ticks it will take for the transformation to end. The default is ticks but it can be set to seconds if a s is appended.");
            put("right_rotation_x", "Initial rotation. This tag corresponds to the right-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("right_rotation_y", "Initial rotation. This tag corresponds to the right-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("right_rotation_z", "Initial rotation. This tag corresponds to the right-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("left_rotation_x", "Rotates the model again. This tag corresponds to the left-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("left_rotation_y", "Rotates the model again. This tag corresponds to the left-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("left_rotation_z", "Rotates the model again. This tag corresponds to the left-singular vector matrix after the matrix singular value decomposition.\n" +
                "This tag has quaternion form and angle-axis form. Only quaternion form are used when saving entities. If quaternion form is used to represent rotation, this tag is a list of float numbers with 4 elements: Axis vector with 3 elements(x,y,z) and angle (a).");
            put("translation_x", "Translation transformation. This tag corresponds to the last column in the matrix form. This tag is a list of float numbers with 3 elements (x,y,z)");
            put("translation_y", "Translation transformation. This tag corresponds to the last column in the matrix form. This tag is a list of float numbers with 3 elements (x,y,z)");
            put("translation_z", "Translation transformation. This tag corresponds to the last column in the matrix form. This tag is a list of float numbers with 3 elements (x,y,z)");
            put("scale_x", "Scale the model centered on the origin. This tag corresponds to the singular values of the matrix after singular value decomposition. This tag is a list of float numbers with 3 elements (x,y,z).");
            put("scale_y", "Scale the model centered on the origin. This tag corresponds to the singular values of the matrix after singular value decomposition. This tag is a list of float numbers with 3 elements (x,y,z).");
            put("scale_z", "Scale the model centered on the origin. This tag corresponds to the singular values of the matrix after singular value decomposition. This tag is a list of float numbers with 3 elements (x,y,z).");
        }};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(literal("display").requires(source -> source.getBukkitSender().hasPermission("wea.display"))
            .then(literal("create")
                .then(literal("block").then(argument("block", BlockStateArgument.block(registryAccess))
                    .then(argument("location", Vec3Argument.vec3())
                        .executes(ctx -> createBlockDisplay(ctx, BlockStateArgument.getBlock(ctx, "block"), Vec3Argument.getVec3(ctx, "location"), ""))
                        .then(argument("data", StringArgumentType.greedyString())
                            .suggests((context, builder) -> {
                                builder.restart();
                                if(context.getInput().contains("billboard=")) {
                                    builder.suggest("fixed");
                                    builder.suggest("vertical");
                                    builder.suggest("horizontal");
                                    builder.suggest("center");
                                    return builder.buildFuture();
                                }
                                Suggestion suggestion = new Suggestion(new StringRange(1, 1), "lol", msg("c"));
                                SuggestionsBuilder builder1 = new SuggestionsBuilder(context.getInput(), context.getInput().length());
                                builder1.suggest("a", msg("b"));

                                for (Map.Entry<String, String> entry: POSSIBLE_DATA_BLOCK.entrySet()) {
                                    builder.suggest(entry.getKey() + "=", msg(entry.getValue()));
                                }
                                builder.add(builder1);
                                return builder.buildFuture();
                            })
                            .executes(ctx -> createBlockDisplay(ctx, BlockStateArgument.getBlock(ctx, "block"), Vec3Argument.getVec3(ctx, "location"), StringArgumentType.getString(ctx, "data"))))))))
            .then(literal("deleteall").requires(source -> source.getBukkitSender().hasPermission("command.display.deleteall"))
                .executes(ctx -> deleteAll(ctx, ""))
                .then(argument("confirm", StringArgumentType.word())
                    .executes(ctx -> deleteAll(ctx, StringArgumentType.getString(ctx, "confirm")))))
            .then(literal("configurator").then(argument("display", IntegerArgumentType.integer(0))
                .suggests((context, builder) -> {
                    List<String> nearby = new ArrayList<>();
                    // TODO: Entities in other worlds
                    for (org.bukkit.entity.Entity entity : context.getSource().getPlayer().getBukkitEntity().getNearbyEntities(15, 15, 15)) {
                        nearby.add(String.valueOf(entity.getEntityId()));
                    }
                    return SharedSuggestionProvider.suggest(nearby, builder);
                })
                .executes(ctx -> configurator(ctx, IntegerArgumentType.getInteger(ctx, "display")))))
            .then(literal("list").executes(ctx -> list(ctx, null))
                .then(argument("world", DimensionArgument.dimension()).executes(ctx -> list(ctx, DimensionArgument.getDimension(ctx, "world"))))));
    }

    private static int createBlockDisplay(CommandContext<CommandSourceStack> ctx, BlockInput block, Vec3 loc, String extra) {
        Location bukkitLocation = new Location(Objects.requireNonNull(ctx.getSource().getBukkitWorld(), "The world can't be null!"), loc.x(), loc.y(), loc.z());

        try {
            Constructor<CraftBlockData> constructor = CraftBlockData.class.getDeclaredConstructor(BlockState.class);
            constructor.setAccessible(true);
            CraftBlockData data = constructor.newInstance(block.getState());

            ctx.getSource().getBukkitWorld().spawn(bukkitLocation, BlockDisplay.class, CreatureSpawnEvent.SpawnReason.COMMAND, blockDisplay -> {
                blockDisplay.setBlock(data);
            });

            ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                "<green>Created <dark_gray>Block Display at <aqua>%s %s %s <dark_gray>with block state <aqua>%s".formatted(
                    loc.x(), loc.y(), loc.z(), data.getAsString()
                )
            )), false);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            WEAddon.get().getLogger().log(Level.SEVERE, "Unable to access CraftBlockState!", e);
            ctx.getSource().sendFailure(Component.literal("Failed to create Block Display \n" + e), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int deleteAll(CommandContext<CommandSourceStack> ctx, String confirm) {
        World world = Objects.requireNonNull(ctx.getSource().getBukkitWorld(), "The world can't be null");

        if (!confirm.equals("confirm")) {
            ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                "<gray>Do you really want to <red>delete</red> all displays? \n<hover:show_text:'<red>Click to delete'><click:run_command:/display deleteall confirm><green><bold>[YES]</bold><green></click></hover> <hover:show_text:'<green>Click to <b>not</b> delete'><click:run_command:/tellraw %s \"\"><dark_red><bold>[NO]</bold></dark_red></click></hover>"
                    .formatted(ctx.getSource().getTextName())
            )), true);
        } else {
             final AtomicInteger count = new AtomicInteger(0);

            world.getEntities().forEach(entity -> {
                if (entity instanceof Display) {
                    count.addAndGet(1);
                    ((CraftEntity) entity).getHandle().remove(Entity.RemovalReason.DISCARDED);
                }
            });
            ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                "<green>Deleted <red>%s <dark_gray>displays!".formatted(count.get())
            )), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int configurator(CommandContext<CommandSourceStack> ctx, int id) {
        ServerLevel world = Objects.requireNonNull(ctx.getSource().getLevel(), "World can't be null!");
        Entity entity = world.getEntity(id);
        if (entity instanceof net.minecraft.world.entity.Display display) {
            ItemStack configuratorBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) configuratorBook.getItemMeta();
            meta.setGeneration(BookMeta.Generation.ORIGINAL);
            meta.author(net.kyori.adventure.text.Component.text("server"));
            meta.title(MiniMessage.miniMessage().deserialize(
                "<white>Configurator for</white> %s".formatted(MiniMessage.miniMessage().serialize(PaperAdventure.asAdventure(entity.getName())))
            ));
            meta.displayName(net.kyori.adventure.text.Component.text("Configurator").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                net.kyori.adventure.text.Component.empty(),
                net.kyori.adventure.text.Component.text("Allows you to configure the", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                net.kyori.adventure.text.Component.text("display with an easy interface", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                net.kyori.adventure.text.Component.empty()
            ));
            meta.addPages(MiniMessage.miniMessage().deserialize(
                "How to use \n\nClick on the numbers and texts in the configurator that you want to choose"
            ));
            configuratorBook.setItemMeta(meta);

            if (ctx.getSource().getPlayer().getBukkitEntity().getInventory().firstEmpty() == -1) {
                ctx.getSource().sendFailure(Component.literal("Your inventory is full!"), true);
            } else {
                ctx.getSource().getPlayer().getBukkitEntity().getInventory().addItem(configuratorBook);
            }
        } else {
            ctx.getSource().sendFailure(PaperAdventure.asVanilla(PaperAdventure.asAdventure(entity.getName()).append(net.kyori.adventure.text.Component.text(" is not a display!"))), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int list(CommandContext<CommandSourceStack> ctx, @Nullable ServerLevel world) {
        if (world == null) {
            DedicatedServer.getServer().server.getServer().getAllLevels().forEach(level -> {
                for (org.bukkit.entity.Entity entity : level.getWorld().getEntities()) {
                    if (entity instanceof Display) {
                        ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                            "<green>Found display <aqua>%s <green>with UUID <aqua>%s <green>and id <aqua>%s <green>at <aqua>%s %s %s %s %s <green>in world <aqua>%s"
                                .formatted(entity.getName(), entity.getUniqueId(), entity.getEntityId(), entity.getX(), entity.getY(), entity.getZ(), entity.getPitch(), entity.getYaw(), ((CraftWorld) entity.getWorld()).getHandle().dimension().location())
                        )), false);
                    }
                }
            });
        } else {
            for (org.bukkit.entity.Entity entity : world.getWorld().getEntities()) {
                if (entity instanceof Display) {
                    ctx.getSource().sendSuccess(() -> PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(
                        "<green>Found display <aqua>%s <green>with UUID <aqua>%s <green>and id <aqua>%s <green>at <aqua>%s %s %s %s %s <green>in world <aqua>%s"
                            .formatted(entity.getName(), entity.getUniqueId(), entity.getEntityId(), entity.getX(), entity.getY(), entity.getZ(), entity.getPitch(), entity.getYaw(), world.dimension().location())
                    )), false);
                }
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static Message msg(String msg) {
        return new LiteralMessage(msg);
    }

    private static CompletableFuture<Suggestions> suggest(List<String> strings, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(strings, builder);
    }
}
