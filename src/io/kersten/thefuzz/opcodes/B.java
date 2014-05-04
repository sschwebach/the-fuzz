package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

public class B extends IOpcode {
    public B() {
        super(Opcode.B);
    }
    @Override
    public String getMnemonic() {
        return "B";
    }

    @Override
    public int getArgumentCount() {
        return 2;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {
                ArgumentType.CONDITION,
                ArgumentType.LABEL
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
