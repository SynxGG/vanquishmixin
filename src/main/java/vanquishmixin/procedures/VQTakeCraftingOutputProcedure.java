package vanquishmixin.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class VQTakeCraftingOutputProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player) {
			System.out.println("[VQ] OUTPUT TAKEN - entity = " + entity);
			vanquishmixin.VQAdvancedCrafting.takeOutput(entity);
		}
	}
}