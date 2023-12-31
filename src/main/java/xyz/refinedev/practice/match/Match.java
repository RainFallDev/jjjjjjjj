package xyz.refinedev.practice.match;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.match.types.kit.BattleRushMatch;
import xyz.refinedev.practice.match.types.kit.BoxingMatch;
import xyz.refinedev.practice.match.types.kit.MLGRushMatch;
import xyz.refinedev.practice.match.types.kit.TeamFightMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBedwarsMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.task.match.MatchCPSTask;
import xyz.refinedev.practice.task.match.MatchStartTask;
import xyz.refinedev.practice.task.match.MatchWaterCheckTask;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter @Setter
public abstract class Match {

    private final List<MatchSnapshot> snapshots = new ArrayList<>();
    private final List<UUID> spectatorList = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private final List<Location> placedBlocks = new ArrayList<>();

    private final UUID matchId;
    private final Queue queue;
    private final Kit kit;
    private final Arena arena;
    private final QueueType queueType;

    public MatchState state = MatchState.STARTING;
    public BukkitTask task, waterTask;

    private long startTimestamp;

    /**
     * Construct a match using the given details
     *
     * @param queue {@link Queue} if match is started from queue, then we provide it
     * @param kit {@link Kit} The kit that will be given to all players in the match
     * @param arena {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public Match(Queue queue, Kit kit, Arena arena, QueueType queueType) {
        this.matchId = UUID.randomUUID();
        this.queue = queue;
        this.kit = kit;
        this.arena = arena;
        this.queueType = queueType;
    }

    /**
     * Start all the usual Match tasks that
     * track and execute the match's logic
     */
    public void initiateTasks(Array plugin) {
        if (this.kit.getGameRules().isWaterKill() || this.kit.getGameRules().isParkour() || this.kit.getGameRules().isSumo()) {
            this.waterTask = new MatchWaterCheckTask(plugin, this).runTaskTimer(plugin, 20L, 20L);
        }

        new MatchCPSTask(plugin, this).runTaskTimer(plugin, 2L, 2L);
        this.task = new MatchStartTask(this).runTaskTimer(plugin, 20L, 20L);
    }


    /**
     * Get match's duration in string form
     *
     * @return {@link String}
     */
    public String getDuration() {
        switch (state) {
            case STARTING:
                return "Starting";
            case ENDING:
                return "Ending";
            default:
                return TimeUtil.millisToTimer(getElapsedDuration());
        }
    }

    /**
     * Get Elapsed Duration of the {@link Match}
     *
     * @return {@link Long} the time passed in long
     */
    public long getElapsedDuration() {
        if (this.isBattleRushMatch()) {
            return (TimeUtil.parseTime("15mins") + startTimestamp) - System.currentTimeMillis();
        }
        return System.currentTimeMillis() - startTimestamp;
    }

    /**
     * Broadcast a {@link String} message to all match participants
     *
     * @param message {@link String}
     */
    public void broadcastMessage(String message) {
        this.getPlayers().forEach(player -> player.sendMessage(message));
        this.getSpectators().forEach(player -> player.sendMessage(message));
    }

    /**
     * Broadcast a {@link Sound} to all match participants
     *
     * @param sound {@link Sound}
     */
    public void broadcastSound(Sound sound) {
        this.getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
        this.getSpectators().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
    }

    /**
     * Get {@link MatchSnapshot} of a specific player
     * from this match instance
     *
     * @param player The player whose match snapshot we are fetching
     * @return {@link MatchSnapshot}
     */
    public MatchSnapshot getSnapshotOfPlayer(Player player) {
        for (MatchSnapshot snapshot : this.getSnapshots()) {
            if (!(player == null ? snapshot.getTeamPlayer().isDisconnected() : snapshot.getTeamPlayer().getUniqueId().equals(player.getUniqueId()))) continue;
            return snapshot;
        }
        return null;
    }

    /**
     * Get all spectators currently in the match
     *
     * @return {@link List}
     */
    public List<Player> getSpectators() {
        return spectatorList.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get both players and spectators in a {@link List}
     *
     * @return {@link List}
     */
    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(getPlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    /**
     * Get the middle spawn of the current arena
     *
     * @return {@link Location} middle spawn
     */
    public Location getMidSpawn() {
        Location spawn = this.getArena().getSpawn1();
        Location spawn2 = this.getArena().getSpawn2();
        Location midSpawn = this.getArena().getSpawn1();

        midSpawn.setX(getAverage(spawn.getX(), spawn2.getX()));
        midSpawn.setZ(getAverage(spawn.getZ(), spawn2.getZ()));

        return midSpawn;
    }

    /**
     * Average between two numbers
     *
     * @param one {@link Double} first number
     * @param two {@link Double} second number
     * @return {@link Double} average of both numbers
     */
    public double getAverage(double one, double two) {
        double three = one + two;
        three = three / 2;
        return three;
    }

    /**
     * Returns true if the match starting
     *
     * @return {@link Boolean}
     */
    public boolean isStarting() {
        return state == MatchState.STARTING;
    }

    /**
     * Returns true if the match is in fight
     *
     * @return {@link Boolean}
     */
    public boolean isFighting() {
        return state == MatchState.FIGHTING;
    }

    /**
     * Returns true if the match is ending
     *
     * @return {@link Boolean}
     */
    public boolean isEnding() {
        return state == MatchState.ENDING;
    }

    /**
     * This method is returns true if the
     * current match related to {@link SoloMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isSoloMatch() {
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link TeamMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isTeamMatch(){
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link FFAMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isFreeForAllMatch(){
        return false;
    }

    /**
     * This method is returns true if the
     * current match related to {@link TeamFightMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isHCFMatch() {
        return this.isTeamMatch() && this instanceof TeamFightMatch;
    }

    /**
     * This method is returns true if the
     * current match related to {@link SoloBridgeMatch}
     * or {@link TeamBridgeMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isTheBridgeMatch() {
        return this.isSoloMatch() && (this instanceof SoloBridgeMatch || this instanceof TeamBridgeMatch);
    }

    /**
     * This method is returns true if the
     * current match related to {@link BattleRushMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isBattleRushMatch() {
        return this.isSoloMatch() && this instanceof BattleRushMatch;
    }

    /**
     * This method is returns true if the
     * current match related to {@link MLGRushMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isMLGRushMatch() {
        return this.isSoloMatch() && this instanceof MLGRushMatch;
    }

    /**
     * This method is returns true if the
     * current match related to {@link BattleRushMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isBedwarsMatch() {
        return this instanceof SoloBedwarsMatch || this instanceof TeamBedwarsMatch;
    }

    /**
     * This method is returns true if the
     * current match related to {@link BoxingMatch}
     *
     * @return {@link Boolean}
     */
    public boolean isBoxingMatch() {
        return this.isSoloMatch() && this instanceof BoxingMatch;
    }

    /**
     * Setup the player according to {@link Kit},
     * {@link KitGameRules} and {@link Arena}
     * <p>
     * This also teleports the player to the specified arena,
     * set's their parkour checkpoint if kit is parkour and
     * gives special potion effects if specified
     *
     * @param player {@link Player} being setup
     */
    public abstract void setupPlayer(Array plugin, Player player);

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    public abstract void onStart(Array plugin);

    /**
     * Execute match handleEnd tasks through this method
     * This method is called to check if the match can handleEnd or not
     * and if it can then the method itself clears up a bit of ending the match
     *
     * @return {@link Boolean} Whether the match successfully ended or not
     */
    public abstract boolean onEnd(Array plugin);

    /**
     * Returns true if the match is ready to handleEnd
     *
     * @return {@link Boolean} Where the match can statistically handleEnd or not
     */
    public abstract boolean canEnd();

    /**
     * Execute tasks upon a player's death
     *
     * @param player {@link Player} the player being killed
     * @param killer {@link Player} the player killing
     */
    public abstract void onDeath(Array plugin, Player player, Player killer);

    /**
     * Execute tasks upon a player's respawn
     *
     * @param player {@link Player} the player being respawned
     */
    public abstract void onRespawn(Array plugin, Player player);

    /**
     * Get the winning {@link Player} of a Match
     *
     * @return {@link Player}
     */
    public abstract Player getWinningPlayer();

    /**
     * Get the winning {@link Team} of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getWinningTeam();

    /**
     * Get teamPlayerA of a Match
     *
     * @return {@link TeamPlayer}
     */
    public abstract TeamPlayer getTeamPlayerA();

    /**
     * Get teamPlayerB of a Match
     *
     * @return {@link TeamPlayer}
     */
    public abstract TeamPlayer getTeamPlayerB();

    /**
     * Get a List of all TeamPlayers of a Match
     *
     * @return {@link List<TeamPlayer>}
     */
    public abstract List<TeamPlayer> getTeamPlayers();

    /**
     * Get a List of all Players of a Match
     *
     * @return {@link List<Player>}
     */
    public abstract List<Player> getPlayers();

    /**
     * Get a List of all Alive Players of a Match
     *
     * @return {@link List<Player>}
     */
    public abstract List<Player> getAlivePlayers();

    /**
     * Get Team A of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getTeamA();

    /**
     * Get Team B of a Match
     *
     * @return {@link Team}
     */
    public abstract Team getTeamB();

    /**
     * Get a Specific {@link Team} related to the
     * specified player
     *
     * @param player {@link Player} the player whose team we are getting
     * @return {@link Team} the queried team
     */
    public abstract Team getTeam(Player player);

    /**
     * Get a specific {@link TeamPlayer} by the player
     * linked to the team player
     *
     * @param player {@link Player} whose teamPlayer we are getting
     * @return {@link TeamPlayer} the queried teamPlayer
     */
    public abstract TeamPlayer getTeamPlayer(Player player);

    /**
     * Get the Opponent {@link Team} of a specified Team
     *
     * @param team {@link Team} the team whose opponent team we are getting
     * @return {@link Team} the queried team
     */
    public abstract Team getOpponentTeam(Team team);

    /**
     * Get the Opponent {@link Team} of a specified Player's Team
     *
     * @param player {@link Player} the player whose opponent team we are getting
     * @return {@link Team} the queried team
     */
    public abstract Team getOpponentTeam(Player player);

    public abstract TeamPlayer getOpponentTeamPlayer(Player player);

    public abstract Player getOpponentPlayer(Player player);

    public abstract List<BaseComponent[]> generateEndComponents(Array plugin, Player player);

    public abstract ChatColor getRelationColor(Player viewer, Player target);
}