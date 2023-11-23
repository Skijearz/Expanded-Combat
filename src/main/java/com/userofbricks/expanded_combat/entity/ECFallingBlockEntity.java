package com.userofbricks.expanded_combat.entity;

import com.userofbricks.expanded_combat.init.ECEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ECFallingBlockEntity extends Entity {
    public int duration;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(ECFallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(ECFallingBlockEntity.class, EntityDataSerializers.BLOCK_STATE);

    public ECFallingBlockEntity(EntityType<ECFallingBlockEntity> type, Level level) {
        super(type, level);
        this.duration = 20;
    }

    public ECFallingBlockEntity(Level level, double posX, double posY, double posZ, BlockState blockState, int duration) {
        this(ECEntities.EC_FALLING_BLOCK.get(), level);
        this.setBlockState(blockState);
        this.setPos(posX, posY + (double)((1.0F - this.getBbHeight()) / 2.0F), posZ);
        this.setDeltaMovement(Vec3.ZERO);
        this.duration = duration;
        this.xo = posX;
        this.yo = posY;
        this.zo = posZ;
        this.setStartPos(this.blockPosition());
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
        this.entityData.define(BLOCK_STATE, Blocks.AIR.defaultBlockState());
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(DATA_START_POS, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public void setBlockState(BlockState p_270267_) {
        this.entityData.set(BLOCK_STATE, p_270267_);
    }

    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround() && this.tickCount > this.duration) {
            this.discard();
        }

        if (this.tickCount > 300) {
            this.discard();
        }

    }

    protected void addAdditionalSaveData(CompoundTag p_31973_) {
        BlockState blockState = this.getBlockState();
        p_31973_.put("block_state", NbtUtils.writeBlockState(blockState));
        p_31973_.putInt("Time", this.duration);
    }

    protected void readAdditionalSaveData(CompoundTag p_31964_) {
        this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), p_31964_.getCompound("block_state")));
        this.duration = p_31964_.getInt("Time");
    }

    public boolean displayFireAnimation() {
        return false;
    }

    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
