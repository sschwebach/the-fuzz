package io.kersten.thefuzz;

import io.kersten.thefuzz.opcodes.HLT;

import java.util.ArrayList;
import java.util.List;

public class Program {

    /**
     * Convert a number to its corresponding Register enum entry.
     *
     * @param number The register number to convert.
     * @return A Register representing the requested register.
     */
    public static Register registerFromNumber(int number) {
        switch (number) {
            case 0:
                return Register.R0;
            case 1:
                return Register.R1;
            case 2:
                return Register.R2;
            case 3:
                return Register.R3;
            case 4:
                return Register.R4;
            case 5:
                return Register.R5;
            case 6:
                return Register.R6;
            case 7:
                return Register.R7;
            case 8:
                return Register.R8;
            case 9:
                return Register.R9;
            case 10:
                return Register.R10;
            case 11:
                return Register.R11;
            case 12:
                return Register.R12;
            case 13:
                return Register.R13;
            case 14:
                return Register.R14;
            case 15:
                return Register.R15;
            default:
                return null;
        }
    }

    // How many labels we've added to the program, modified by generators
    // which build the program. This is to avoid label naming conflicts.
    private int labelCount = 0;

    // Which registers have been written to at least once so far.
    private boolean validRegisters[] = new boolean[16];

    // The current state of the flags (after the last instruction executed).
    private boolean flag_z = false;
    private boolean flag_n = false;
    private boolean flag_v = false;

    private short registerFile[] = new short[16];

    private short memory[] = new short[65536];
    private ArrayList<Integer> validMemory = new ArrayList<Integer>();

    private int memoryDataOffset = 0;   // Due to unified memory, how far into
    // memory does the data begin?

    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    public Program(int memoryDataOffset) {
        validRegisters[0] = true; // R0 is always valid since it's tied to 0.
        registerFile[0] = 0;
        this.memoryDataOffset = memoryDataOffset;
    }

    public void addInstructions(List<Instruction> instrs) {
        instructions.addAll
                (instrs);
    }

    public String print() {
        String build = "";

        for (Instruction i : instructions) {
            build += i.print() + "\n";
            System.out.println(i.print());
        }

        return build;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public void incLabelCount() {
        this.labelCount++;
    }

    public boolean isRegisterValid(Register r) {
        return validRegisters[r.getNumber()];
    }

    public void setRegisterValid(Register r) {
        validRegisters[r.getNumber()] = true;
    }

    /**
     * Selects a register at random.
     *
     * @param mustBeValid If true, only select registers which are valid.
     * @return A randomly selected register.
     */
    public Register getRandomRegister(boolean mustBeValid) {
        int choice = -1;

        do {
            do {
                choice = (int) (Math.random() * validRegisters.length);
            } while (choice == 15); // Don't select register 15.
        } while (!validRegisters[choice] && mustBeValid); // Make sure valid

        return registerFromNumber(choice);
    }

    public boolean isFlag_z() {
        return flag_z;
    }

    public void setFlag_z(boolean flag_z) {
        this.flag_z = flag_z;
    }

    public boolean isFlag_n() {
        return flag_n;
    }

    public void setFlag_n(boolean flag_n) {
        this.flag_n = flag_n;
    }

    public boolean isFlag_v() {
        return flag_v;
    }

    public void setFlag_v(boolean flag_v) {
        this.flag_v = flag_v;
    }

    public short[] getRegisterFile() {
        return registerFile;
    }

    public int getMemoryDataOffset() {
        return memoryDataOffset;
    }

    public short[] getMemory() {
        return memory;
    }

    public ArrayList<Integer> getValidMemory() {
        return validMemory;
    }

    public void dumpRegisters() {
        for (int i = 0 ; i < registerFile.length; i++) {
            System.out.println("R" + i + "=" + registerFile[i]);
        }
    }

    /**
     * Insert a HLT at the end of the program. For its comment,
     * print the expected values of the registers, flags,
     * and valid memory addresses.
     */
    public void terminate() {
        instructions.add(new Instruction(new HLT()));

        String finalState = "\n";

        // Summarize registers
        for (int i = 0; i < registerFile.length; i++) {
            finalState += "\n# R" + i + " = " + registerFile[i];
        }

        // Summarize flags
        finalState += "\n\n# Z =" + (isFlag_z() ? "1" : "0") + " N = " +
                (isFlag_n()
                ? "1" : "0") + " V = " + (isFlag_v() ? "1" : "0");

        // Summarize memory:
        finalState += "\n# Valid memory addresses:";
        for (int i : validMemory) {
            finalState += "\n#    mem[" + i + "] = " + memory[i];
        }


        instructions.get(instructions.size() - 1).appendComment(finalState);
    }
}
