package xyz.refinedev.practice.task.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchBowCooldownTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        float timeInterval = (plugin.getConfigHandler().getBOW_COOLDOWN() * 1000.0f);

        for ( Player player : plugin.getServer().getOnlinePlayers()) {
            if (player == null) return;
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());

            if ((profile.isInFight()) && !profile.getBowCooldown().hasExpired()) {
                int seconds = Math.round(profile.getBowCooldown().getRemaining()) / 1000;

                player.setLevel(seconds);
                player.setExp(profile.getBowCooldown().getRemaining() / timeInterval);
            } else {
                if (profile.isInFight()) {
                    if (!profile.getBowCooldown().isNotified() && !profile.isInLobby()) {
                        profile.getBowCooldown().setNotified(true);
                        player.sendMessage(Locale.MATCH_BOW_COOLDOWN_EXPIRE.toString());
                    }
                }
                if (player.getLevel() > 0) player.setLevel(0);
                if (player.getExp() > 0.0F) player.setExp(0.0F);
            }
        }
    }
}