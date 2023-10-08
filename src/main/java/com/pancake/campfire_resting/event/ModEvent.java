package com.pancake.campfire_resting.event;

import com.pancake.campfire_resting.capability.RestingCap;
import com.pancake.campfire_resting.client.gui.CampfireGUIScreen;
import com.pancake.campfire_resting.network.ModMessages;
import com.pancake.campfire_resting.network.message.CampfireRestingC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
        Level level = event.getWorld();
        if (hand != InteractionHand.MAIN_HAND) return;
        if (level.getBlockState(event.getPos()).getBlock() != Blocks.CAMPFIRE) return;
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new CampfireGUIScreen(event.getPos()));
        }
    }

    @SubscribeEvent
    public static void onTickWorldTick(TickEvent.WorldTickEvent event) {
        Level level = event.world;

        if(level instanceof ClientLevel clientLevel) {
            LazyOptional<RestingCap> restingCap = RestingCap.get(level);
            restingCap.ifPresent(cap ->{
                if (cap.isResting()) {
                    int skipTime = cap.getSkipTime();
                    clientLevel.setDayTime(clientLevel.getDayTime() + 10);
                    cap.setSkipTime(skipTime - 10);
                    if (skipTime <= 0) {
                        cap.setResting(false);
                        cap.setSkipTime(0);
                    }
                    ModMessages.sendToServer(new CampfireRestingC2SPacket(clientLevel.getDayTime(), skipTime, cap.isResting()));
                }
            });
        }
    }
}
