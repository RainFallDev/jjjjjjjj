package me.drizzy.practice.profile;

import io.netty.channel.Channel;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.events.MatchEvent;
import me.drizzy.practice.match.events.MatchStartEvent;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.TaskUtil;
import me.drizzy.practice.array.essentials.Essentials;
import net.minecraft.server.v1_8_R3.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.array.essentials.event.SpawnTeleportEvent;
import me.drizzy.practice.util.nametag.NameTags;

import java.lang.reflect.Field;
import java.util.List;

public class ProfileListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMatchStart(MatchEvent e) {
        if (e instanceof MatchStartEvent) {
            Match match = e.getMatch();
            Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> match.getPlayers().forEach(player -> {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                List<Player> followers = profile.getFollower();
                for (Player follower : followers) {
                    if (follower != null) {
                        follower.chat("/spec " + profile.getName());
                    }
                }
            }), 20L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getClickedBlock() == null) {
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem().getType() == Material.PAINTING) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().isOp())
                        e.setCancelled(true);
                }
            }

            if (e.getClickedBlock().getState() instanceof ItemFrame) {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (!e.getPlayer().isOp()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
        Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isBusy(event.getPlayer()) && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            TaskUtil.runAsync(() -> {
                PlayerUtil.reset(event.getPlayer(), false);
                Player player = event.getPlayer();
                player.getActivePotionEffects().clear();
                player.setHealth(20.0D);
                player.setFoodLevel(20);
                profile.refreshHotbar();
            });
            profile.handleVisibility();
        }
        for ( Player ps : Bukkit.getOnlinePlayers() ) {
            NameTags.color(event.getPlayer(), ps, ChatColor.GREEN, false);
            if (!Profile.getByUuid(ps).isBusy(ps) && !Profile.getByUuid(ps).isInSomeSortOfFight()) {
                if (Profile.getByUuid(ps).getState() == ProfileState.IN_LOBBY || Profile.getByUuid(ps).getState() == ProfileState.IN_QUEUE)
                    if (profile.getParty() != null) {
                        NameTags.color(ps, event.getPlayer(), ChatColor.BLUE, false);
                    } else {
                        NameTags.color(ps, event.getPlayer(), ChatColor.GREEN, false);
                    }
            }
            PlayerUtil.allowMovement(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!(profile.isInSomeSortOfFight())) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isInSomeSortOfFight()) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInSomeSortOfFight()) {
            if (!profile.isInFight()) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (!event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.isInLobby()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    Essentials.teleportToSpawn((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
            }
            if(profile.isInSumo()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player=event.getPlayer();
        Profile.getPlayerList().add(player);
        Profile profile=new Profile(player.getUniqueId());
        //TODO: Clean this up
        for ( Profile other : Profile.getProfiles().values() ) {
            other.handleVisibility();
        }

        TaskUtil.runAsync(() -> {
            if (!ArrayCache.getPlayerCache().containsKey(player.getName())) {
                ArrayCache.getPlayerCache().put(player.getName(), player.getUniqueId());
            }
            try {
                profile.load();
            } catch (Exception e) {
                e.printStackTrace();
                event.getPlayer().kickPlayer(CC.AQUA + "Failed to load your profile, Please contact an Administrator!");
                return;
            }

            Profile.getProfiles().put(player.getUniqueId(), profile);
            profile.setName(player.getName());
            Essentials.teleportToSpawn(player);
            profile.refreshHotbar();
        });
        //Visibility Bug Fix :)
        new BukkitRunnable() {
            @Override
            public void run() {
                profile.handleVisibility();
            }
        }.runTaskLater(Array.getInstance(), 5L);

        //TODO: Remove this with a NameTagHandler Thread
        for ( Player ps : Bukkit.getOnlinePlayers() ) {
            NameTags.color(player, ps, ChatColor.GREEN, false);
            if (!Profile.getByUuid(ps).isBusy(ps) && !Profile.getByUuid(ps).isInSomeSortOfFight()) {
                if (Profile.getByUuid(ps).getState() == ProfileState.IN_LOBBY || Profile.getByUuid(ps).getState() == ProfileState.IN_QUEUE)
                    NameTags.color(ps, player, ChatColor.GREEN, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Profile profile=Profile.getProfiles().get(event.getPlayer().getUniqueId());
        TaskUtil.runAsync(() -> {
                Profile.getPlayerList().remove(event.getPlayer());
                if (profile.getMatch() != null) {
                    if (profile.getMatch().isSoloMatch() || profile.getMatch().isSumoMatch() || profile.getMatch().isTheBridgeMatch()) {
                        profile.getMatch().handleDeath(event.getPlayer(), profile.getMatch().getOpponentPlayer(event.getPlayer()), true);
                    } else {
                        profile.getMatch().handleDeath(event.getPlayer(), profile.getMatch().getOpponentTeam(event.getPlayer()).getLeader().getPlayer(), true);
                    }
                }
                if (profile.isInQueue()) {
                    profile.getQueue().removePlayer(profile.getQueueProfile());
                }
                profile.save();
                if (profile.getRematchData() != null) {
                    Player target = Array.getInstance().getServer().getPlayer(profile.getRematchData().getTarget());
                    if (target != null && target.isOnline()) {
                        Profile.getByUuid(target.getUniqueId()).checkForHotbarUpdate();
                    }
                }
                if (profile.getParty() !=null && Tournament.CURRENT_TOURNAMENT !=null && Tournament.CURRENT_TOURNAMENT.isParticipating(event.getPlayer())) {
                    Tournament.CURRENT_TOURNAMENT.leave(profile.getParty());
                }
        });
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPressurePlate(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        List<String> completions = (List<String>) event.getTabCompletions();
        completions.clear();
        String token = event.getLastToken();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (StringUtil.startsWithIgnoreCase(p.getName(), token)) {
                completions.add(p.getName());
            }
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPearlThrow(final ProjectileLaunchEvent event) {
        final ProjectileSource source=event.getEntity().getShooter();
        if (source instanceof Player) {
            final Player shooter=(Player) source;
            final Profile profile=Profile.getByUuid(shooter.getUniqueId());
            if (profile.isInLobby() || profile.isInQueue()) {
                event.setCancelled(true);
                profile.getKitEditor().setActive(false);
                PlayerUtil.reset(shooter);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.refreshHotbar();
                    }
                }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
            }
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPotionThrow(final PotionSplashEvent event) {
        ProjectileSource source = event.getPotion().getShooter();
            if (source instanceof Player) {
                final Player shooter=(Player) source;
                final Profile profile=Profile.getByUuid(shooter.getUniqueId());
                if (profile.isInLobby() || profile.isInQueue()) {
                    event.setCancelled(true);
                    profile.getKitEditor().setActive(false);
                    PlayerUtil.reset(shooter);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profile.refreshHotbar();
                        }
                    }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
                }
            }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        final Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());
        if (profile.isInLobby()) {
            if (profile.getKitEditor().isActive()) {
                event.setCancelled(true);
                profile.getKitEditor().setActive(false);
                PlayerUtil.reset(event.getPlayer());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.refreshHotbar();
                    }
                }.runTaskLaterAsynchronously(Array.getInstance(), 2L);
            }
        }
    }

}
