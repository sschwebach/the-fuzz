package io.kersten.thefuzz;

import com.sun.org.apache.xpath.internal.Arg;

import java.util.ArrayList;

/**
 * Once an opcode has been generated with valid arguments and things, it becomes
 * an instruction.
 */
public class Instruction {

    private IOpcode iOpcode;

    private ArrayList<Argument> arguments = new ArrayList<Argument>();

    // This will show up in the output assembly - generator can put reasons
    // here detailing what it expects about a certain line.
    private String comment = "";


    public Instruction(IOpcode iOpcode) {
        this.iOpcode = iOpcode;
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

    public void appendComment(String ap) {
        setComment(getComment() + "; " + ap);
    }

    public String print() {
        String build = iOpcode.getMnemonic() + " ";

        int c = 0;
        for (Argument a : arguments) {
            build += a.print() + (c == arguments.size() - 1 ? "" : ", ");
        }

        build += "#" + getComment();
        return build;
    }

    public IOpcode getiOpcode() {
        return iOpcode;
    }
}
