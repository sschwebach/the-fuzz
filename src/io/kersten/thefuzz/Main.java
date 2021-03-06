package io.kersten.thefuzz;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static final String VERSION = "1.0.0";

    public static void main(String[] args) {

        // Which opcodes should be tested?
        ArrayList<String> opcodes = new ArrayList<String>();

        // How long should the tests be?
        int testlength = -1;
        int originalTestLength = -1;

        // What is the memory data offset?
        int dataoffset = -1;

        // What is the file name?
        String filename;

        // How many times to generate the same type of test.
        int iterations = 1;

        // We might be running in batch mode...
        if (args.length == 5) {
            // Usage: "opcodes opcodes opcodes" testLength dataOffset filename
            // iterations

            // Gather opcodes
            if (args[0].equalsIgnoreCase("all")) {
                opcodes.addAll(IOpcode.getAllOpcodes());
            } else {
                for (String s : args[0].split(" ")) {
                    if (IOpcode.isValidOpcode(s.toUpperCase()))
                        opcodes.add(s.toUpperCase());
                    else if (s.length() > 0)
                        System.out.println("Not an opcode: " + s);
                }
            }

            // read test length
            testlength = Integer.parseInt(args[1]);
            originalTestLength = testlength;

            // read data offset
            dataoffset = Integer.parseInt(args[2]);

            // read filename
            filename = args[3];

            iterations = Integer.parseInt(args[4]);
        } else if (args.length > 0) {
            System.err.println("Usage: \"opcodes opcodes opcodes\" testLength" +
                    " dataOffset filename iterations");
            return;
        } else {
            System.out.println("Welcome to TheFuzz.");
            System.out.println("Version " + VERSION);
            System.out.println("Supported: ADD SUB NAND XOR INC SRA SRL SLL LW SW LHB LLB B CALL RET");

            // Gather opcodes to test.
            Scanner scan = new Scanner(System.in);
            System.out.print("\nList opcodes to test (blank for all): ");
            String tmp = scan.nextLine();
            String tmpopcodes[] = tmp.split(" ");

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

            do {
                try {
                    System.out.print("Number of opcodes to test: ");
                    testlength = scan.nextInt();
                    originalTestLength = testlength;
                } catch (InputMismatchException ime) {
                    // Not a number...
                    scan.nextLine(); // Clear buffer...
                    testlength = -1; // ...and try again
                }
            } while (testlength <= 0);

            scan.nextLine();    // Clear the return from the integer read

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

            do {
                System.out.print("Save file as: ");
                filename = scan.nextLine();
            } while (filename.length() == 0);

        }

        for (int i = 0; i < iterations; i++) {
            testlength = originalTestLength;

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
            String output = "# Test Name: " + filename + " (iteration " + i
                    + ")\n# Generated by " +
                    "TheFuzz v" + VERSION + " written by Alex Kersten and " +
                    "Kenneth Siu for ECE552 (https://github.com/akersten/TheFuzz)\n" +
                    "Spring 2015 Functionality Added by Sam Schwebach" +
                    "\n\n" + program.print();

            File f = new File(filename + "_" + i + ".asm");

            System.out.println(f.getAbsolutePath());
            try {
                if (!f.createNewFile()) {
                    throw new RuntimeException("Sadness in creating file ):");
                }
            } catch (IOException ioe) {
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
            } catch (Exception e) {
                System.err.println("We lose.");
            }
        }
    }
}
