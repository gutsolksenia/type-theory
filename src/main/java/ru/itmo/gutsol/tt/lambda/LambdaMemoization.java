package ru.itmo.gutsol.tt.lambda;


public class LambdaMemoization implements LambdaContainer {
    private Lambda lambda;
    private boolean fullyReduced = false;

    public LambdaMemoization(Lambda lambdaInt) {
        this.lambda = lambdaInt;
    }

    @Override
    public Lambda getLambda() {
        return lambda;
    }

    @Override
    public LambdaContainer reduce() {
        if (fullyReduced) {
            return null;
        }
        Lambda reduced = lambda.reduce();
        if (reduced == null) {
            fullyReduced = true;
            return null;
        }
        lambda = reduced;
        return this;
    }

    @Override
    public LambdaContainer substitute(Variable varSub, LambdaContainer sub) {
        return memoization(getLambda().substituteShared(varSub, sub, null, null));
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        return memoization(getLambda().substituteShared(varSub, sub, prevVarStack, newVarStack));
    }

    public static LambdaMemoization memoization(LambdaContainer arg) {
        if (arg instanceof LambdaMemoization) {
            return (LambdaMemoization) arg;
        } else {
            return new LambdaMemoization(arg.getLambda());
        }
    }
}
