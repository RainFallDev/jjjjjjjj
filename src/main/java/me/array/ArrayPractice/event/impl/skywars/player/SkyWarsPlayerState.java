package me.array.ArrayPractice.event.impl.skywars.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SkyWarsPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private String readable;

}
