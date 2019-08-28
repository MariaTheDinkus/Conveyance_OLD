package com.zundrel.conveyance.common.blocks;

import com.zundrel.conveyance.api.Casing;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;

public class ConveyorProperties {
    public static final EnumProperty<Casing> CASING = EnumProperty.of("casing", Casing.class, Casing.values());
    public static final BooleanProperty FRONT = BooleanProperty.of("front");
    public static final BooleanProperty LEFT = BooleanProperty.of("left");
    public static final BooleanProperty RIGHT = BooleanProperty.of("right");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty CONVEYOR = BooleanProperty.of("conveyor");
}
