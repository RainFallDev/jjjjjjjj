package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.managers.*;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.kit.TeamFightMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.party.menu.PartyKitMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.ButtonUtil;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyEventButton extends Button {

    private final BasicConfigurationFile config;
    private final PartyEventType partyEventType;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        String key = "BUTTONS." + partyEventType.name() + ".";

        Material material = ButtonUtil.getMaterial(config, key);
        ItemBuilder itemBuilder = new ItemBuilder(material);

        itemBuilder.name(config.getString(key + "NAME"));
        itemBuilder.lore(config.getStringList(key + "LORE"));
        itemBuilder.clearFlags();

        return itemBuilder.build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        MatchManager matchManager = plugin.getMatchManager();
        PartyManager partyManager = plugin.getPartyManager();
        ArenaManager arenaManager = plugin.getArenaManager();
        KitManager kitManager = plugin.getKitManager();

        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());

        player.closeInventory();

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }

        if (plugin.getPartyManager().isFighting(party.getUniqueId()) || party.isInTournament()) {
            player.sendMessage(Locale.PARTY_BUSY.toString());
            return;
        }

        if (party.getPlayers().size() < 2) {
            player.sendMessage(Locale.PARTY_EVENT_NEED.toString());
            return;
        }

        if (this.partyEventType == PartyEventType.PARTY_FFA || this.partyEventType == PartyEventType.PARTY_SPLIT) {
            Menu menu = new PartyKitMenu(this.partyEventType);
            plugin.getMenuHandler().openMenu(menu, player);
            return;
        }

        Arena arena = arenaManager.getByKit(kitManager.getTeamFight());

        if (arena == null) {
            player.sendMessage(Locale.ERROR_NO_ARENAS.toString());
            return;
        }

        Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
        Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

        List<Player> players = new ArrayList<>(party.getPlayers());

        //Doing a thorough shuffle, because some people complaining it keeps the same team
        Collections.shuffle(players);
        Collections.reverse(players);
        Collections.shuffle(players);
        Collections.reverse(players);

        Match match = new TeamFightMatch(teamA, teamB, arena);

        //Add players to the newly created teams
        for ( Player otherPlayer : players ) {
            if (!teamA.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                if (teamB.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                    continue;
                }
                if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                    teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                } else {
                    teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            }
        }
        matchManager.start(match);
    }
}