package xyz.refinedev.practice.events.meta.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private final String readable;

}