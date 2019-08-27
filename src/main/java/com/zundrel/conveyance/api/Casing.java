package com.zundrel.conveyance.api;

import net.minecraft.util.StringIdentifiable;

public enum Casing implements StringIdentifiable {
    NONE("none"),
    GLASS("glass"),
    OPAQUE("opaque");

    private final String name;

    Casing(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
