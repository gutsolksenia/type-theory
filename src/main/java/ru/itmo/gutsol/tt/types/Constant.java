package ru.itmo.gutsol.tt.types;

import java.util.ArrayList;

public class Constant extends TypeDescriptor {

    public String getName() {
        return name;
    }

    private final String name;

    public Constant(String name) {
        super(new ArrayList<>());
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
