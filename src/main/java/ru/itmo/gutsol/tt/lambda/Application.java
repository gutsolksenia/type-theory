package ru.itmo.gutsol.tt.lambda;

import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

public class Application extends Lambda {
    private final LambdaContainer funcContainer;
    private final LambdaContainer argContainer;

    public Application(LambdaContainer funcContainer, LambdaContainer argContainer) {
        this.funcContainer = funcContainer;
        this.argContainer = argContainer;
        variables.addAll(getFunc().variables);
        variables.addAll(getArg().variables);
    }

    public Lambda getFunc() {
        return funcContainer.getLambda();
    }

    public Lambda getArg() {
        return argContainer.getLambda();
    }

    @Override
    public Lambda reduce() {
        if (funcContainer instanceof Abstraction) {
            return ((Abstraction) funcContainer).getBodyContainer()
                    .substitute(((Abstraction) funcContainer).getParam(),
                            LambdaMemoization.memo(getArg())).getLambda();
        }
        if (funcContainer instanceof LambdaMemoization) {
            Lambda funcInt = funcContainer.getLambda();
            if (funcInt instanceof Abstraction) {
                return ((Abstraction) funcInt).getBodyContainer().substituteShared(((Abstraction) funcInt).getParam(),
                        LambdaMemoization.memo(getArg()), null, null).getLambda();
            }
        }
        LambdaContainer funcReduced = funcContainer.reduce();
        if (funcReduced != null) {
            if (funcReduced == funcContainer) {
                return this;
            }
            return new Application(funcReduced, argContainer);
        }

        LambdaContainer argReduced = argContainer.reduce();
        if (argReduced != null) {
            if (argReduced == argContainer) {
                return this;
            }
            return new Application(funcContainer, argReduced);
        }
        return null;
    }

    @Override
    public Lambda substitute(Variable varSub, LambdaContainer sub) {
        LambdaContainer funcSubst = funcContainer.substitute(varSub, sub);
        LambdaContainer argSubst = argContainer.substitute(varSub, sub);
        if (funcSubst != null && argSubst != null) {
            return new Application(funcSubst, argSubst);
        }
        return null;
    }

    @Override
    public Type deduceType(TypeController typeController) {
        Type funcType = getFunc().deduceType(typeController);
        Type argType = getArg().deduceType(typeController);
        if (funcType == null || argType == null) {
            return null;
        }
        Type resType = typeController.createType(null);
        boolean unifyRes = funcType.unifyWith(typeController.makeApplication(argType, resType));
        return unifyRes ? resType : null;
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        LambdaContainer funcSubst = funcContainer.substituteShared(varSub, sub, prevVarStack, newVarStack);
        LambdaContainer argSubst = argContainer.substituteShared(varSub, sub, prevVarStack, newVarStack);
        if (funcSubst != null && argSubst != null) {
            return new Application(funcSubst, argSubst);
        }
        return null;
    }

    @Override
    public String toString() {
        String strFunc = getFunc() instanceof Abstraction ?
                String.format("(%s)", getFunc().toString()) :
                getFunc().toString();
        String strArg = getArg() instanceof VariableReference ?
                getArg().toString() :
                String.format("(%s)", getArg().toString());
        return strFunc + ' ' + strArg;
    }
}
