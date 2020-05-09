package com.zundrel.conveyance.api;

public interface ConveyorConveyable extends Conveyable {
	ConveyorType getConveyorType();

	int getPosition();

	int getPrevPosition();
}
