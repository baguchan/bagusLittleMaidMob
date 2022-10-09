package net.baguchan.bagus_littlemaidmob.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ClientboundAddMaidPacket extends ClientboundAddEntityPacket {
	public final CompoundTag compoundTag;

	public ClientboundAddMaidPacket(LivingEntity p_237562_, CompoundTag compoundTag) {
		super(p_237562_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(LivingEntity p_237564_, int p_237565_, CompoundTag compoundTag) {
		super(p_237564_, p_237565_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(Entity p_131481_, CompoundTag compoundTag) {
		super(p_131481_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(Entity p_131483_, int p_131484_, CompoundTag compoundTag) {
		super(p_131483_, p_131484_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(Entity p_237558_, int p_237559_, BlockPos p_237560_, CompoundTag compoundTag) {
		super(p_237558_, p_237559_, p_237560_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(int p_237546_, UUID p_237547_, double p_237548_, double p_237549_, double p_237550_, float p_237551_, float p_237552_, EntityType<?> p_237553_, int p_237554_, Vec3 p_237555_, double p_237556_, CompoundTag compoundTag) {
		super(p_237546_, p_237547_, p_237548_, p_237549_, p_237550_, p_237551_, p_237552_, p_237553_, p_237554_, p_237555_, p_237556_);
		this.compoundTag = compoundTag;
	}

	public ClientboundAddMaidPacket(FriendlyByteBuf p_178562_) {
		super(p_178562_);
		this.compoundTag = p_178562_.readNbt();
	}
}
