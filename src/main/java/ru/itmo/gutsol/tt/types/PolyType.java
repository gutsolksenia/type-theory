package ru.itmo.gutsol.tt.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PolyType {
    private final Set<Type> polymorphicTypes;

    private final Type type;

    public PolyType(Type type, Set<Type> polymorphicTypes) {
        this.type = type;
        this.polymorphicTypes = polymorphicTypes;
    }

    public Type getType() {
        return type;
    }

    public Type mono() {
        if (polymorphicTypes.isEmpty()) {
            return type;
        }
        Set<Type> variables = polymorphicTypes.stream()
                .map(Type::getBackingType)
                .collect(Collectors.toSet());
        Map<Type, Type> createdVariables = new HashMap<>();
        return type.recreateLiterals(variables, createdVariables);
    }

    @Override
    public String toString() {
        if (polymorphicTypes.isEmpty()) {
            return type.toString();
        }
        StringBuilder res = new StringBuilder();
        res.append("@");
        for (Type param: polymorphicTypes) {
            res.append(" ");
            res.append(param.toString());
        }
        res.append(". ");
        res.append(type.toString());
        return res.toString();
    }
}
