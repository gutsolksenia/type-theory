package ru.itmo.gutsol.tt.lambda;

public interface LambdaContainer {
    Lambda getLambda();

    LambdaContainer reduce();

    LambdaContainer substitute(Variable varSub, LambdaContainer sub);

    LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack,
                                     VariableStack newVarStack);
}