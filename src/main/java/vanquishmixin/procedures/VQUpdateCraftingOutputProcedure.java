package vanquishmixin.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class VQUpdateCraftingOutputProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player) {
			System.out.println("[VQ] INPUT CHANGED - entity = " + entity);
			vanquishmixin.VQAdvancedCrafting.updateOutput(entity);
		}
	}
}