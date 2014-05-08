package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

public class HLT extends IOpcode {

    public HLT() {
        super(Opcode.HLT);
    }

    @Override
    public String getMnemonic() {
        return "HLT";
    }

    @Override
    public int getArgumentCount() {
        return 0;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[0];
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
