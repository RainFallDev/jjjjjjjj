package xyz.refinedev.practice.pvpclasses.bard;

import org.bukkit.potion.PotionEffect;

public class EffectData {

    public PotionEffect clickable;
    public PotionEffect heldable;

    public int energyCost;

    public EffectData(int energyCost, PotionEffect clickable, PotionEffect heldable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
        this.heldable = heldable;
    }
}
