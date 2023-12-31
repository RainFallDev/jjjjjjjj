package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.MatchManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SpectateCommand {

    private final Array plugin;

    @Command(name = "", desc = "Spectate a target player", usage = "<target>")
    public void spectate(@Sender Player player, Player target) {
        ProfileManager profileManager = plugin.getProfileManager();
        MatchManager matchManager = plugin.getMatchManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());

        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        if (profile.hasParty()) {
            player.sendMessage(Locale.ERROR_PARTY.toString());
            return;
        }

        Profile targetProfile = profileManager.getProfile(target.getUniqueId());

        if (!targetProfile.getSettings().isAllowSpectators() && !profile.isSilent()) {
            player.sendMessage(Locale.ERROR_NOSPEC.toString());
            return;
        }

        if (!targetProfile.isInFight() && !targetProfile.isInEvent()) {
            player.sendMessage(Locale.ERROR_FREE.toString());
            return;
        }

        if (targetProfile.getMatch() != null && !targetProfile.getMatch().isFreeForAllMatch()) {
            for ( TeamPlayer teamPlayer : targetProfile.getMatch().getTeamPlayers() ) {
                Player inMatchPlayer = teamPlayer.getPlayer();
                Profile inMatchProfile = profileManager.getProfile(inMatchPlayer.getUniqueId());

                if (!inMatchProfile.getSettings().isAllowSpectators() && !profile.isSilent()) {
                    player.sendMessage(Locale.ERROR_MATCHNOSPEC.toString());
                    return;
                }
            }
        }

        if (targetProfile.isInFight() || targetProfile.isInTournament()) {
            Match match = profile.getMatch();
            matchManager.addSpectator(match, player, target);
        }/* else if (targetProfile.isInEvent()) {
            Event event = this.plugin.getEventManager().getEventByUUID(profile.getEvent());
            this.plugin.getEventManager().addSpectator(event, player.getUniqueId());
        }*/
    }

    @Command(name = "show", aliases = "view", desc = "Show spectators")
    public void show(@Sender Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        MatchManager matchManager = plugin.getMatchManager();

        Profile profile = profileManager.getProfile(player);
        if (!profile.isInMatch()) {
            player.sendMessage(Locale.ERROR_TARGET_NOT_IN_MATCH.toString());
            return;
        } else if (!profile.isSpectating()) {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
            return;
        }
        matchManager.toggleSpectators(profile.getMatch(), player);
    }

    @Command(name = "hide", aliases = "disable", desc = "Hide spectators")
    public void hide(@Sender Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        MatchManager matchManager = plugin.getMatchManager();

        Profile profile = profileManager.getProfile(player);
        if (!profile.isInMatch()) {
            player.sendMessage(Locale.ERROR_TARGET_NOT_IN_MATCH.toString());
            return;
        } else if (!profile.isSpectating()) {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
            return;
        }
        matchManager.toggleSpectators(profile.getMatch(), player);
    }
}
