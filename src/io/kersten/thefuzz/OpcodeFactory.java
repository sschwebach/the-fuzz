package io.kersten.thefuzz;

public class OpcodeFactory {
    public Opcode generate(String mnemonic) {
        if (!Opcode.OPCODES.contains(mnemonic.toUpperCase())) {
            throw new RuntimeException("Not a valid mnemonic: " + mnemonic);
        }

        //TODO
    }
}
