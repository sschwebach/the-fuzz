package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

public class JR extends IOpcode {
    public JR() {
        super(Opcode.JR);
    }

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
