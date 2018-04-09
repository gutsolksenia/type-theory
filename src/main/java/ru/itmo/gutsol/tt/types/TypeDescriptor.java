package ru.itmo.gutsol.tt.types;

import java.util.List;

public class TypeDescriptor {
    public List<Type> getParams() {
        return params;
    }

    protected List<Type> params;

    public TypeDescriptor(List<Type> params) {
        this.params = params;
    }

    public static String toStringInLeftChild(Type arg) {
        if (arg.getDescriptor() instanceof Function) {
            return "(" + arg.toString() + ")";
        } else {
            return arg.toString();
        }
    }

    public TypeDescriptor clone(List<Type> params) {
        if (this instanceof Function) {
            return new Function(params.get(0), params.get(1));
        } else {
            return new Constant(((Constant) this).getName());
        }
    }

    public static boolean kindEquals(TypeDescriptor fst, TypeDescriptor snd) {
        if (fst instanceof Function && snd instanceof Function) {
            return true;
        }
        if (fst instanceof Constant && snd instanceof Constant) {
            return ((Constant) fst).getName().equals(((Constant) snd).getName());
        }
        return false;
    }

    public boolean contains(Type otherType) {
        for (Type param : params) {
            if (param.contains(otherType)) {
                return true;
            }
        }
        return false;
    }
}
