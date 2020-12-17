package me.array.ArrayPractice.profile.command.staff;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.options.OptionsMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "stats", "statistics", "elo", "stat" })
public class StatsCommand
{
    public void execute(final Player player) {
        new OptionsMenu().openMenu(player);
        player.sendMessage(ChatColor.GRAY + "Now viewing stats menu.");
    }
}
