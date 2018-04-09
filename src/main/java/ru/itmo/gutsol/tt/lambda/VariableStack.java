package ru.itmo.gutsol.tt.lambda;

public class VariableStack {
    private final Variable variable;
    private VariableStack prev = null;

    public VariableStack(Variable variable, VariableStack prev) {
        this.variable = variable;
        this.prev = prev;
    }

    public VariableStack(Variable variable) {
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    public VariableStack getPrev() {
        return prev;
    }
}
