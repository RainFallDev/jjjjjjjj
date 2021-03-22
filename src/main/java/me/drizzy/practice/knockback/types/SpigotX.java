package me.drizzy.practice.knockback.types;

import com.minexd.spigot.knockback.KnockbackProfile;
import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.knockback.KnockbackType;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

public class SpigotX implements KnockbackType {

    public SpigotX() {
        Array.logger("&bFound SpigotX! Hooking in...");
    }

    @Override
    public void applyKnockback(Player p, String s) {
        KnockbackProfile knockbackProfile;
        knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getKbProfileByName(s);
        if (knockbackProfile == null) {
            knockbackProfile=com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
        }
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockbackProfile", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void appleKitKnockback(Player p, Kit kit) {
        KnockbackProfile knockbackProfile;
        knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getKbProfileByName(kit.getKnockbackProfile());
        if (knockbackProfile == null) {
            knockbackProfile=com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
        }
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockbackProfile", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyDefaultKnockback(Player p) {
        KnockbackProfile knockbackProfile = com.minexd.spigot.SpigotX.INSTANCE.getConfig().getCurrentKb();
        try {
            Class<?> player = ((CraftPlayer)p).getClass();
            EntityPlayer craftPlayer = ((CraftPlayer)p).getHandle();
            Method getHandle = player.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Method setKnockback = nms.getClass().getMethod("setKnockbackProfile", KnockbackProfile.class);
            setKnockback.invoke(craftPlayer, knockbackProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

