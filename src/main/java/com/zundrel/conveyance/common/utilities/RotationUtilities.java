package com.zundrel.conveyance.common.utilities;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class RotationUtilities {
	public static Box getRotatedBoundingBox(Box def, Direction facing) {
		def.offset(-0.5, -0.5, -0.5);
		switch (facing) {
			case SOUTH:
				def = new Box(def.minZ, def.minY, (def.maxX * -1) + 1, def.maxZ, def.maxY, (def.minX * -1) + 1);
			case WEST:
				def = new Box((def.maxX * -1) + 1, def.minY, (def.maxZ * -1) + 1, (def.minX * -1) + 1, def.maxY, (def.minZ * -1) + 1);
			case EAST:
				def = new Box((def.maxZ * -1) + 1, def.minY, def.minX, (def.minZ * -1) + 1, def.maxY, def.maxX);
			default:

		}
		def.offset(0.5, 0.5, 0.5);
		return def;
	}
}