package ru.itmo.gutsol.tt.common;

import ru.itmo.gutsol.tt.lambda.Variable;

public class AbstractionScope implements Scope {
    private final Variable param;
    private final Scope parentScope;

    public AbstractionScope(Variable param, Scope parentScope) {
        this.param = param;
        this.parentScope = parentScope;
    }


    @Override
    public Variable findVariable(String name) {
        return param.getName().equals(name) ? param : parentScope.findVariable(name);
    }

    @Override
    public boolean contains(Variable variable) {
        return variable.equals(param) || parentScope.contains(variable);
    }
}
