package ru.itmo.gutsol.tt.common;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import ru.itmo.gutsol.tt.autogen.grammar.LambdaLexer;
import ru.itmo.gutsol.tt.autogen.grammar.LambdaParser;
import ru.itmo.gutsol.tt.lambda.*;

public class StubGenerator {
    public static LambdaStub toLambdaStub(String s) {
        LambdaLexer lexer = new LambdaLexer(new ANTLRInputStream(s));
        return new LambdaParser(new CommonTokenStream(lexer)).let_expression().ret;
    }

    public static LambdaStub let(String alias, LambdaStub def, LambdaStub expr) {
        return scope -> {
            Lambda defLambda = def.resolve(scope);
            Variable var = new Variable(alias);
            Lambda exprLambda = expr.resolve(new AbstractionScope(var, scope));
            return new Let(var, defLambda, exprLambda);
        };
    }

    public static LambdaStub abstraction(String alias, LambdaStub body) {
        return scope -> {
            Variable param = new Variable(alias);
            return new Abstraction(param, body.resolve(new AbstractionScope(param, scope)));
        };
    }

    public static LambdaStub application(LambdaStub func, LambdaStub arg) {
        return scope -> {
            Lambda funcLambda = func.resolve(scope);
            Lambda argLambda = arg.resolve(scope);

            return new Application(funcLambda, argLambda);
        };
    }

    public static LambdaStub variable(String alias) {
        return scope -> new VariableReference(scope.findVariable(alias));
    }

}

