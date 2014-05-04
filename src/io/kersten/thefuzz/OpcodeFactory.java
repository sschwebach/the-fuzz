package io.kersten.thefuzz;

import io.kersten.thefuzz.opcodes.*;

import java.util.List;

public class OpcodeFactory {

    /**
     * Generates a list of one or more instructions that need to be added to a
     * program in order to insert an opcode with this mnemonic. For example,
     * adding a branch instruction will imply the following:
     * * The generator looks at the current state of the registers and
     * decides on a condition to branch.
     * * The generator needs to make a failure path and a success path.
     * Branches can either be taken or not taken, and instructions can be
     * generated for both cases.
     * * Many opcodes might be generated in this process.
     *
     * @param p A program, passed into here because we'll want to be able to
     *          analyze its supposed register state to make branching
     *          condition decisions, as well as see which labels already
     *          exist in the program so we don't overwrite the names. Also
     *          need to be able to see which registers have been written to,
     *          so we don't wind up generating nonsense instructions which
     *          take things from uninitialized registers.
     * @param mnemonic Which opcode to generate a random instruction from.
     * @return A list of Instructions that get generated, to be added to the end
     * of the program.
     */
    public List<Instruction> generate(Program p, String mnemonic) {
        if (!Opcode.MNEMONICS.contains(mnemonic.toUpperCase())) {
            throw new RuntimeException("Not a valid mnemonic: " + mnemonic);
        }

        // Special cases for certain instructions which need to generate
        // alternate paths through the test (like branch with pass/failure)
        // and other jumps.

        if (mnemonic.equalsIgnoreCase("B")) {
            //TODO

            // Need to do the following:
            // 1) Look at current state of the Program's register file and
            // think up a condition on which we could branch.
            // 2) Generate failure and success paths - this could be in
            // either order (i.e. this can generate both taken and not-taken
            // branches).
        }

        if (mnemonic.equalsIgnoreCase("JAL")) {
            //TODO

            // Need to do the following:
            // 1) Generate a failure path (right after this instruction,
            // since it's an unconditional).
            // 2) Generate a label to branch to and insert at the end of the
            // generated instructions.
        }

        if (mnemonic.equalsIgnoreCase("JR")) {
            //TODO

            // Need to do the following:
            // 1) Generate a failure path (right after the jump).
            // 2) Generate a label to branch to.
        }

        Instruction newInstr;
        Opcode op = opcodeFromMnemonic(mnemonic);

        //Generate arguments for this opcode...
    }

    public static Opcode opcodeFromMnemonic(String mnemonic) {
        if (mnemonic.equalsIgnoreCase("ADD")) {
            return new ADD();
        }

        if (mnemonic.equalsIgnoreCase("ADDZ")) {
            return new ADDZ();
        }

        if (mnemonic.equalsIgnoreCase("ADDZ")) {
            return new ADDZ();
        }

        //TODO more
    }
}
