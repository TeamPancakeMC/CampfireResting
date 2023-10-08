package com.pancake.campfire_resting.network;

import com.pancake.campfire_resting.CampfireResting;
import com.pancake.campfire_resting.network.message.CampfireRestingC2SPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(CampfireResting.asResource("messages"))
                .networkProtocolVersion(()->"1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;


        net.messageBuilder(CampfireRestingC2SPacket.class,id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CampfireRestingC2SPacket::new)
                .encoder(CampfireRestingC2SPacket::toBytes)
                .consumerMainThread(CampfireRestingC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()-> player),message);
    }
}