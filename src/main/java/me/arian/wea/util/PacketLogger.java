package me.arian.wea.util;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import me.arian.wea.WEAddon;

public class PacketLogger implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
//        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(("<dark_gray>Received packet <aqua>%s <dark_gray>with ID <red>%s <dark_gray>| Posttask: <green>%s")
//            .formatted(event.getPacketType().getName(), event.getPacketId(), event.hasPostTasks()))
////            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
////                "<green>Every known information\n<red>Address: %s\n<aqua>Channel: %s\n<green>Connection state: %s\n<yellow>Client version: %s\n<white>Protocol version: %s\n<blue>Decoder state: %s\n<gray>Dimension: %s\n<white>Entity id: %s\n<dark_gray>UUID: %s"
////                    .formatted(
////                        event.getUser().getAddress().toString(),
////                        event.getUser().getChannel(),
////                        event.getUser().getConnectionState().name(),
////                        event.getUser().getClientVersion().name(),
////                        event.getUser().getClientVersion().getProtocolVersion(),
////                        event.getUser().getDecoderState().name(),
////                        event.getUser().getDimension().getDimensionName(),
////                        event.getUser().getEntityId(),
////                        event.getUser().getUUID()
////                    )
////            )))
//           );
        WEAddon.get().getLogger().info("Received packet %s with ID %s | Posttask: %s".formatted(event.getPacketType().getName(), event.getPacketId(), event.hasPostTasks()));
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
//        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(("<dark_gray>Send packet <aqua>%s <dark_gray>with ID <red>%s <dark_gray>| Posttask: <green>%s")
//            .formatted(event.getPacketType().getName(), event.getPacketId(), event.hasPostTasks()))
////            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
////                "<green>Every known information\n<red>Address: %s\n<aqua>Channel: %s\n<green>Connection state: %s\n<yellow>Client version: %s\n<white>Protocol version: %s\n<blue>Decoder state: %s\n<gray>Dimension: %s\n<white>Entity id: %s\n<dark_gray>UUID: %s"
////                    .formatted(
////                        event.getUser().getAddress().toString(),
////                        event.getUser().getChannel(),
////                        event.getUser().getConnectionState().name(),
////                        event.getUser().getClientVersion().name(),
////                        event.getUser().getClientVersion().getProtocolVersion(),
////                        event.getUser().getDecoderState().name(),
////                        event.getUser().getDimension().getDimensionName(),
////                        event.getUser().getEntityId(),
////                        event.getUser().getUUID()
////                    )
////            )))
//            );

        WEAddon.get().getLogger().info("Send packet %s with ID %s | Posttask: %s".formatted(event.getPacketType().getName(), event.getPacketId(), event.hasPostTasks()));
    }

    @Override
    public void onPacketEventExternal(PacketEvent event) {
        WEAddon.get().getLogger().info("External packet event at %sms".formatted(event.getTimestamp()));
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
//        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(("<dark_gray>Player <aqua>%s <dark_gray>joined at <red>%sms")
//            .formatted(event.getUser().getName(), event.getTimestamp()))
////            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
////                "<green>Every known information\n<red>Address: %s\n<aqua>Channel: %s\n<green>Connection state: %s\n<yellow>Client version: %s\n<white>Protocol version: %s\n<blue>Decoder state: %s\n<gray>Dimension: %s\n<white>Entity id: %s\n<dark_gray>UUID: %s"
////                    .formatted(
////                        event.getUser().getAddress().toString(),
////                        event.getUser().getChannel(),
////                        event.getUser().getConnectionState().name(),
////                        event.getUser().getClientVersion().name(),
////                        event.getUser().getClientVersion().getProtocolVersion(),
////                        event.getUser().getDecoderState().name(),
////                        event.getUser().getDimension().getDimensionName(),
////                        event.getUser().getEntityId(),
////                        event.getUser().getUUID()
////                    )
////            )))
//            );
        WEAddon.get().getLogger().info("Player %s connected at %sms".formatted(event.getUser().getName(), event.getTimestamp()));
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
//        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(("<dark_gray>Player <aqua>%s <dark_gray>logged in at <red>%sms")
//            .formatted(event.getUser().getName(), event.getTimestamp()))
////            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
////                "<green>Every known information\n<red>Address: %s\n<aqua>Channel: %s\n<green>Connection state: %s\n<yellow>Client version: %s\n<white>Protocol version: %s\n<blue>Decoder state: %s\n<gray>Dimension: %s\n<white>Entity id: %s\n<dark_gray>UUID: %s"
////                    .formatted(
////                        event.getUser().getAddress().toString(),
////                        event.getUser().getChannel(),
////                        event.getUser().getConnectionState().name(),
////                        event.getUser().getClientVersion().name(),
////                        event.getUser().getClientVersion().getProtocolVersion(),
////                        event.getUser().getDecoderState().name(),
////                        event.getUser().getDimension().getDimensionName(),
////                        event.getUser().getEntityId(),
////                        event.getUser().getUUID()
////                    )
////            )))
//            );
        WEAddon.get().getLogger().info("Player %s logged in at %sms".formatted(event.getUser().getName(), event.getTimestamp()));
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
//        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(("<dark_gray>Player <aqua>%s <dark_gray>quit at <red>%sms")
//            .formatted(event.getUser().getName(), event.getTimestamp()))
////            .hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(
////                "<green>Every known information\n<red>Address: %s\n<aqua>Channel: %s\n<green>Connection state: %s\n<yellow>Client version: %s\n<white>Protocol version: %s\n<blue>Decoder state: %s\n<gray>Dimension: %s\n<white>Entity id: %s\n<dark_gray>UUID: %s"
////                    .formatted(
////                        event.getUser().getAddress().toString(),
////                        event.getUser().getChannel(),
////                        event.getUser().getConnectionState().name(),
////                        event.getUser().getClientVersion().name(),
////                        event.getUser().getClientVersion().getProtocolVersion(),
////                        event.getUser().getDecoderState().name(),
////                        event.getUser().getDimension().getDimensionName(),
////                        event.getUser().getEntityId(),
////                        event.getUser().getUUID()
////                    )
////            )))
//            );
        WEAddon.get().getLogger().info("Player %s quit at %sms".formatted(event.getUser().getName(), event.getTimestamp()));
    }
}
