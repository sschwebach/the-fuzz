package io.kersten.thefuzz;

import io.kersten.thefuzz.opcodes.*;

import java.util.ArrayList;
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
     * @param p        A program, passed into here because we'll want to be able to
     *                 analyze its supposed register state to make branching
     *                 condition decisions, as well as see which labels already
     *                 exist in the program so we don't overwrite the names. Also
     *                 need to be able to see which registers have been written to,
     *                 so we don't wind up generating nonsense instructions which
     *                 take things from uninitialized registers.
     * @param mnemonic Which opcode to generate a random instruction from.
     * @return A list of Instructions that get generated, to be added to the end
     * of the program.
     */
    public static List<Instruction> generate(Program p, String mnemonic) {
        if (!Opcode.MNEMONICS.contains(mnemonic.toUpperCase())) {
            throw new RuntimeException("Not a valid mnemonic: " + mnemonic);
        }

        ArrayList<Instruction> newInstrs = new ArrayList<Instruction>();
        Opcode op = opcodeFromMnemonic(mnemonic);

        //Generate arguments for this opcode...
        newInstrs.add(new Instruction(op));


        // Special cases for certain instructions which need to generate
        // alternate paths through the test (like branch with pass/failure)
        // and other jumps.

        //TODO: in all special cases, while elaborating instructions,
        // need to check if it sets flags and set those flags.
        // set flags if the instruction needs it
        if (mnemonic.equalsIgnoreCase("B")) {
            //TODO

            // Need to do the following:
            // 1) Look at current state of the Program's register file and
            // think up a condition on which we could branch.
            // 2) Generate failure and success paths - this could be in
            // either order (i.e. this can generate both taken and not-taken
            // branches).

        } else if (mnemonic.equalsIgnoreCase("JAL")) {
            //TODO

            // Need to do the following:
            // 1) Generate a failure path (right after this instruction,
            // since it's an unconditional).
            // 2) Generate a label to branch to and insert at the end of the
            // generated instructions.

            //TODO return
        } else if (mnemonic.equalsIgnoreCase("JR")) {
            //TODO

            // Need to do the following:
            // 1) Generate a failure path (right after the jump).
            // 2) Generate a label to branch to.

            //TODO return
        } else {
            // Otherwise, for each argument, assign it a value. These arguments
            // should usually just be immediate/register types since other ones
            // really do require additional logic (as seen above).
            for (ArgumentType t : op.getArgumentTypes()) {
                if (!(t == ArgumentType.IMMEDIATE4 || t == ArgumentType
                        .IMMEDIATE8 || t == ArgumentType.REGISTER || t ==
                        ArgumentType.VREGISTER)) {
                    throw new RuntimeException("Precious and fragile things " +
                            "need special handling - can't randomly generate " +
                            "label or condition names without additional " +
                            "logic.");
                }

                newInstrs.get(0).addArgument(new RandomArgument(p, t, null,
                        null));

                newInstrs.get(0).setComment("random fuzz");
            }


        }

        // Look at any generated instructions' target registers and set them to
        // valid in the program state.
        for (Instruction i : newInstrs)
            if (i.getArguments().size() > 0)
                if (i.getArguments().get(0).getType() == ArgumentType.REGISTER)
                    p.setRegisterValid(i.getArguments().get(0).value_register);



    }

    public static Opcode opcodeFromMnemonic(String mnemonic) {
        if (mnemonic.equalsIgnoreCase("ADD")) {
            return new ADD();
        }

        if (mnemonic.equalsIgnoreCase("ADDZ")) {
            return new ADDZ();
        }

        if (mnemonic.equalsIgnoreCase("SUB")) {
            return new SUB();
        }

        if (mnemonic.equalsIgnoreCase("AND")) {
            return new AND();
        }

        if (mnemonic.equalsIgnoreCase("NOR")) {
            return new NOR();
        }

        if (mnemonic.equalsIgnoreCase("SLL")) {
            return new SLL();
        }

        if (mnemonic.equalsIgnoreCase("SRL")) {
            return new SRL();
        }

        if (mnemonic.equalsIgnoreCase("SRA")) {
            return new SRA();
        }

        if (mnemonic.equalsIgnoreCase("LW")) {
            return new LW();
        }

        if (mnemonic.equalsIgnoreCase("SW")) {
            return new SW();
        }

        if (mnemonic.equalsIgnoreCase("LHB")) {
            return new LLB();
        }

        if (mnemonic.equalsIgnoreCase("B")) {
            return new B();
        }

        if (mnemonic.equalsIgnoreCase("JAL")) {
            return new JAL();
        }

        if (mnemonic.equalsIgnoreCase("JR")) {
            return new JR();
        }

        return null;
    }
}
