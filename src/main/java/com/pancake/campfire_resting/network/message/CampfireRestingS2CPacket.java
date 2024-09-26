package com.pancake.campfire_resting.network.message;

import com.pancake.campfire_resting.client.gui.CampfireGUIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CampfireRestingS2CPacket {
    int x;
    int y;
    int z;

    public CampfireRestingS2CPacket(int x,int y,int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CampfireRestingS2CPacket(FriendlyByteBuf buf){
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public void clientHandle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandle.handlePacket(x,y,z,supplier)));
        }

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientHandle {
    public static void handlePacket(int x,int y,int z, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        if (context.getDirection().getReceptionSide().isClient())
        {
            context.enqueueWork(()->
                    Minecraft.getInstance().setScreen(new CampfireGUIScreen(new BlockPos(x,y,z))));
        }
    }

}
