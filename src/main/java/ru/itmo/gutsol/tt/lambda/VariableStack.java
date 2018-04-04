package ru.itmo.gutsol.tt.lambda;

public class VariableStack {
    private final Variable variable;
    private VariableStack previous = null;

    public VariableStack(Variable variable, VariableStack prev) {
        this.variable = variable;
        this.previous = prev;
    }

    public VariableStack(Variable variable) {
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    public VariableStack getPrevious() {
        return previous;
    }
}
