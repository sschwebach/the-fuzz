package io.kersten.thefuzz;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static final String VERSION = "0.0.1";

    public static void main(String[] args) {
        System.out.println("Welcome to TheFuzz.");
        System.out.println("Version " + VERSION);

        // Gather opcodes to test.
        Scanner scan = new Scanner(System.in);
        System.out.print("\nList opcodes to test (blank for all): ");
        String tmp = scan.nextLine();
        String tmpopcodes[] = tmp.split(" ");

        ArrayList<String> opcodes = new ArrayList<String>();

        for (String s : tmpopcodes) {
            if (Opcode.MNEMONICS.contains(s.toUpperCase()))
                opcodes.add(s.toUpperCase());
            else if (s.length() > 0)
                System.out.println("Not an opcode: " + s);
        }

        if (opcodes.size() == 0) {
            System.out.println("Testing all opcodes.");
            opcodes.addAll(Opcode.MNEMONICS);
        }

        // How long should the tests be?
        int testlength = -1;

        do {
            try {
                System.out.print("Number of opcodes to test: ");
                testlength = scan.nextInt();
            } catch (InputMismatchException ime) {
                // Not a number...
                scan.nextLine(); // Clear buffer...
                testlength = -1; // ...and try again
            }
        } while (testlength <= 0);

        scan.nextLine();    // Clear the return from the integer read

        String filename;
        do {
            System.out.print("Save file as: ");
            filename = scan.nextLine();
        } while (filename.length() == 0);

        //Okay, start randomly generating opcodes!
        Program program = new Program();
        while (testlength > 0) {
            program.addOpcodes(OpcodeFactory.generate(opcodes.get(
                    (int) (Math.random() * opcodes.size())
            )));

            testlength--;
        }

        program.print();
    }
}
