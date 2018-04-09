package ru.itmo.gutsol.tt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Homework3 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new FileInputStream("task3.in"));
        PrintWriter out = new PrintWriter(new PrintStream("task3.out"));
        String s = in.nextLine();

        String res = Homework2.getResult(s);
        out.println(res);

        in.close();
        out.close();
    }
}
