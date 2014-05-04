package io.kersten.thefuzz;

import java.util.ArrayList;

/**
 *
 */
public abstract class Opcode {

    public static final ArrayList<String> OPCODES = new ArrayList<String>();

    static {
        OPCODES.add("ADD");
        OPCODES.add("ADDZ");
        OPCODES.add("SUB");
        OPCODES.add("AND");
        OPCODES.add("NOR");
        OPCODES.add("SLL");
        OPCODES.add("SRL");
        OPCODES.add("SRA");
        OPCODES.add("LW");
        OPCODES.add("SW");
        OPCODES.add("LHB");
        OPCODES.add("LLB");
        OPCODES.add("B");
        OPCODES.add("JAL");
        OPCODES.add("JR");
    }

    public abstract String getMnemonic();

    public abstract int getArgumentCount();

}
