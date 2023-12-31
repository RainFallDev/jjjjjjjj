package xyz.refinedev.practice.leaderboards.menu.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/13/2021
 * Project: Array
 */

public class GlobalLeaderboardsButton extends Button {

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        List<String> lore = new ArrayList<>();

        int position = 1;
        lore.add(CC.MENU_BAR);
        for ( LeaderboardsAdapter leaderboardsAdapter : plugin.getLeaderboardsManager().getGlobalLeaderboards()) {
            Profile profile = plugin.getProfileManager().getProfile(leaderboardsAdapter.getUniqueId());

            lore.add(Locale.LEADERBOARDS_GLOBAL_FORMAT.toString()
                    .replace("<leaderboards_pos>", String.valueOf(position))
                    .replace("<leaderboards_name>", leaderboardsAdapter.getName())
                    .replace("<leaderboards_elo>", String.valueOf(leaderboardsAdapter.getElo()))
                    .replace("<leaderboards_division>", plugin.getProfileManager().getDivision(profile).getDisplayName()));
            position++;
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.SUGAR)
                .name("&aGlobal Leaderboards")
                .clearFlags()
                .lore(lore)
                .build();
    }
}
