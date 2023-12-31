package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;

@UtilityClass
public class TaskUtil {

    public void run(Runnable runnable) {
        Array.getInstance().getServer().getScheduler().runTask(Array.getInstance(), runnable);
    }

    public void runTimer(Runnable runnable, long delay, long timer) {
        Array.getInstance().getServer().getScheduler().runTaskTimer(Array.getInstance(), runnable, delay, timer);
    }

    public void runTimerAsync(Runnable runnable, long delay, long timer) {
        Array.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Array.getInstance(), runnable, delay, timer);
    }

    public void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(Array.getInstance(), delay, timer);
    }

    public void runLater(Runnable runnable, long delay) {
        Array.getInstance().getServer().getScheduler().runTaskLater(Array.getInstance(), runnable, delay);
    }

    public void runLaterAsync(Runnable runnable, long delay) {
        Array.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Array.getInstance(), runnable, delay);
    }

    public void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Array.getInstance(), runnable);
    }

    public void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Array.getInstance(), runnable);
        else
            runnable.run();
    }
}
