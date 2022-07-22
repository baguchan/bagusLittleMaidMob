package net.baguchan.littlemaidmob.message;

import net.baguchan.littlemaidmob.entity.MultiModelEntity;
import net.baguchan.littlemaidmob.entity.compound.MultiModelCompound;
import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncMaidModelMessage {
	private int entityId;
	private MultiModelCompound multiModel;

	public SyncMaidModelMessage(MultiModelEntity entity) {
		this.entityId = entity.getId();
		this.multiModel = entity.getMultiModel();
	}

	public SyncMaidModelMessage(int id, MultiModelCompound multiModelCompound) {
		this.entityId = id;
		this.multiModel = multiModelCompound;
	}

	public void serialize(FriendlyByteBuf buffer) {
		this.multiModel.writeToPacket(buffer);
	}

	public static SyncMaidModelMessage deserialize(FriendlyByteBuf buffer) {
		int entityId = buffer.readInt();
		MultiModelCompound multiModelCompound = new MultiModelCompound(null, new TextureHolder("",""), new TextureHolder("",""));
		multiModelCompound.readFromPacket(buffer);
		return new SyncMaidModelMessage(entityId, multiModelCompound);
	}

	public static boolean handle(SyncMaidModelMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();

			context.enqueueWork(() -> {
				Entity entity = context.getSender().level.getEntity(message.entityId);
				if (entity != null && entity instanceof MultiModelEntity) {
					((MultiModelEntity) entity).readMultiModel(message.multiModel);
				}
			});

		return true;
	}
}