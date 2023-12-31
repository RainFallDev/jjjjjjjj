package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.config.ConfigHandler;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.queue.menu.QueueSelectKitMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.MenuHandler;
import xyz.refinedev.practice.util.other.PlayerUtil;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/4/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class RankedQueueCMD {

    private final Array plugin;

    @Command(name = "", desc = "Open ranked queue menu")
    public void queue(@Sender Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        ConfigHandler configHandler = plugin.getConfigHandler();
        MenuHandler menuHandler = plugin.getMenuHandler();
        Profile profile = profileManager.getProfile(player);

        if (!configHandler.isRANKED_ENABLED()) {
            player.sendMessage(Locale.RANKED_DISABLED.toString());
            return;
        }

        if (configHandler.isLIMIT_PING()) {
            if (PlayerUtil.getPing(player) > configHandler.getPING_LIMIT()) {
                player.sendMessage(Locale.ERROR_PING_TOO_HIGH.toString().replace("<ping_limit>", String.valueOf(configHandler.getPING_LIMIT())));
                return;
            }
        }

        if (!player.hasPermission("array.profile.ranked")) {
            if (configHandler.isREQUIRE_KILLS()) {
                if (profile.getTotalWins() < configHandler.getREQUIRED_KILLS()) {
                    int i = configHandler.getREQUIRED_KILLS() - profile.getTotalWins();
                    Locale.RANKED_REQUIRED.toList().forEach(line -> player.sendMessage(line.replace("<match_limit>", String.valueOf(i))));
                    return;
                }
            }
        }

        if (!profile.isBusy()) {
            Menu menu = new QueueSelectKitMenu(QueueType.RANKED);
            menuHandler.openMenu(menu, player);
        } else {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
    }
}
