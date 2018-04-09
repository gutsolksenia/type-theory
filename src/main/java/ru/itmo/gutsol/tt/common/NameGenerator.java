package ru.itmo.gutsol.tt.common;

public class NameGenerator {

    private final String prefix;

    public NameGenerator(String prefix) {
        this.prefix = prefix;
    }
    private int index = 0;

    public String next() {
        return prefix + index++;
    }
}

