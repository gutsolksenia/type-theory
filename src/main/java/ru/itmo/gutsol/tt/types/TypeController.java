package ru.itmo.gutsol.tt.types;

import ru.itmo.gutsol.tt.lambda.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static ru.itmo.gutsol.tt.types.TypeDescriptor.kindEquals;

public class TypeController {
    private Map<Type, TypeDescriptor> descriptors = new HashMap<>();
    private Map<Variable, PolyType> varTypes = new HashMap<>();


    public TypeDescriptor td(Type type) {
        return descriptors.get(type.getBackingType());
    }

    public Type createType(TypeDescriptor descriptor) {
        Type type = new Type(this);
        if (descriptor != null) {
            descriptors.put(type, descriptor);
        }
        return type;
    }

    public boolean substitute(Type type, TypeDescriptor typeDescriptor) {
        if (typeDescriptor.contains(type)) {
            return false;
        }
        descriptors.put(type.getBackingType(), typeDescriptor);
        return true;
    }

    public boolean substitute(Type type, Type substitution) {
        TypeDescriptor substDesc = substitution.getDescriptor();
        if (substDesc != null && substDesc.contains(type)) {
            return false;
        }
        descriptors.remove(type.getBackingType());
        type.getBackingType().bType = substitution.getBackingType();
        return true;
    }

    public boolean equalize(Type fst, Type snd, boolean unify) {
        if (fst.getBackingType() == snd.getBackingType()) {
            return true;
        }
        TypeDescriptor fstDesc = td(fst);
        TypeDescriptor sndDesc = td(snd);
        if (fstDesc != null && sndDesc != null) {
            if (!kindEquals(fstDesc, sndDesc)) {
                return false;
            }
            for (int i = 0; i < fstDesc.params.size(); i++) {
                if (!equalize(fstDesc.params.get(i), sndDesc.params.get(i), unify)) {
                    return false;
                }
            }
            if (unify) {
                descriptors.remove(fst.getBackingType());
                fst.getBackingType().bType = snd.getBackingType();
            }
            return true;
        }
        if (unify) {
            if (fstDesc == null) {
                return substitute(fst, snd);
            }
            if (sndDesc == null) {
                return substitute(snd, fst);
            }
        }
        return false;
    }

    public boolean unify(Type first, Type second) {
        return equalize(first, second, true);
    }

    public Type makeApplication(Type arg, Type res) {
        return createType(new Function(arg, res));
    }


    public PolyType getType(Variable variable) {
        PolyType pt = varTypes.get(variable);
        if (pt == null) {
            varTypes.put(variable, new PolyType(createType(null), new HashSet<>()));
        }
        return varTypes.get(variable);
    }

    public void assignType(Variable variable, PolyType type) {
        varTypes.put(variable, type);
    }
}
