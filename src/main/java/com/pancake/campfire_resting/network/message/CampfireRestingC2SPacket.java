package com.pancake.campfire_resting.network.message;


import com.pancake.campfire_resting.capability.RestingCap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CampfireRestingC2SPacket {


    private final long dayTime;
    private final int skipTime;
    private final boolean resting;

    public CampfireRestingC2SPacket(long dayTime, int skipTime, boolean resting) {
        this.dayTime = dayTime;
        this.skipTime = skipTime;
        this.resting = resting;
    }


    public CampfireRestingC2SPacket(FriendlyByteBuf buf){
        this.dayTime = buf.readLong();
        this.skipTime = buf.readInt();
        this.resting = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeLong(dayTime);
        buf.writeInt(skipTime);
        buf.writeBoolean(resting);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            Level level = player.level;

            RestingCap.get(level).ifPresent(restingCap -> {
                if (level instanceof  ServerLevel serverLevel){
                    serverLevel.setDayTime(dayTime);
                    restingCap.setResting(resting);
                    restingCap.setSkipTime(skipTime);
                }
            });
        });
    }
}
