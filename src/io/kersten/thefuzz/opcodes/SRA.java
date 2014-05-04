package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.Opcode;

public class SRA extends Opcode {
    @Override
    public String getMnemonic() {
        return "SRA";
    }

    @Override
    public int getArgumentCount() {
        return 3;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {
                ArgumentType.REGISTER,
                ArgumentType.REGISTER,
                ArgumentType.IMMEDIATE4
        };
    }
}
