package ru.itmo.gutsol.tt.lambda;


import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

import java.util.HashSet;
import java.util.Set;

public abstract class Lambda implements LambdaContainer {
    protected Set<Variable> variables = new HashSet<>();

    @Override
    public Lambda getLambda() {
        return this;
    }

    @Override
    public Lambda reduce() {
        return null;
    }

    @Override
    public LambdaContainer substitute(Variable varSub, LambdaContainer sub) {
        return this;
    }

    public Lambda reduceFully() {
        Lambda lambda = this;
        Lambda reduced = lambda.reduce();
        while (reduced != null) {
//            System.out.println(reduced);
            lambda = reduced;
            reduced = lambda.reduce();
        }
        return lambda;
    }

    public abstract Type deduceType(TypeController typeController);

    public Set<Variable> getVariables() {
        return variables;
    }
}
