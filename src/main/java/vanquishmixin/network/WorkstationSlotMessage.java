package vanquishmixin.network;

import vanquishmixin.procedures.VQUpdateCraftingOutputProcedure;
import vanquishmixin.procedures.VQTakeCraftingOutputProcedure;

import vanquishmixin.VanquishmixinMod;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.SectionPos;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record WorkstationSlotMessage(int slotID, int x, int y, int z, int changeType, int meta) {
	public WorkstationSlotMessage(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	public static void buffer(WorkstationSlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
	}

	public static void handler(WorkstationSlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleSlotAction(context.getSender(), message.slotID, message.changeType, message.meta, message.x, message.y, message.z));
		context.setPacketHandled(true);
	}

	public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
		Level world = entity.level();
		// security measure to prevent arbitrary chunk generation
		if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)))
			return;
		if (slot == 0 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 1 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 6 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 3 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 4 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 7 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 2 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 5 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 8 && changeType == 0) {

			VQUpdateCraftingOutputProcedure.execute(entity);
		}
		if (slot == 9 && changeType == 1) {
			int amount = meta;

			VQTakeCraftingOutputProcedure.execute(entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		VanquishmixinMod.addNetworkMessage(WorkstationSlotMessage.class, WorkstationSlotMessage::buffer, WorkstationSlotMessage::new, WorkstationSlotMessage::handler);
	}
}