package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.Opcode;

public class LLB extends Opcode {
    @Override
    public String getMnemonic() {
        return "LLB";
    }

    @Override
    public int getArgumentCount() {
        return 2;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {
                ArgumentType.REGISTER,
                ArgumentType.IMMEDIATE8
        };
    }

    @Override
    public boolean setsZ() {
        return false;
    }

    @Override
    public boolean setsN() {
        return false;
    }

    @Override
    public boolean setsV() {
        return false;
    }
}
