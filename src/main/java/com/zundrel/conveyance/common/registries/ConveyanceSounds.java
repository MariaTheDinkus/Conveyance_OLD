package com.zundrel.conveyance.common.registries;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ConveyanceSounds {
	public static final SoundEvent MACHINE_CLICK = register("block.machine.click");

	private ConveyanceSounds() {
		// NO-OP
	}

	public static void init() {
		// NO-OP
	}

	private static SoundEvent register(String name) {
		return Registry.register(Registry.SOUND_EVENT, new Identifier("conveyance", name), new SoundEvent(new Identifier("conveyance", name)));
	}
}
