package io.kersten.thefuzz;

/**
 * Once an opcode has been generated with valid arguments and things, it becomes
 * an instruction.
 */
public class Instruction {



    // This will show up in the output assembly - generator can put reasons
    // here detailing what it expects about a certain line.
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
