package ru.itmo.gutsol.tt.common;

import ru.itmo.gutsol.tt.lambda.Variable;

import java.util.HashMap;
import java.util.Map;

public class GlobalScope implements Scope {
    private final static GlobalScope SCOPE = new GlobalScope();

    private final Map<String, Variable> variables = new HashMap<>();

    @Override
    public Variable findVariable(String name) {
        if (variables.get(name) == null) {
            variables.put(name, new Variable(name));
        }
        return variables.get(name);
    }

    @Override
    public boolean contains(Variable variable) {
        return variables.values().contains(variable);
    }

    public static Scope getGlobalScope() {
        return SCOPE;
    }
}
