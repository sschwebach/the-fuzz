package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

public class Label extends IOpcode {

    private String title;

    public Label(String title) {

        super(Opcode.SRA);
        this.title = title;
    }

    @Override
    public String getMnemonic() {
        return title + ":";
    }

    @Override
    public int getArgumentCount() {
        return 0;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {};
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
