package me.array.ArrayPractice.event.impl.spleef;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SpleefTask extends BukkitRunnable {

	private int ticks;
	private Spleef spleef;
	private SpleefState eventState;

	public SpleefTask(Spleef spleef, SpleefState eventState) {
		this.spleef = spleef;
		this.eventState = eventState;
	}

	@Override
	public void run() {
		if (Array.get().getSpleefManager().getActiveSpleef() == null ||
		    !Array.get().getSpleefManager().getActiveSpleef().equals(spleef) || spleef.getState() != eventState) {
			cancel();
			return;
		}

		onRun();

		ticks++;
	}

	public int getSeconds() {
		return 3 - ticks;
	}

	public abstract void onRun();

}
