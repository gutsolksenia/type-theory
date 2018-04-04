package ru.itmo.gutsol.tt.common;

import ru.itmo.gutsol.tt.lambda.Variable;

public interface Scope {
    Variable findVariable(String name);
    boolean contains(Variable variable);
}
