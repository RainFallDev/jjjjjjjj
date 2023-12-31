package xyz.refinedev.practice.hook.core.impl;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.refinedev.practice.hook.core.CoreAdapter;
import xyz.refinedev.practice.util.chat.CC;

public class DefaultProvider implements CoreAdapter {

    @Override
    public String getRankName(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getRankPrefix(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getRankSuffix(OfflinePlayer player) {
        return CC.translate("&a");
    }

    @Override
    public String getFullName(OfflinePlayer player) {
        return player.getPlayer().getDisplayName();
    }

    @Override
    public ChatColor getRankColor(OfflinePlayer player) {
        return ChatColor.GREEN;
    }
}
