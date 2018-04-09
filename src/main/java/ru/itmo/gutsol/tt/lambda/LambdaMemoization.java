package ru.itmo.gutsol.tt.lambda;


public class LambdaMemoization implements LambdaContainer {
    private Lambda lambdaInt;
    private boolean fullyReduced = false;

    public LambdaMemoization(Lambda lambdaInt) {
        this.lambdaInt = lambdaInt;
    }

    @Override
    public Lambda getLambda() {
        return lambdaInt;
    }

    @Override
    public LambdaContainer reduce() {
        if (fullyReduced) {
            return null;
        }
        Lambda reduced = lambdaInt.reduce();
        if (reduced == null) {
            fullyReduced = true;
            return null;
        }
        lambdaInt = reduced;
        return this;
    }

    @Override
    public LambdaContainer substitute(Variable varSub, LambdaContainer sub) {
        return memo(getLambda().substituteShared(varSub, sub, null, null));
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        return memo(getLambda().substituteShared(varSub, sub, prevVarStack, newVarStack));
    }

    public static LambdaMemoization memo(LambdaContainer arg) {
        if (arg instanceof LambdaMemoization) {
            return (LambdaMemoization) arg;
        } else {
            return new LambdaMemoization(arg.getLambda());
        }
    }
}
