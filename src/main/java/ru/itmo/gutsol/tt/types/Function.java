package ru.itmo.gutsol.tt.types;

import java.util.Arrays;

public class Function extends TypeDescriptor {
    private final Type arg;
    private final Type res;

    public Function(Type arg, Type res) {
        super(Arrays.asList(arg, res));
        this.arg = arg;
        this.res = res;
    }

    @Override
    public String toString() {
        return TypeDescriptor.toStringInLeftChild(arg) + " -> " + res.toString();
    }
}
