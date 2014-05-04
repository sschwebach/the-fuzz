package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.Opcode;

public class JR extends Opcode {
    @Override
    public String getMnemonic() {
        return "JR";
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {
                ArgumentType.VREGISTER
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
