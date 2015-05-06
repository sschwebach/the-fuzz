package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

public class JAL extends IOpcode {

    public JAL() {
        super(Opcode.CALL);
    }

    @Override
    public String getMnemonic() {
        return "JAL";
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[]{
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

