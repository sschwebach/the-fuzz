package io.kersten.thefuzz;

import java.util.ArrayList;

/**
 * Once an opcode has been generated with valid arguments and things, it becomes
 * an instruction.
 */
public class Instruction {

    private Opcode opcode;

    private ArrayList<Argument> arguments;

    // This will show up in the output assembly - generator can put reasons
    // here detailing what it expects about a certain line.
    private String comment;


    public Instruction(Opcode opcode) {
        this.opcode = opcode;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public void addArgument(Argument arg) {
        arguments.add(arg);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String print() {
        String build = opcode.getMnemonic();

        for (Argument a : arguments) {
            build += " " + a.print();
        }

        build += " #" + getComment();
        return build;
    }
}
