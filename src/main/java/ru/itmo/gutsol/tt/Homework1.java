package ru.itmo.gutsol.tt;

import ru.itmo.gutsol.tt.common.GlobalScope;
import ru.itmo.gutsol.tt.common.LambdaStub;
import ru.itmo.gutsol.tt.common.StubGenerator;
import ru.itmo.gutsol.tt.lambda.Lambda;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Homework1 {
    public static void main(String... args) throws FileNotFoundException {
        Scanner in = new Scanner(new FileInputStream("task1.in"));
        PrintWriter out = new PrintWriter(new PrintStream("task1.out"));
        String s = in.nextLine();

        String res = getResult(s);
        out.println(res);

        in.close();
        out.close();
    }

    public static String getResult(String s) {
        LambdaStub lambdaStub = StubGenerator.toLambdaStub(s);
        Lambda lambda = lambdaStub.resolve(GlobalScope.getGlobalScope());
        return lambda.reduceFully().toString();
    }
}
