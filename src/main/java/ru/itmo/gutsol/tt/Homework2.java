package ru.itmo.gutsol.tt;

import ru.itmo.gutsol.tt.common.GlobalScope;
import ru.itmo.gutsol.tt.common.LambdaStub;
import ru.itmo.gutsol.tt.common.NameGenerator;
import ru.itmo.gutsol.tt.common.StubGenerator;
import ru.itmo.gutsol.tt.lambda.Lambda;
import ru.itmo.gutsol.tt.lambda.Variable;
import ru.itmo.gutsol.tt.types.Type;
import ru.itmo.gutsol.tt.types.TypeController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;

public class Homework2 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new FileInputStream("task2.in"));
        PrintWriter out = new PrintWriter(new PrintStream("task2.out"));
        String s = in.nextLine();

        String res = getResult(s);
        out.println(res);

        in.close();
        out.close();
    }

    public static String getResult(String s) {
        LambdaStub lambdaStub = StubGenerator.toLambdaStub(s);
        Lambda lambda = lambdaStub.resolve(GlobalScope.getGlobalScope());

        final TypeController typeController = new TypeController();
        NameGenerator nm = new NameGenerator("t");
        Type type = lambda.deduceType(typeController);
        if (type != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Type: ").append(type.concrete(nm)).append("\n");
            Set<Variable> variables = lambda.getVariables();
            sb.append("Context: ").append(variables.isEmpty() ? "empty\n" : "\n");
            for (Variable var: variables) {
                sb.append('\t');
                sb.append(var.getName());
                sb.append(" : ");
                sb.append(typeController.getType(var).mono().concrete(nm));
            }
            return sb.toString();
        } else {
            return "No type deduced";
        }
    }
}
