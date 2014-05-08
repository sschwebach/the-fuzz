package io.kersten.thefuzz;

import io.kersten.thefuzz.opcodes.HLT;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static final String VERSION = "0.0.4";

    public static void main(String[] args) {
        System.out.println("Welcome to TheFuzz.");
        System.out.println("Version " + VERSION);
        System.out.println("Implemented so far: ADD ADDZ LLB LHB SLL SRL SRA " +
                "LW SW SUB AND NOR");
        System.out.println("Not yet: JAL B");

        // Gather opcodes to test.
        Scanner scan = new Scanner(System.in);
        System.out.print("\nList opcodes to test (blank for all): ");
        String tmp = scan.nextLine();
        String tmpopcodes[] = tmp.split(" ");

        ArrayList<String> opcodes = new ArrayList<String>();

        for (String s : tmpopcodes) {
            if (IOpcode.isValidOpcode(s.toUpperCase()))
                opcodes.add(s.toUpperCase());
            else if (s.length() > 0)
                System.out.println("Not an opcode: " + s);
        }

        if (opcodes.size() == 0) {
            System.out.println("Testing all opcodes.");

            opcodes.addAll(IOpcode.getAllOpcodes());
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

        // What is the memory data offset?
        int dataoffset = -1;

        do {
            try {
                System.out.print("Memory data offset? (Try 30000): ");
                dataoffset = scan.nextInt();
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
        Program program = new Program(dataoffset);
        while (testlength > 0) {
            program.addInstructions(InstructionFactory.generateInstruction
                    (program, opcodes.get((int) (Math.random() * opcodes.size())
            )));

            testlength--;
        }

        program.terminate();

        System.out.println("");
        String output = program.print();

        File f = new File(filename + "_" + (int)
                (Math
                .random() *
                10000) + ".txt");

        System.out.println(f.getAbsolutePath());
        try {
        if (!f.createNewFile()) {
            throw new RuntimeException("Sadness in creating file ):");
        }} catch (IOException ioe) {
            System.err.println(ioe.getLocalizedMessage());
            throw new RuntimeException("Extra sadness in creating file ):");
        }

        if (!f.canWrite()) {
            throw new RuntimeException("Can't write...");
        }

        try {
            BufferedOutputStream bw = new BufferedOutputStream(new
                    FileOutputStream
                    (f));
            bw.write(output.getBytes(), 0, output.getBytes().length);
            bw.close();
        } catch (Exception e ){
            System.err.println("We lose.");
        }
    }
}
