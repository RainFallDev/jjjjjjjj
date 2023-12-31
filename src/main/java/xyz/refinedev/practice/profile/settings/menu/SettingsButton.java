package xyz.refinedev.practice.profile.settings.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.menu.KillEffectMenu;
import xyz.refinedev.practice.profile.settings.ProfileSettingsType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/11/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SettingsButton extends Button {

    private final BasicConfigurationFile config;
    private final ProfileSettingsType type;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        List<String> lines = new ArrayList<>();
        String key = "BUTTONS." + type.name() + ".";

        switch (type) {
            case TOGGLESCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isScoreboardEnabled() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isScoreboardEnabled() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEDUELREQUESTS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isReceiveDuelRequests() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isReceiveDuelRequests() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEPINGFACTOR:
                if (player.hasPermission("array.profile.pingfactor")) {
                    for ( String text : config.getStringList(key + "LORE_PERMISSION" )) {
                        if (text.contains("<options>")) {
                            lines.add((profile.getSettings().isPingFactor() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isPingFactor() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                } else {
                    config.getStringList(key + "LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", plugin.getConfigHandler().getSTORE()))));
                }
                break;
            case TOGGLESPECTATORS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isAllowSpectators() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isAllowSpectators() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEPINGONSCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isPingScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isPingScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLECPSONSCOREBOARD:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isCpsScoreboard() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isCpsScoreboard() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLETOURNAMENTMESSAGES:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isTournamentMessages() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isTournamentMessages() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLETABSTYLE:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isVanillaTab() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isVanillaTab() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLESHOWPLAYERS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    if (text.contains("<options>")) {
                        lines.add((profile.getSettings().isShowPlayers() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                        lines.add((!profile.getSettings().isShowPlayers() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                        continue;
                    }
                    lines.add(CC.translate(text));
                }
                break;
            case KILLEFFECTS:
                for ( String text : config.getStringList(key + "LORE" )) {
                    lines.add(CC.translate(text));
                }
                break;
            case TOGGLEDROPPROTECT:
                if (player.hasPermission("array.profile.dropprotect")) {
                    for ( String text : config.getStringList(key + "LORE_PERMISSION" )) {
                        if (text.contains("<options>")) {
                            lines.add((profile.getSettings().isDropProtect() ? config.getString(key + "ENABLED.SELECTED") : config.getString(key + "ENABLED.NOT_SELECTED")));
                            lines.add((!profile.getSettings().isDropProtect() ?  config.getString(key + "DISABLED.SELECTED") : config.getString(key + "DISABLED.NOT_SELECTED")));
                            continue;
                        }
                        lines.add(CC.translate(text));
                    }
                } else {
                    config.getStringList(key + "LORE_NO_PERM" ).forEach(text -> lines.add(CC.translate(text.replace("<store>", plugin.getConfigHandler().getSTORE()))));
                }
                break;
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(config.getString(key + "MATERIAL")));
        itemBuilder.name(config.getString(key + "NAME"));
        itemBuilder.lore(lines);
        if (config.getInteger(key + "DATA") != 0) itemBuilder.durability(config.getInteger(key + "DATA"));

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
        Profile profile = profileManager.getProfile(player.getUniqueId());

        switch (type) {
            case TOGGLESCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setScoreboardEnabled(!profile.getSettings().isScoreboardEnabled());
                break;
            case TOGGLEDUELREQUESTS:
                Button.playSuccess(player);
                profile.getSettings().setReceiveDuelRequests(!profile.getSettings().isReceiveDuelRequests());
                break;
            case TOGGLESPECTATORS:
                Button.playSuccess(player);
                profile.getSettings().setAllowSpectators(!profile.getSettings().isAllowSpectators());
                break;
            case TOGGLECPSONSCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setCpsScoreboard(!profile.getSettings().isCpsScoreboard());
                break;
            case TOGGLEPINGONSCOREBOARD:
                Button.playSuccess(player);
                profile.getSettings().setPingScoreboard(!profile.getSettings().isPingScoreboard());
                break;
            case TOGGLETOURNAMENTMESSAGES:
                Button.playSuccess(player);
                profile.getSettings().setTournamentMessages(!profile.getSettings().isTournamentMessages());
                break;
            case TOGGLETABSTYLE:
                Button.playSuccess(player);
                profile.getSettings().setVanillaTab(!profile.getSettings().isVanillaTab());
                break;
            case TOGGLESHOWPLAYERS:
                Button.playSuccess(player);
                profile.getSettings().setShowPlayers(!profile.getSettings().isShowPlayers());
                plugin.getProfileManager().handleVisibility(profile);
                break;
            case TOGGLEPINGFACTOR:
                if (player.hasPermission("array.profile.pingfactor")) {
                    Button.playSuccess(player);
                    profile.getSettings().setPingFactor(!profile.getSettings().isPingFactor());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    Locale.SETTING_NOPERM.toList().forEach(line -> player.sendMessage(line.replace("<store>", plugin.getConfigHandler().getSTORE())));
                }
                break;
            case TOGGLEDROPPROTECT:
                if (player.hasPermission("array.profile.dropprotect")) {
                    Button.playSuccess(player);
                    profile.getSettings().setDropProtect(!profile.getSettings().isDropProtect());
                } else {
                    Button.playFail(player);
                    player.closeInventory();
                    Locale.SETTING_NOPERM.toList().forEach(line -> player.sendMessage(line.replace("<store>", plugin.getConfigHandler().getSTORE())));
                }
                break;
            case KILLEFFECTS:
                KillEffectMenu menu = new KillEffectMenu();
                plugin.getMenuHandler().openMenu(menu, player);
                break;
        }
        plugin.getProfileManager().save(profile);
    }

    /**
     * Should the click update the menu
     *
     * @param player The player clicking
     * @param clickType {@link ClickType}
     * @return {@link Boolean}
     */
    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}