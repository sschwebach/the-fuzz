package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.Opcode;

public class B extends Opcode {
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
}
