package ru.itmo.gutsol.tt.types;

import ru.itmo.gutsol.tt.common.NameGenerator;

import java.util.*;

public class Type {
    Type bType;
    private final TypeController typeController;

    Type(TypeController typeController) {
        this.typeController = typeController;
        bType = this;
    }

    public Type getBackingType() {
        if (this != bType) {
            bType = bType.getBackingType();
        }
        return bType;
    }

    public TypeDescriptor getDescriptor() {
        return typeController.td(this);
    }


    @Override
    public String toString() {
        TypeDescriptor desc = getDescriptor();
        return desc != null ? desc.toString() : String.format("type[%s]",
                Integer.toHexString(getBackingType().hashCode()));
    }

    public boolean equals(Type other) {
        return typeController.equalize(this, other, false);
    }

    public boolean unifyWith(Type other) {
        return typeController.unify(this, other);
    }

    public Type concrete(NameGenerator nameGen) {
        TypeDescriptor desc = getDescriptor();
        if (desc == null) {
            typeController.substitute(this, new Constant(nameGen.next()));
        } else {
            desc.getParams().forEach(t -> t.concrete(nameGen));
        }
        return this;
    }

    public boolean contains(Type other) {
        if (this.equals(other)) {
            return true;
        }
        TypeDescriptor desc = getDescriptor();
        if (desc == null) {
            return false;
        }
        return desc.contains(other);
    }

    public void countVariables(Set<Type> variables) {
        TypeDescriptor desc = getDescriptor();
        if(desc == null) {
            variables.add(this.getBackingType());
        } else {
            desc.params.forEach(t -> t.countVariables(variables));
        }
    }

    public Set<Type> getVariables() {
        Set<Type> vars = new HashSet<>();
        countVariables(vars);
        return vars;
    }

    public Type recreateLiterals(Set<Type> literals, Map<Type, Type> createdLiterals) {
        TypeDescriptor desc = getDescriptor();
        if (desc == null) {
            if(literals.contains(getBackingType())) {
                literals.remove(getBackingType());
                Type lit = typeController.createType(null);
                createdLiterals.put(getBackingType(), lit);
                return lit;
            }
            return createdLiterals.getOrDefault(getBackingType(), this);
        } else {
            boolean changed = false;
            List<Type> newParams =new ArrayList<>();
            for (Type type: desc.params) {
                Type newParam = type.recreateLiterals(literals, createdLiterals);
                newParams.add(newParam);
                if (type != newParam) {
                    changed = true;
                }
            }
            if (changed) {
                return typeController.createType(desc.clone(newParams));
            } else {
                return this;
            }
        }
    }
}
