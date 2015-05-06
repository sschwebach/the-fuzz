package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

/**
 * Created by sschwebach on 3/17/15.
 */
public class NAND extends IOpcode {
    //TODO Everything
    public NAND() {
        super(Opcode.NAND);
    }

    @Override
    public String getMnemonic() {
        return "NAND";
    }

    @Override
    public int getArgumentCount() {
        return 3;
    }

    @Override
    public ArgumentType[] getArgumentTypes() {
        return new ArgumentType[] {
                ArgumentType.REGISTER,
                ArgumentType.VREGISTER,
                ArgumentType.VREGISTER
        };
    }

    @Override
    public boolean setsZ() {
        return true;
    }

    @Override
    public boolean setsN() {
        return true;
    }

    @Override
    public boolean setsV() {
        return true;
    }
}
