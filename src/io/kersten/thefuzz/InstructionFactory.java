package io.kersten.thefuzz;

import io.kersten.thefuzz.opcodes.*;

import java.util.ArrayList;
import java.util.List;

public class InstructionFactory {

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
    public static List<Instruction> generateInstruction(Program p,
                                                        String mnemonic) {
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

        } else if (mnemonic.equalsIgnoreCase("JR")) {
            //TODO

            // Need to do the following:
            // 1) Generate a failure path (right after the jump).
            // 2) Generate a label to branch to.


        } else if (mnemonic.equalsIgnoreCase("LW") || mnemonic
                .equalsIgnoreCase("SW")) {
            //TODO

            // Need to do the following:
            // 1) Make sure the random addresses generated are valid (this
            // means, the value in the register plus the immOffset is valid on a
            // load). For both, make sure that whatever address gets
            // generated (again, register + immOffset) is less than 65536 minus
            // dataOffset. Subtle but important.

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

                simulateLastInstruction(p, newInstrs.get(0));
            }


        }

        // Look at any generated instructions' target registers and set them to
        // valid in the program state. (This could be moved into the simulate
        // method.)
        for (Instruction i : newInstrs)
            if (i.getArguments().size() > 0)
                if (i.getArguments().get(0).getType() == ArgumentType.REGISTER)
                    p.setRegisterValid(i.getArguments().get(0).value_register);


    }

    private void simulateLastInstruction(Program p, Instruction instr) {

        // Simulate executing this instruction based on its arguments
        // and the current program state.
        boolean wouldSetZ = false;
        boolean wouldSetN = false;
        boolean wouldSetV = false;
        int aluResult = 0;
        int arg1 = 0;
        int arg2 = 0;

        // Get the integer values of what's happening. Presumnably,
        // all three-length instructions operate in the ALU. Lucky! Makes
        // this part easy.
        // XXX: This might fail in the future if we ever have opcodes
        // that set flags that aren't of the pattern of TARGET, ARG1,
        // ARG2.
        if (instr.getOpcode().getArgumentCount() == 3) {
            // Need to read arg1 and arg2 out of here. This means that this
            // instruction potentially sets flags. Ones that don't are LW and
            // SW.
            // Candidates: ADD, ADDZ, SUB, AND, NOR, SLL, SRL, SRA, LW, SW

            // For memory operations, update the memory or registers.
            if (instr.getOpcode().getMnemonic().equalsIgnoreCase("LW")) {
                // Load this memory location into the register. Presumably
                // the location is valid (since this came from a generator
                // which should be providing us with valid instructions).
                p.getRegisterFile()[
                        instr.getArguments().get(1).value_register.getNumber()
                        ] =
                        p.getMemory()[
                                p.getMemoryDataOffset() + p.getRegisterFile()[
                                        instr.getArguments().get(2)
                                                .value_register.getNumber()]
                                        + instr.getArguments().get(3)
                                        .value_immediate
                                ];
            } else if (instr.getOpcode().getMnemonic().equalsIgnoreCase("SW")) {
                p.getMemory()[
                        p.getMemoryDataOffset() +
                                p.getRegisterFile()[
                                        instr.getArguments().get(2)
                                                .value_register.getNumber()]
                                + instr.getArguments().get(3).value_immediate
                        ] =
                        p.getRegisterFile()[instr.getArguments().get(1)
                                .value_register.getNumber()];
            } else {
                // For ALU operations, update the registers and set flags.

            }


        } else if (instr.getOpcode().getArgumentCount() == 2) {
            // This instruction potentailly changes things in memory or
            // registers. I don't think these can set flags though.
            // Candidates: LHB, LLB, B
        } else {
            // These instructions just modify the PC.
            // Candidates: JAL, JR
        }

        if (instr.getOpcode().getMnemonic().equalsIgnoreCase("ADD")
        switch (instr.getOpcode().) {

        }

        // Check if this instruction sets flags.

        if (instr.getOpcode().setsZ() ||
                instr.getOpcode().setsN() ||
                instr.getOpcode().setsV()) {

            int arg1, arg2; //Integers because it's easy to detect overflow and
            // saturate our 16-bit registers. Some of these are immediate
            // arguments and some are register arguments...
            int arg1 = newInstrs.get(0).getArguments().get(1).val
        }

        int arg1 = (newInstrs.get(1).)
        newInstrs.get(0).setComment("random fuzz");
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
