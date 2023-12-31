package xyz.refinedev.practice.util.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.TypeCallback;

@RequiredArgsConstructor
public class ConfirmationButton extends Button {

	private final Array plugin;
	private final boolean confirm;
	private final TypeCallback<Boolean> callback;
	private final boolean closeAfterResponse;

	@Override
	public ItemStack getButtonItem(Array plugin, Player player) {
		ItemStack itemStack = new ItemStack(Material.WOOL, 1, this.confirm ? ((byte) 5) : ((byte) 14));
		ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setDisplayName(this.confirm ? CC.GREEN + "Confirm" : CC.RED + "Cancel");
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	@Override
	public void clicked(Array plugin, Player player, ClickType clickType) {
		if (this.confirm) player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
		else player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20f, 0.1F);
		if (this.closeAfterResponse) {
			Menu menu = plugin.getMenuHandler().getOpenedMenus().get(player.getUniqueId());
			if (menu != null) menu.setClosedByMenu(true);
			player.closeInventory();
		}
		this.callback.callback(this.confirm);
	}
}
