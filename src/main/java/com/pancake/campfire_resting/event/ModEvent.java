package com.pancake.campfire_resting.event;

import com.pancake.campfire_resting.CampfireResting;
import com.pancake.campfire_resting.capability.RestingCap;
import com.pancake.campfire_resting.network.ModMessages;
import com.pancake.campfire_resting.network.message.CampfireRestingC2SPacket;
import com.pancake.campfire_resting.network.message.CampfireRestingS2CPacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ModEvent {
    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionHand hand = event.getHand();
        Level level = event.getLevel();
        BlockState blockState = level.getBlockState(event.getPos());
        if (hand != InteractionHand.MAIN_HAND || ! event.getEntity().isShiftKeyDown()) return;
        if (!level.isClientSide && blockState.is(CampfireResting.CAMPFIRE)) {
            ModMessages.sendToPlayer(new CampfireRestingS2CPacket(event.getPos().getX(),event.getPos().getY(),event.getPos().getZ()),(ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onTickLevelTick(TickEvent.LevelTickEvent event) {
        Level level = event.level;

        if(level.isClientSide) {
            LazyOptional<RestingCap> restingCap = RestingCap.get(level);
            restingCap.ifPresent(cap ->{
                if (cap.isResting()) {
                    int skipTime = cap.getSkipTime();
                    ((ClientLevel)level).setDayTime(level.getDayTime() + 10);
                    cap.setSkipTime(skipTime - 10);
                    if (skipTime <= 0) {
                        cap.setResting(false);
                        cap.setSkipTime(0);
                    }
                    ModMessages.sendToServer(new CampfireRestingC2SPacket(level.getDayTime(), skipTime, cap.isResting()));
                }
            });
        }
    }
}
