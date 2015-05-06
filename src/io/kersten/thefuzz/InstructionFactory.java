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
        if (!IOpcode.isValidOpcode(mnemonic.toUpperCase())) {
            throw new RuntimeException("Not a valid mnemonic: " + mnemonic);
        }

        ArrayList<Instruction> newInstrs = new ArrayList<Instruction>();
        IOpcode op = opcodeFromMnemonic(mnemonic);

        //Generate arguments for this opcode...
        newInstrs.add(new Instruction(op));


        // Special cases for certain instructions which need to generate
        // alternate paths through the test (like branch with pass/failure)
        // and other jumps.

        //TODO: in all special cases, while elaborating instructions,
        // need to check if it sets flags and set those flags.
        // set flags if the instruction needs it
        if (mnemonic.equalsIgnoreCase("B")) {

            // Need to do the following:
            // 1) Look at current state of the Program's register file and
            // think up a condition on which we could branch.
            // 2) Generate failure and success paths - this could be in
            // either order (i.e. this can generate both taken and not-taken
            // branches). In one case, there will be an unconditional branch at
            // the end of the success path to skip the failure path.
            int thisLabel = p.getLabelCount();
            p.incLabelCount();

            // Choose a condition
            int condIdx = (int) (Math.random() * Condition.values().length);
            Condition cond = Condition.values()[condIdx];

            // Determine if a branch on this condition will be taken or not.
            boolean conditionIsTrue = false;
            switch (cond) {
                case NEQ:
                    conditionIsTrue = !p.isFlag_z();
                    break;
                case EQ:
                    conditionIsTrue = p.isFlag_z();
                    break;
                case GT:
                    conditionIsTrue = !p.isFlag_z() && !p.isFlag_n();
                    break;
                case LT:
                    conditionIsTrue = p.isFlag_n();
                    break;
                case GTE:
                    conditionIsTrue = !p.isFlag_n();
                    break;
                case LTE:
                    conditionIsTrue = p.isFlag_n() || p.isFlag_z();
                    break;
                case OVFL:
                    conditionIsTrue = p.isFlag_v();
                    break;
                case UNCOND:
                    conditionIsTrue = true;
                    break;
            }

            newInstrs.get(0).appendComment("branch on " + cond + ", " +
                    "take = " + (conditionIsTrue ? "yes" : "no"));

            // Add the condition. If the condition is true and we take the
            // branch, we pass. Else, branch to a failure condition and add a
            // skip before it in the regular code.
            newInstrs.get(0).addArgument(new Argument(p, ArgumentType.CONDITION,
                    null, cond));
            newInstrs.get(0).addArgument(new Argument(p, ArgumentType.LABEL,
                    (conditionIsTrue ? "taken" + thisLabel : "nottaken"), null));

            if (conditionIsTrue) {
                // We need a failure path followed by a thisLabel,
                // since this branch should skip it.

                generateFailurePath(p, newInstrs);
                newInstrs.add(new Instruction(new Label("taken" + thisLabel)));
            } else {
                // The fall-through path is correct, and if we take the branch
                // on accident we need to go to the global nottaken failure.
                // Set the program to generate a global nottaken failure case.
                p.setGenerateGlobalNotTaken();
            }

            // XXX: Don't really need to simulate this,
            // but don't want to forget to call simulate if we ever wind up
            // needing it.
            simulateLastInstruction(p, newInstrs.get(0));

        } else if (mnemonic.equalsIgnoreCase("JAL")) {

            // Need to do the following:
            // 1) Generate a failure path (right after this instruction,
            // since it's an unconditional).
            //
            // 2) Generate a label to branch to and insert at the end of the
            // generated instructions.

            int thisLabel = p.getLabelCount();
            p.incLabelCount();

            // Add a label to jump to.
            newInstrs.get(0).addArgument(new Argument(p, ArgumentType.LABEL,
                    "linklabel" + thisLabel, null));


            simulateLastInstruction(p, newInstrs.get(0)); // Load R15

            // Generate a failure path and then add our target label at the end.
            generateFailurePath(p, newInstrs);

            // Add target label
            newInstrs.add(new Instruction(new Label("linklabel" + thisLabel)));

        } else if (mnemonic.equalsIgnoreCase("JR")) {

            // XXX: Not actually implementing because this will jump us back
            // to wherever we JAL'd from, and that'll be a little hard to test
            // Really, any complex JR constructions should be tested manually.

            // Need to do the following:
            // 0) Only generate this if R15 is valid.
            // 1) Generate a failure path (right after the jump).

            if (!p.isRegisterValid(Register.R15))
                return new ArrayList<Instruction>();

            newInstrs.get(0).addArgument(new Argument(p,
                    ArgumentType.REGISTER, null, null));
            newInstrs.get(0).getArguments().get(0).value_register =
                    Register.R15;

            simulateLastInstruction(p, newInstrs.get(0));

            // Add a fall-through failure path after that.
            generateFailurePath(p, newInstrs);
        } else if (mnemonic.equalsIgnoreCase("LW") || mnemonic
                .equalsIgnoreCase("SW")) {

            // Need to do the following:
            // 1) Make sure the random addresses generated are valid (this
            // means, the value in the register plus the immOffset is valid on a
            // load). For both, make sure that whatever address gets
            // generated (again, register + immOffset) is less than 65536 minus
            // dataOffset. Subtle but important.

            if (mnemonic.equalsIgnoreCase("LW")) {
                // Generate a valid memory address to load from...
                if (p.getValidMemory().size() == 0) {
                    // No valid addresses - can't generate a load instruction
                    // yet!
                    return new ArrayList<Instruction>();
                }

                int addr = p.getValidMemory().get((int) (Math.random() * p
                        .getValidMemory().size()));

                // Think of a split for how we want to index ths memory...
                // Maximum offset is 4 bits, so we'll want to be able to go
                // +7/-8 on it.
                int offset = (int) (Math.random() * 16) - 8;

                // Need to insert the address less the offset into some random
                // register...
                int intoReg = addr - offset; // These will already be offset
                // by p.getMemoryDataOffset, since
                // they're in the valid list.

                // Need to get this value into a register. Need to use a LHB
                // potentially, and definitely a LLB.

                // First select a register to form the address
                Register chosenRegister;

                do {
                    chosenRegister = p.getRandomRegister(false);
                } while (chosenRegister == Register.R0);

                // set up the lw instruction
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));

                do {
                    newInstrs.get(0).getArguments().get(0).value_register =
                            p.getRandomRegister(false);
                } while (newInstrs.get(0).getArguments().get(0)
                        .value_register == Register.R0);

                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));
                newInstrs.get(0).getArguments().get(1).value_register =
                        chosenRegister;
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.IMMEDIATE4, null, null));
                newInstrs.get(0).getArguments().get(2).value_immediate =
                        (short) offset;


                newInstrs.get(0).appendComment(newInstrs.get(0).getArguments()
                        .get(0).value_register + "<-mem[" + addr
                        + "]");

                // See if we need an LHB
                if (((short) intoReg & 0xFF00) > 0) {
                    // Yes it's needed.

                    newInstrs.add(0, new Instruction(new LHB()));
                    newInstrs.get(0).addArgument(new Argument(p,
                            ArgumentType.REGISTER, null, null));

                    newInstrs.get(0).getArguments().get(0).value_register
                            = chosenRegister;

                    // Sets its value to the lower bits of the intoReg
                    newInstrs.get(0).addArgument(new Argument(p,
                            ArgumentType.IMMEDIATE8, null, null));
                    newInstrs.get(0).getArguments().get(1).value_immediate =
                            (short) ((short) (intoReg & 0xFF00) >> 8);
                    newInstrs.get(0).appendComment("Load upper for lw");
                }

                newInstrs.add(0, new Instruction(new LLB()));
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));

                newInstrs.get(0).getArguments().get(0).value_register =
                        chosenRegister;

                // Sets its value to the lower bits of the intoReg
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.IMMEDIATE8, null, null));
                newInstrs.get(0).getArguments().get(1).value_immediate =
                        (short) (intoReg & 0xFF);
                newInstrs.get(0).appendComment("Load lower for lw");

                for (Instruction i : newInstrs) {
                    simulateLastInstruction(p, i);
                }
            } else {
                // Store word. Need to find somewhere to store something and
                // think of something to store.

                // Store it somewhere in valid memory, but watch out for the
                // data memory offset cap.
                int addr;

                // Eh, screw it, keep addresses positive in 16-bit signed.
                do {
                    addr = (int) (Math.random() * (p.getMemory().length - p
                            .getMemoryDataOffset()));
                } while (addr > ((1 << 15) - 1));

                int offset = (int) (Math.random() * 16) - 8;

                int intoReg = addr - offset;

                p.getValidMemory().add(addr);

                // Okay, pick a valid register whose contents we want to store.

                // First select a register to form the address
                Register chosenRegister;

                do {
                    chosenRegister = p.getRandomRegister(false);
                } while (chosenRegister == Register.R0);

                // set up sw instruction
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));
                newInstrs.get(0).getArguments().get(0).value_register =
                        p.getRandomRegister(true);
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));
                newInstrs.get(0).getArguments().get(1).value_register =
                        chosenRegister;
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.IMMEDIATE4, null, null));
                newInstrs.get(0).getArguments().get(2).value_immediate =
                        (short) offset;

                newInstrs.get(0).appendComment(newInstrs.get(0).getArguments()
                        .get(0).value_register + "=" + p.getRegisterFile()[newInstrs.get(0).getArguments()
                        .get(0).value_register.getNumber()] + "->mem[" + addr
                        + "]");

                // See if we need an LHB
                if (((short) intoReg & 0xFF00) > 0) {
                    // Yes it's needed.
                    newInstrs.add(0, new Instruction(new LHB()));
                    newInstrs.get(0).addArgument(new Argument(p,
                            ArgumentType.REGISTER, null, null));

                    newInstrs.get(0).getArguments().get(0).value_register
                            = chosenRegister;

                    // Sets its value to the lower bits of the intoReg
                    newInstrs.get(0).addArgument(new Argument(p,
                            ArgumentType.IMMEDIATE8, null, null));
                    newInstrs.get(0).getArguments().get(1).value_immediate =
                            (short) ((short) (intoReg & 0xFF00) >> 8);
                    newInstrs.get(0).appendComment("Load upper for sw");
                }

                newInstrs.add(0, new Instruction(new LLB()));
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.REGISTER, null, null));

                newInstrs.get(0).getArguments().get(0).value_register =
                        chosenRegister;

                // Sets its value to the lower bits of the intoReg
                newInstrs.get(0).addArgument(new Argument(p,
                        ArgumentType.IMMEDIATE8, null, null));
                newInstrs.get(0).getArguments().get(1).value_immediate =
                        (short) (intoReg & 0xFF);
                newInstrs.get(0).appendComment("Load lower for sw");

                for (Instruction i : newInstrs) {
                    simulateLastInstruction(p, i);
                }
            }
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

                // For each argument the instruction expects, add a random one.
                newInstrs.get(0).addArgument(new RandomArgument(p, t, null,
                        null));


            }

            simulateLastInstruction(p, newInstrs.get(0));
        }

        // Look at any generated instructions' target registers and set them to
        // valid in the program state. (This could be moved into the simulate
        // method.)
        for (Instruction i : newInstrs)
            if (i.getArguments().size() > 0)
                if (i.getArguments().get(0).getType() == ArgumentType.REGISTER)
                    // Don't know for sure if any addz executed,
                    // so set it valid only when it simulates (different method)
                    p.setRegisterValid(i.getArguments().get(0)
                            .value_register);

        return newInstrs;
    }

    private static void simulateLastInstruction(Program p, Instruction instr) {
        // Sanity check on R0...
        if (p.getRegisterFile()[0] != 0) {
            p.print();
            throw new RuntimeException("R0 became non-zero!");
        }

        // Simulate executing this instruction based on its arguments
        // and the current program state.

        // These flag values will only be invoked if it winds up that this
        // instruction actually should be setting flags.
        boolean setZTo = false;
        boolean setNTo = false;
        boolean setVTo = false;

        int aluResult = 0;
        int arg1 = 0;
        int arg2 = 0;

        // Get the integer values of what's happening. Presumnably,
        // all three-length instructions operate in the ALU. Lucky! Makes
        // this part easy.
        // XXX: This might fail in the future if we ever have opcodes
        // that set flags that aren't of the pattern of TARGET, ARG1,
        // ARG2.
        if (instr.getiOpcode().getArgumentCount() == 3) {
            // Need to read arg1 and arg2 out of here. This means that this
            // instruction potentially sets flags. Ones that don't are LW and
            // SW.
            // Candidates: ADD, ADDZ, SUB, AND, NOR, SLL, SRL, SRA, LW, SW

            // For memory operations, update the memory or registers.
            if (instr.getiOpcode().getMnemonic().equalsIgnoreCase("LW")) {
                // Load this memory location into the register. Presumably
                // the location is valid (since this came from a generator
                // which should be providing us with valid instructions).

                if (instr.getArguments().get(0).value_register == Register.R0) {
                    instr.appendComment("No change to R0");
                } else {
                    p.getRegisterFile()[
                            instr.getArguments().get(0).value_register.getNumber()
                            ] =
                            p.getMemory()[
                                    p.getMemoryDataOffset() + p.getRegisterFile()[
                                            instr.getArguments().get(1)
                                                    .value_register.getNumber()]
                                            + instr.getArguments().get(2)
                                            .value_immediate
                                    ];
                }
            } else if (instr.getiOpcode().getMnemonic().equalsIgnoreCase("SW")) {
                p.getMemory()[
                        p.getMemoryDataOffset() +
                                p.getRegisterFile()[
                                        instr.getArguments().get(1)
                                                .value_register.getNumber()]
                                + instr.getArguments().get(2).value_immediate
                        ] =
                        p.getRegisterFile()[instr.getArguments().get(0)
                                .value_register.getNumber()];
            } else {
                // For ALU operations, update the registers and set flags.

                // XXX: Assumptions here about argument types, again.
                // Arg1 is always in a register.
                arg1 = p.getRegisterFile()[instr.getArguments().get(1)
                        .value_register.getNumber()];

                // Arg2 depends on if this is a shift or not. Check for an
                // immediate 4-bit argument.
                if (instr.getiOpcode().getArgumentTypes()[2] ==
                        ArgumentType.IMMEDIATE4) {
                    arg2 = instr.getArguments().get(2).value_immediate;
                } else if (instr.getiOpcode().getArgumentTypes()[2] ==
                        ArgumentType.VREGISTER) {
                    arg2 = p.getRegisterFile()[instr.getArguments().get(2)
                            .value_register.getNumber()];
                } else {
                    throw new RuntimeException("Unexpected argument type " +
                            "during simulation!");
                }

                // Perform the ALU operation.
                switch (instr.getiOpcode().getOpcode()) {
                    case ADD:
                        if (arg1 + arg2 > (1 << 15) - 1)
                            setVTo = true;
                        aluResult = Math.min(arg1 + arg2, (1 << 15) - 1);
                        if (aluResult == 0)
                            setZTo = true;
                        if (aluResult < 0)
                            setNTo = true;
                        instr.appendComment("(" + arg1 + "+" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case SUB:
                        if (arg1 - arg2 < -(1 << 15))
                            setVTo = true;
                        aluResult = Math.max(arg1 - arg2, -(1 << 15));
                        if (aluResult == 0)
                            setZTo = true;
                        if (aluResult < 0)
                            setNTo = true;
                        instr.appendComment("(" + arg1 + "-" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case NAND:
                        aluResult = ~(arg1 & arg2);
                        setVTo = false;
                        setNTo = false;
                        if (aluResult == 0)
                            setZTo = true;
                        instr.appendComment("(" + arg1 + "!&" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case XOR:
                        aluResult = (arg1 ^ arg2);
                        setVTo = false;
                        setNTo = false;
                        if (aluResult == 0)
                            setZTo = true;
                        instr.appendComment("(~(" + arg1 + "^" + arg2 + ")=" +
                                (short) aluResult + ")");
                        break;
                    case INC:
                        // TODO
                    case SLL:
                        aluResult = arg1 << arg2;
                        if (aluResult == 0)
                            setZTo = true;
                        instr.appendComment("(" + arg1 + "<<" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case SRL:
                        aluResult = arg1 >> arg2;
                        if (aluResult == 0)
                            setZTo = true;
                        instr.appendComment("(" + arg1 + ">>" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case SRA:
                        // This is tricky because we're doing these
                        // high-level on ints but need some zeros inserted
                        // down in the 16-bit range. So cast to short first.
                        aluResult = ((short) arg1 >>> arg2);
                        if (aluResult == 0)
                            setZTo = true;
                        instr.appendComment("(" + arg1 + ">>>" + arg2 + "=" +
                                (short) aluResult + ")");
                        break;
                    case INC:
                        throw new RuntimeException("INC currently not supported!");
                    default:
                        throw new RuntimeException("How did we get here? (1)");
                }

                // Okay, update the state of the target register with the ALU
                // result. Don't write to R0 though...
                if (instr.getArguments().get(0).value_register != Register.R0)
                    p.getRegisterFile()[instr.getArguments().get(0)
                            .value_register.getNumber()] = (short) aluResult;
                else
                    instr.appendComment("No change to R0");
            }
        } else if (instr.getiOpcode().getArgumentCount() == 2) {
            // This instruction potentailly changes things in memory or
            // registers. I don't think these can set flags though.
            // Candidates: LHB, LLB, B

            switch (instr.getiOpcode().getOpcode()) {
                case LHB:
                    if (instr.getArguments().get(0).value_register != Register
                            .R0) {
                        p.getRegisterFile()[instr.getArguments().get(0)
                                .value_register.getNumber()] = (short) ((p
                                .getRegisterFile()[instr.getArguments().get(0)
                                .value_register.getNumber()]) & 0xFF);
                        p.getRegisterFile()[instr.getArguments().get(0)
                                .value_register.getNumber()] += (instr
                                .getArguments().get(1).value_immediate << 8);
                    } else {
                        instr.appendComment("No change to R0");
                    }
                    instr.appendComment(instr.getArguments().get(0)
                            .value_register + "=" +
                            p.getRegisterFile()[instr.getArguments().get(0)
                                    .value_register.getNumber()]);
                    break;
                case LLB:
                    if (instr.getArguments().get(0).value_register != Register
                            .R0) {
                        p.getRegisterFile()[instr.getArguments().get(0)
                                .value_register.getNumber()] = (instr
                                .getArguments().get(1).value_immediate);
                    } else {
                        instr.appendComment("No change to R0");
                    }

                    instr.appendComment(instr.getArguments().get(0)
                            .value_register + "=" +
                            p.getRegisterFile()[instr.getArguments().get(0)
                                    .value_register.getNumber()]);
                    break;
                case B:
                    // TODO: Branching really doesn't need to be simulated
                    // unless we start simulating more complex processor
                    // structures, which we don't need to do for the purposes
                    // of our testing. For now, we just generate the control
                    // path for branching.
                    break;
                default:
                    throw new RuntimeException("How did we get here? (2)");
            }
        } else {
            // These instructions just modify the PC.
            // Candidates: CALL, RET

            switch (instr.getiOpcode().getOpcode()) {
                case CALL:
                    //TODO
                    throw new RuntimeException("CALL not implemented - too complex " +
                            "to make subroutines right now. Test manually.");
                case RET:
                    //TODO
                    throw new RuntimeException("RET not implemented - too complex " +
                            "to make subroutines right now. Test manually.");
                /*
                case JR:
                    throw new RuntimeException("JR not implemented - too complex " +
                            "to make subroutines right now. Test manually.");
                case JAL:
                    // Loads PC + 1 into R15. The program doesn't have this
                    // instruction yet though. It needs one past this
                    // instruction.
                    // XXX: Assume that this instruction would be the next one
                    // to be added to p's list of instructions,
                    // so the last instruction in p.instructions is PC - 1.
                    short pcp1 = (short)(p.getInstructions().size() + 2);
                    p.getRegisterFile()[Register.R15.getNumber()] = pcp1;

                    // Also set R15 as valid now...
                    p.setRegisterValid(Register.R15);
                    instr.appendComment("R15 linked to PC=" + pcp1);

                    break;
                    */
            }
        }

        // Now, set flags if the previous instruction would have.

        if (instr.getiOpcode().setsZ()) {
            p.setFlag_z(setZTo);
            instr.appendComment("Z->" + (setZTo ? "1" : "0"));
        }

        if (instr.getiOpcode().setsN()) {
            p.setFlag_n(setNTo);
            instr.appendComment("N->" + (setNTo ? "1" : "0"));
        }

        if (instr.getiOpcode().setsV()) {
            p.setFlag_v(setVTo);
            instr.appendComment("V->" + (setVTo ? "1" : "0"));
        }

    }

    public static IOpcode opcodeFromMnemonic(String mnemonic) {
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

        if (mnemonic.equalsIgnoreCase("LLB")) {
            return new LLB();
        }

        if (mnemonic.equalsIgnoreCase("LHB")) {
            return new LHB();
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

        if (mnemonic.equalsIgnoreCase("NAND")){
            return new NAND();
        }

        if (mnemonic.equalsIgnoreCase("XOR")){
            return new XOR();
        }

        if (mnemonic.equalsIgnoreCase("CALL")){
            return new CALL();
        }

        if (mnemonic.equalsIgnoreCase("RET")){
            return new RET();
        }

        if (mnemonic.equalsIgnoreCase("INC")){
            return new INC();
        }

        return null;
    }

    /**
     * Generates a dead-end failure path of instrs into which the program
     * control flow will fall if a branch/jump fails.
     *
     * @param p      The program so we can look at register states
     * @param instrs The instruction set which will be populated.
     */
    private static void generateFailurePath(Program p, ArrayList<Instruction>
            instrs) {

        // The most basic implementation of this will just be a failure label
        // followed by a halt.
        instrs.add(new Instruction(new Label("fail" + p.getLabelCount())));
        p.incLabelCount();

        instrs.add(new Instruction(new HLT()));

        // TODO: We might want to generate longer failure paths at random to
        // test things like erroneous instruction fetches or something.
        // XXX: Be careful though! Any target registers referenced here will
        // become valid because of our overzealous validity checking earlier
        // in generateInstruction. So fill it with NoOPs if at all possible.
        // Like an add into R0.
    }
}
