package com.zundrel.conveyance.api;

public interface PositionalConveyable extends Conveyable {
	int getPosition();

	int getPrevPosition();
}
