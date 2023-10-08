package com.pancake.campfire_resting.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pancake.campfire_resting.CampfireResting;
import com.pancake.campfire_resting.Time;
import com.pancake.campfire_resting.capability.RestingCap;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CampfireGUIScreen extends Screen {
    private static final ResourceLocation MOON = CampfireResting.asResource("textures/gui/moon.png");

    private static final ResourceLocation SUN = CampfireResting.asResource("textures/gui/sun.png");

    private static final ResourceLocation SUNRISE = CampfireResting.asResource("textures/gui/sunrise.png");

    private static final ResourceLocation MOON_NIGHT = CampfireResting.asResource("textures/gui/moon_night.png");
    private final BlockPos pos;

    private ImageButton sunrise;
    private ImageButton sun;
    private ImageButton moon;
    private ImageButton moon_night;
    private int select;

    public CampfireGUIScreen(BlockPos pos) {
        super(new TranslatableComponent("gui.campfire_resting.rest"));
        this.pos = pos;
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(p_96562_);
        this.sunrise.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.sun.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.moon.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.moon_night.render(p_96562_, p_96563_, p_96564_, p_96565_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }


    @Override
    protected void init() {
        //sunrise
        ImageButton sunrise = new ImageButton(this.width / 2 - 100, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, SUNRISE,40,80 ,(onPress) -> select = 1,new TranslatableComponent("gui.campfire_resting.sunrise"));

        //sun
        ImageButton sun = new ImageButton(this.width / 2 - 40, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, SUN,40,80 ,(onPress) -> select = 2,new TranslatableComponent("gui.campfire_resting.sun"));

        //moon
        ImageButton moon = new ImageButton(this.width / 2 + 20 , this.height / 2 - 30,
                40, 40,
                0, 0,
                40, MOON,40,80 ,(onPress) -> select = 3,new TranslatableComponent("gui.campfire_resting.moon"));

        //moon_night
        ImageButton moon_night = new ImageButton(this.width / 2 + 80, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, MOON_NIGHT,40,80 ,(onPress) -> select = 4,new TranslatableComponent("gui.campfire_resting.moon_night"));

        this.sunrise = this.addRenderableWidget(sunrise);
        this.sun = this.addRenderableWidget(sun);
        this.moon = this.addRenderableWidget(moon);
        this.moon_night = this.addRenderableWidget(moon_night);
        super.init();
    }

    @Override
    public void onClose() {
        ClientLevel level = minecraft.level;
        if (level != null && isPlayerNearby(level, pos)){
            RestingCap.get(level).ifPresent(restingCap -> {
                int skipTime = getSkipTime(level, select);
                restingCap.setSkipTime(skipTime);
                restingCap.setResting(true);
            });
        }
        super.onClose();
    }

    private boolean isPlayerNearby(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(8.0D, 8.0D, 8.0D)).stream()
                .filter((player) -> !player.isSpectator())
                .count() > level.players().size() / 2;
    }

    public int getSkipTime(Level level, int select) {
        long dayTime = level.getDayTime() % 24000;
        Time[] times = {Time.DAY, Time.NOON, Time.MIDNIGHT, Time.NIGHT};

        if (select >= 1 && select <= times.length) {
            Time selectedTime = times[select - 1];
            if (dayTime < selectedTime.getTime()) {
                return (int) (selectedTime.getTime() - dayTime);
            } else {
                return (int) (24000 - dayTime + selectedTime.getTime());
            }
        } else {
            return 0;
        }
    }
}