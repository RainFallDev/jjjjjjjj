package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"kit setkb"}, permission = "practice.dev")
public class KitSetKnockbackProfileCommand {

    public void execute(Player player, Kit kit, @CPL("KnockbackProfile") String knockbackProfile) {
        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.setKnockbackProfile(knockbackProfile);
        kit.save();

        player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "You updated the kit's knockbackprofile to" + knockbackProfile);
    }

}