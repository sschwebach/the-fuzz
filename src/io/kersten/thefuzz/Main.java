package io.kersten.thefuzz;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        System.out.println("Welcome to TheFuzz.");
        System.out.println("Version " + VERSION);

        //Gather opcodes to test.
        Scanner scan = new Scanner(System.in);
        System.out.println("\nList opcodes to test (blank for all): ");
        String tmp = scan.nextLine();
        String tmpopcodes[] = tmp.split(" ");

        ArrayList<String> opcodes = new ArrayList<String>();
        for (String s : tmpopcodes) {
            if (Opcode.OPCODES.contains(s.toUpperCase()))
                opcodes.add(s.toUpperCase());
            else
                System.out.println("Not an opcode: " + s);
        }

        if (tmpopcodes.length == 0) {
            System.out.println("Testing all opcodes.");
            opcodes.addAll(Opcode.OPCODES);
        }

    }
}
