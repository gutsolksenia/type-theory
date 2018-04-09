package ru.itmo.gutsol.tt.lambda;

import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

public class Abstraction extends Lambda {
    private Variable param;

    private LambdaContainer bodyContainer;

    public Abstraction(Variable param, LambdaContainer bodyContainer) {
        this.param = param;
        this.bodyContainer = bodyContainer;
        variables.addAll(getBody().variables);
        variables.remove(param);
    }

    public Variable getParam() {
        return param;
    }

    public LambdaContainer getBodyContainer() {
        return bodyContainer;
    }

    public Lambda getBody() {
        return bodyContainer.getLambda();
    }

    @Override
    public Lambda reduce() {
        LambdaContainer reduced = bodyContainer.reduce();
        if (reduced == null) {
            return null;
        }
        if (reduced == bodyContainer) {
            return this;
        }
        return new Abstraction(param, reduced);
    }

    @Override
    public Lambda substitute(Variable varSub, LambdaContainer sub) {
        if (checkVariables(varSub, sub)) {
            return null;
        }
        LambdaContainer bodySubst = bodyContainer.substitute(varSub, sub);
        if (bodySubst == null) {
            return null;
        }
        return new Abstraction(param, bodySubst);
    }

    @Override
    public Type deduceType(TypeController typeController) {
        Type paramType = typeController.getType(param).getType();
        Type bodyType = getBody().deduceType(typeController);
        if (bodyType == null) {
            return null;
        }
        return typeController.makeApplication(paramType, bodyType);
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        if (checkVariables(varSub, sub)) {
            return null;
        }
        Variable newParam = new Variable(param.getName());
        LambdaContainer bodySubst = bodyContainer.substituteShared(varSub, sub,
                new VariableStack(param, prevVarStack),
                new VariableStack(newParam, newVarStack));
        if (bodySubst == null) {
            return null;
        }
        return new Abstraction(newParam, bodySubst);
    }

    @Override
    public String toString() {
        return '\\' + param.getName() + '.' + getBody();
    }

    private boolean checkVariables(Variable varSub, LambdaContainer subst) {
        return getBody().variables.contains(varSub)
                && subst.getLambda().variables
                .stream()
                .anyMatch(v -> v.getName().equals(param.getName()));
    }
}
