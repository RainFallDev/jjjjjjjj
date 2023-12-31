package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.menu.ProfileMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class StatsCommand {

    private final Array plugin;

    @Command(name = "", desc = "View Statistics of your profile")
    public void stats(@Sender Player player, @OptArg() Player target) {
        if (target == null) {
            Menu menu = new ProfileMenu(player);
            plugin.getMenuHandler().openMenu(menu, player);
            return;
        }
        Menu menu = new ProfileMenu(target);
        plugin.getMenuHandler().openMenu(menu, player);
    }
}
