package ru.itmo.gutsol.tt.lambda;

import ru.itmo.gutsol.tt.types.PolyType;
import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

import java.util.Set;
import java.util.stream.Collectors;

public class Let extends Lambda {
    private final Variable variable;
    private final LambdaContainer defContainer;
    private final LambdaContainer exprContainer;

    public Let(Variable variable, LambdaContainer defContainer, LambdaContainer exprContainer) {
        super();
        this.variable = variable;
        this.defContainer = defContainer;
        this.exprContainer = exprContainer;

        variables.addAll(getDefinition().variables);
        variables.addAll(getExpr().variables);
        variables.remove(variable);
    }

    @Override
    public LambdaContainer substitute(Variable varSub, LambdaContainer sub) {
        if (checkVariables(varSub, sub)) {
            return null;
        }
        LambdaContainer definitionSub = defContainer.substitute(varSub, sub);
        LambdaContainer exprSub = exprContainer.substitute(varSub, sub);
        if (definitionSub == null || exprSub == null) {
            return null;
        }
        return new Let(variable, definitionSub, exprSub);
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        if (checkVariables(varSub, sub)) {
            return null;
        }
        Variable newVariable = new Variable(variable.getName());
        LambdaContainer definitionSub = defContainer.substituteShared(varSub, sub, prevVarStack, newVarStack);
        LambdaContainer exprSub = exprContainer.substituteShared(varSub, sub,
                new VariableStack(variable, prevVarStack),
                new VariableStack(newVariable, newVarStack));
        if (definitionSub == null || exprSub == null) {
            return null;
        }
        return new Let(newVariable, definitionSub, exprSub);
    }

    @Override
    public Lambda reduce() {
        LambdaContainer substituted = exprContainer.substitute(variable, LambdaMemoization.memo(getDefinition()));
        if (substituted != null) {
            return substituted.getLambda();
        }
        Lambda defReduced = getDefinition().reduce();
        if (defReduced == null) {
            return null;
        }
        if (defReduced == defContainer) {
            return this;
        }
        return new Let(variable, defReduced, getExpr());
    }

    private boolean checkVariables(Variable varSub, LambdaContainer sub) {
        return getExpr().variables.contains(varSub) &&
                sub.getLambda().variables.stream()
                        .anyMatch(v -> v.getName().equals(varSub.getName()));
    }

    @Override
    public Type deduceType(TypeController typeController) {
        Type defType = getDefinition().deduceType(typeController);
        Set<Type> polymorphicTypes = defType.getVariables()
                .stream()
                .filter(type -> {
                    boolean filter = true;
                    for (Variable defVar: getDefinition().getVariables()) {
                        if (typeController.getType(defVar).getType().contains(type)) {
                            filter = false;
                            break;
                        }
                    }
                    return filter;
                }).collect(Collectors.toSet());
        typeController.assignType(variable, new PolyType(defType, polymorphicTypes));
        return getExpr().deduceType(typeController);
    }

    public Lambda getDefinition() {
        return defContainer.getLambda();
    }

    public Lambda getExpr() {
        return exprContainer.getLambda();
    }
}
