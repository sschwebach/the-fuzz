package io.kersten.thefuzz;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public abstract class IOpcode {

    public Opcode getOpcode() {
        return this.opcode;
    }

    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    public IOpcode(Opcode opcode) {
        setOpcode(opcode);
    }

    public static Collection<String> getAllOpcodes() {
        ArrayList<String> ops = new ArrayList<String>();

        for (Opcode o : Opcode.values()) {
            if (o != Opcode.HLT && o != Opcode.RET)
                ops.add(o.toString().toUpperCase());
        }

        return ops;
    }

    public static boolean isValidOpcode(String s) {
        for (Opcode o : Opcode.values()) {
            if (o.name().equalsIgnoreCase(s))
                return true;
        }

        return false;
    }

    public enum Opcode {
        ADD, SUB, NAND, XOR, INC, SRA, SRL, SLL, LW, SW, LHB, LLB, B, HLT, CALL, RET;
    }

    private Opcode opcode;

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
