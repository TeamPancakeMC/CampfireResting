package com.pancake.campfire_resting.capability;

import com.pancake.campfire_resting.init.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestingCap implements INBTSerializable<CompoundTag> {
    private int skipTime = 0;
    private boolean isResting = false;
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("skipTime", skipTime);
        compoundTag.putBoolean("isResting", isResting);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        skipTime = compoundTag.getInt("skipTime");
        isResting = compoundTag.getBoolean("isResting");
    }

    public static LazyOptional<RestingCap> get(Level level) {
        return level.getCapability(ModCapability.RESTING_CAP);
    }

    public int getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(int skipTime) {
        this.skipTime = skipTime;
    }

    public boolean isResting() {
        return isResting;
    }

    public void setResting(boolean resting) {
        isResting = resting;
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<RestingCap> instance;

        public Provider() {
            instance = LazyOptional.of(RestingCap::new);
        }


        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return ModCapability.RESTING_CAP.orEmpty(cap, instance);
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")).deserializeNBT(nbt);
        }
    }
}