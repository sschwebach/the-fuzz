package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.Opcode;

public class Label extends Opcode {

    private String title;

    public Label(String title) {
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
}
