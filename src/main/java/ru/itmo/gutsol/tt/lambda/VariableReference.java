package ru.itmo.gutsol.tt.lambda;

import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

public class VariableReference extends Lambda {
    private final Variable variable;

    public VariableReference(Variable variable) {
        this.variable = variable;
        variables.add(variable);
    }

    @Override
    public LambdaContainer substitute(Variable varSub, LambdaContainer sub) {
        return varSub.equals(variable) ? sub : this;
    }

    @Override
    public Type deduceType(TypeController typeController) {
        return typeController.getType(this.variable).mono();
    }

    @Override
    public LambdaContainer substituteShared(Variable varSub, LambdaContainer sub, VariableStack prevVarStack, VariableStack newVarStack) {
        if (varSub.equals(variable)) {
            return sub;
        }
        VariableStack oldStack = prevVarStack;
        VariableStack newStack = newVarStack;
        while (oldStack != null) {
            if (newStack == null) {
                throw new IllegalStateException("Different size of stack!");
            }
            if (oldStack.getVariable().equals(variable)) {
                return makeRef(newStack.getVariable());
            }
            oldStack = oldStack.getPrev();
            newStack = newStack.getPrev();
        }
        return this;
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    public static VariableReference makeRef(Variable variable) {
        return new VariableReference(variable);
    }
}
