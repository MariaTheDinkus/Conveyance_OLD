package com.zundrel.conveyance.common.utilities;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RotationUtilities {
	public static Box getRotatedBoundingBox(Box def, Direction facing) {
		def.offset(-0.5, -0.5, -0.5);
		switch (facing) {
			case SOUTH:
				def = new Box(def.z1, def.y1, (def.x2 * -1) + 1, def.z2, def.y2, (def.x1 * -1) + 1);
			case WEST:
				def = new Box((def.x2 * -1) + 1, def.y1, (def.z2 * -1) + 1, (def.x1 * -1) + 1, def.y2, (def.z1 * -1) + 1);
			case EAST:
				def = new Box((def.z2 * -1) + 1, def.y1, def.x1, (def.z1 * -1) + 1, def.y2, def.x2);
			default:

		}
		def.offset(0.5, 0.5, 0.5);
		return def;
	}

    public static VoxelShape getRotatedShape(Box def, Direction facing) {
        return VoxelShapes.cuboid(getRotatedBoundingBox(def, facing));
    }
}