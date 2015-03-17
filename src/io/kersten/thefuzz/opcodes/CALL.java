package io.kersten.thefuzz.opcodes;

import io.kersten.thefuzz.ArgumentType;
import io.kersten.thefuzz.IOpcode;

/**
 * Created by sschwebach on 3/17/15.
 */
public class CALL extends IOpcode {
    //TODO everything
    public CALL(){
        super(Opcode.CALL);
    }
    @Override
    public String getMnemonic() {
        return null;
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
