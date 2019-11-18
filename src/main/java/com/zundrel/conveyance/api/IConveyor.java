package com.zundrel.conveyance.api;

public interface IConveyor {
    /**
     * @return How many ticks it takes for an item to traverse one block.
     */
    int getSpeed();

    /**
     * @return The type of conveyor that this is.
     */
    ConveyorType getType();
}
