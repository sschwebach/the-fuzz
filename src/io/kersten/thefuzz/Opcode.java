package io.kersten.thefuzz;

import java.util.ArrayList;

/**
 *
 */
public abstract class Opcode {

    public static final ArrayList<String> MNEMONICS = new ArrayList<String>();

    static {
        MNEMONICS.add("ADD");
        MNEMONICS.add("ADDZ");
        MNEMONICS.add("SUB");
        MNEMONICS.add("AND");
        MNEMONICS.add("NOR");
        MNEMONICS.add("SLL");
        MNEMONICS.add("SRL");
        MNEMONICS.add("SRA");
        MNEMONICS.add("LW");
        MNEMONICS.add("SW");
        MNEMONICS.add("LHB");
        MNEMONICS.add("LLB");
        MNEMONICS.add("B");
        MNEMONICS.add("JAL");
        MNEMONICS.add("JR");
    }

    public abstract String getMnemonic();

    public abstract int getArgumentCount();

    /**
     * @return An array of ArgumentType, one for each argument,
     * describing what type that argument expects.
     */
    public abstract ArgumentType[] getArgumentTypes();

    public abstract boolean setsZ();

    public abstract boolean setsN();

    public abstract boolean setsV();
}
