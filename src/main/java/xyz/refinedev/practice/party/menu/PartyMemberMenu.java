package xyz.refinedev.practice.party.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.party.enums.PartyManageType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PartyMemberMenu extends Menu {

    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&cSelect an action for &9" + this.target.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new SelectManageButton(PartyManageType.LEADER));
        buttons.put(4, new SelectManageButton(PartyManageType.KICK));
        buttons.put(6, new SelectManageButton(PartyManageType.BAN));
        return buttons;
    }

    @RequiredArgsConstructor
    private class SelectManageButton extends Button {

        private final PartyManageType partyManageType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Player target = PartyMemberMenu.this.target;
            if (this.partyManageType == PartyManageType.LEADER) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to make &c" + target.getName());
                lore.add("&7the party leader, this will grant them");
                lore.add("&c&lCOMPLETE &7control of the party!");
                lore.add("");
                lore.add("&cClick to Grant party leadership.");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.GOLD_SWORD).name("&c" + this.partyManageType.getName()).lore(lore).build();
            }
            if (this.partyManageType == PartyManageType.KICK) {
                lore.add(CC.MENU_BAR);
                lore.add("&7Click here to Kick &c" + target.getName());
                lore.add("&7this will make them leave the party");
                lore.add("&7but, they can join back unless invited");
                lore.add("");
                lore.add("&cClick to Kick " + target.getName() + ".");
                lore.add(CC.MENU_BAR);
                return new ItemBuilder(Material.BOOK).name("&c" + this.partyManageType.getName()).lore(lore).build();
            }
            lore.add(CC.MENU_BAR);
            lore.add("&7Click here to Ban &c" + target.getName());
            lore.add("&7this will make them leave the party");
            lore.add("&7and they will not be able to join back");
            lore.add("&7unless unbanned manually!");
            lore.add("");
            lore.add("&cClick to Ban " + target.getName() + ".");
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SKULL_ITEM).name("&c" + this.partyManageType.getName()).lore(lore).build();
        }
        
        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (this.partyManageType == PartyManageType.LEADER) {
                profile.getParty().leader(player, PartyMemberMenu.this.target);
            } else if (this.partyManageType == PartyManageType.MANAGE) {
                profile.getParty().leave(PartyMemberMenu.this.target, true);
            } else if (this.partyManageType == PartyManageType.BAN) {
                profile.getParty().leave(PartyMemberMenu.this.target, true);
                profile.getParty().ban(PartyMemberMenu.this.target);
            }
            player.closeInventory();
        }
    }
}
