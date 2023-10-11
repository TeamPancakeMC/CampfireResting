package com.pancake.campfire_resting.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pancake.campfire_resting.CampfireResting;
import com.pancake.campfire_resting.Time;
import com.pancake.campfire_resting.capability.RestingCap;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CampfireGUIScreen extends Screen {
    private static final ResourceLocation DUSK = CampfireResting.asResource("textures/gui/dusk.png");

    private static final ResourceLocation EARLY_MORNING = CampfireResting.asResource("textures/gui/early_morning.png");

    private static final ResourceLocation MIDNIGHT = CampfireResting.asResource("textures/gui/midnight.png");

    private static final ResourceLocation NOON = CampfireResting.asResource("textures/gui/noon.png");
    private final BlockPos pos;

    private ImageButton dusk;
    private ImageButton early_morning;
    private ImageButton midnight;
    private ImageButton noon;
    private int select;

    public CampfireGUIScreen(BlockPos pos) {
        super(new TranslatableComponent("gui.campfire_resting.rest"));
        this.pos = pos;
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(p_96562_);
        this.dusk.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.early_morning.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.midnight.render(p_96562_, p_96563_, p_96564_, p_96565_);
        this.noon.render(p_96562_, p_96563_, p_96564_, p_96565_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    protected void init() {
        //sunrise
        ImageButton early_morning = new ImageButton(this.width / 2 - 100, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, EARLY_MORNING,40,80 ,(onPress) -> select = 1,new TranslatableComponent("gui.campfire_resting.early_morningzg"));

        //sun
        ImageButton noon = new ImageButton(this.width / 2 - 40, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, NOON,40,80 ,(onPress) -> select = 2,new TranslatableComponent("gui.campfire_resting.noon"));

        //moon
        ImageButton dusk = new ImageButton(this.width / 2 + 20 , this.height / 2 - 30,
                40, 40,
                0, 0,
                40, DUSK,40,80 ,(onPress) -> select = 3,new TranslatableComponent("gui.campfire_resting.dusk"));

        //moon_night
        ImageButton midnight = new ImageButton(this.width / 2 + 80, this.height / 2 - 30,
                40, 40,
                0, 0,
                40, MIDNIGHT,40,80 ,(onPress) -> select = 4,new TranslatableComponent("gui.campfire_resting.midnight"));

        this.dusk = this.addRenderableWidget(dusk);
        this.early_morning = this.addRenderableWidget(early_morning);
        this.midnight = this.addRenderableWidget(midnight);
        this.noon = this.addRenderableWidget(noon);
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
        Time[] times = { Time.EARLY_MORNING, Time.NOON , Time.DUSK,Time.MIDNIGHT};

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