package me.drizzy.practice.statistics.menu;

import com.google.common.collect.Lists;
import me.drizzy.practice.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitLeaderboards;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardsMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&7Leaderboards";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new GlobalLeaderboardsButton());
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && kit.getGameRules().isRanked()) {
                buttons.put(buttons.size(), new KitLeaderboardsButton(kit));
            }
        }
        return buttons;
    }
    
    private static class KitLeaderboardsButton extends Button
    {
        private final Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            List<String> lore = Lists.newArrayList();
            lore.add(CC.MENU_BAR);
            int position = 1;
            for (final KitLeaderboards kitLeaderboards : this.kit.getRankedEloLeaderboards()) {
                Profile profile = Profile.getByUuid(Profile.getUUID(kitLeaderboards.getName()));
                if (position == 1 || position == 2 || position == 3) {
                    lore.add(" &a" + position + " &7&l| &b" + kitLeaderboards.getName() + "&7: &f" + kitLeaderboards.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                } else {
                    lore.add(" &7" + position + " &7&l| &b" + kitLeaderboards.getName() + "&7: &f" + kitLeaderboards.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                }                ++position;
            }
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b" + this.kit.getName() + " &7&l| &fTop 10").lore(lore).build();
        }
        
        @ConstructorProperties({ "kit" })
        public KitLeaderboardsButton(final Kit kit) {
            this.kit = kit;
        }
    }
    
    private static class GlobalLeaderboardsButton extends Button
    {
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore =new ArrayList<>();
            int position = 1;
            lore.add(CC.MENU_BAR);
            for (final KitLeaderboards kitLeaderboards : Profile.getGlobalEloLeaderboards()) {
                Profile profile = Profile.getByUuid(Profile.getUUID(kitLeaderboards.getName()));
                if (position == 1 || position == 2 || position == 3) {
                    lore.add(" &a" + position + " &7&l| &b" + kitLeaderboards.getName() + "&7: &f" + kitLeaderboards.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                } else {
                    lore.add(" &7" + position + " &7&l| &b" + kitLeaderboards.getName() + "&7: &f" + kitLeaderboards.getElo() + " &7(" + ChatColor.stripColor(profile.getEloLeague()) + "&7)");
                }
                ++position;
            }
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(Material.SUGAR).name("&bGlobal &7| &fTop 10").lore(lore).build();
        }
    }

 /*   @AllArgsConstructor
    private static class StatsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            Profile profile = Profile.getByUuid(player.getUniqueId());

            lore.add(CC.MENU_BAR);
            for ( Kit kit : Kit.getKits() ) {
                if (kit.getGameRules().isRanked()) {
                lore.add("&b" + kit.getName() + ": &f" + profile.getKitData().get(kit).getElo());
                }
            }
            lore.add(CC.MENU_BAR);
            lore.add("&aGlobal ELO: &f" + profile.getGlobalElo());
            lore.add("&aGlobal League: &f" + profile.getEloLeague());
            lore.add(CC.MENU_BAR);
            return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                    .name("&b&l" + player.getName() + " | Statistics")
                    .lore(lore)
                    .build();
        }

    }*/
}
