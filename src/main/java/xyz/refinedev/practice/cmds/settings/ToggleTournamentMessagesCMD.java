package xyz.refinedev.practice.cmds.settings;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.ProfileSettings;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */
@RequiredArgsConstructor
public class ToggleTournamentMessagesCMD {

    private final Array plugin;

    @Command(name = "", desc = "Toggle Tournament Messages for your Profile")
    public void toggle(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        ProfileSettings settings = profile.getSettings();

        settings.setTournamentMessages(!settings.isTournamentMessages());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Tournament Messages");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Tournament Messages");

        player.sendMessage(settings.isReceiveDuelRequests() ? enabled : disabled);
    }

}
