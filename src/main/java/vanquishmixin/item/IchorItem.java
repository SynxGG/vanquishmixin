package vanquishmixin.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class IchorItem extends Item {
	public IchorItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON));
	}
}