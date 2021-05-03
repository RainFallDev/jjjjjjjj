package me.drizzy.practice.events.types.sumo;

import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.sumo.task.SumoRoundEndTask;
import me.drizzy.practice.events.types.sumo.task.SumoRoundStartTask;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.other.*;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.events.types.sumo.player.SumoPlayer;
import me.drizzy.practice.events.types.sumo.player.SumoPlayerState;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Sumo {

	protected static String EVENT_PREFIX=CC.translate("&8[&cSumo&8] &r");

	private final String name;
	@Setter private SumoState state = SumoState.WAITING;
	private SumoTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, SumoPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter final private List<UUID> spectators = new ArrayList<>();
	@Getter @Setter	public static int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	private SumoPlayer roundPlayerA;
	private SumoPlayer roundPlayerB;
	@Setter	private long roundStart;
	@Getter	@Setter	private static boolean enabled = true;

	public Sumo(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Sumo sumo = Array.getInstance().getSumoManager().getActiveSumo();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&fHost: &c" + sumo.getName()));

		if (sumo.isWaiting()) {
			toReturn.add("&fPlayers: &c" + sumo.getEventPlayers().size() + "/" + Sumo.getMaxPlayers());
			toReturn.add("");

			if (sumo.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&fRemaining: &c" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
			toReturn.add("&fDuration: &c" + sumo.getRoundDuration());
			toReturn.add("");
			toReturn.add("&a" + sumo.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&c" + sumo.getRoundPlayerB().getUsername());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(SumoTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == SumoState.WAITING;
	}

	public boolean isFighting() {
		return state == SumoState.ROUND_FIGHTING;
	}

	public SumoPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				Player player = sumoPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new SumoPlayer(player));

		broadcastMessage(CC.RED + player.getName() + CC.GRAY + " has joined the &cSumo Event&8! &8(&c" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &cSumo Event&8!"));
		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
					NameTags.color(player, otherPlayer, Array.getInstance().getEssentials().getNametagMeta().getEventColor(), false);
				}
			}
		}.runTaskAsynchronously(Array.getInstance());
	}

	public void handleLeave(Player player) {
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SumoState.WAITING) {
			broadcastMessage(CC.RED + player.getName() + CC.GRAY + " left the &cSumo Event&8! &8(&c" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &cSumo Event&8!"));
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
					NameTags.reset(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(Array.getInstance());

		profile.setState(ProfileState.IN_LOBBY);
		profile.setSpleef(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		SumoPlayer loser = getEventPlayer(player);
		loser.setState(SumoPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		Array.getInstance().getSumoManager().setActiveSumo(null);
		Array.getInstance().getSumoManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The sumo events has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Sumo Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Sumo Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Sumo Event" + CC.GRAY + "!");
		}

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSumo(null);
				profile.refreshHotbar();

				profile.teleportToSpawn();
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public boolean canEnd() {
		int remaining = 0;

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() != SumoPlayerState.ELIMINATED) {
				return sumoPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&c&l[Sumo Event]"));
		strings.add(CC.translate("&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&fA &cSumo &fevent is being hosted by &c" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Sumo events", "/sumo join");
			for ( Player player : Bukkit.getOnlinePlayers() ) {
				if (!eventPlayers.containsKey(player.getUniqueId())) {
					message.sendToPlayer(player);
				}
			}
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : getPlayers()) {
			player.sendMessage(EVENT_PREFIX + CC.translate(message));
		}
	}

	public void onJoin(Player player) {
	  Profile.setKb(player, Array.getInstance().getSumoManager().getSumoKnockbackProfile());
	}

	public void onLeave(Player player) {
      Profile.setKb(player, Array.getInstance().getSumoManager().getSumoKnockbackProfile());
	}

	public void onRound() {
		setState(SumoState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInSumo()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInSumo()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerB = null;
		}

		roundPlayerA = findRoundPlayer();
		roundPlayerB = findRoundPlayer();

		Player playerA = roundPlayerA.getPlayer();
		Player playerB = roundPlayerB.getPlayer();

		PlayerUtil.reset(playerA);
		PlayerUtil.reset(playerB);

		PlayerUtil.denyMovement(playerA);
		PlayerUtil.denyMovement(playerB);

		playerA.teleport(Array.getInstance().getSumoManager().getSumoSpawn1());
		playerB.teleport(Array.getInstance().getSumoManager().getSumoSpawn2());

		setEventTask(new SumoRoundStartTask(this));
	}

	public void onDeath(Player player) {
		SumoPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(SumoPlayerState.WAITING);
		winner.incrementRoundWins();
		winner.getPlayer().teleport(Array.getInstance().getSumoManager().getSumoSpectator());


		broadcastMessage("&c" + player.getName() + "&7 was eliminated by &c" + winner.getUsername() + "&7!");

		setState(SumoState.ROUND_ENDING);
		setEventTask(new SumoRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == SumoState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == SumoState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
	}

	private SumoPlayer findRoundPlayer() {
		SumoPlayer sumoPlayer = null;

		for (SumoPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == SumoPlayerState.WAITING) {
				if (sumoPlayer == null) {
					sumoPlayer = check;
					continue;
				}

				if (check.getRoundWins() == 0) {
					sumoPlayer = check;
					continue;
				}

				if (check.getRoundWins() <= sumoPlayer.getRoundWins()) {
					sumoPlayer = check;
				}
			}
		}

		if (sumoPlayer == null) {
			throw new RuntimeException("Could not find a new round player");
		}

		return sumoPlayer;
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(this);
		PlayerUtil.spectator(player);
		player.setFlying(true);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(Array.getInstance().getSumoManager().getSumoSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(null);
		player.setFlying(false);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		profile.teleportToSpawn();
	}

}
