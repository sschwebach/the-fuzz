package io.kersten.thefuzz;

public class Argument {

    // This is the type of the argument.
    private ArgumentType type;

    // Only one of these will be set, and when we return a value for the string
    // rendering, we'll insert based on one of these.
    short value_immedaite;  // Really max length is a byte but don't want to
                            // auto sign-extend.
    Condition value_condition;
    Register value_register;
    String value_label;


    public Argument(Program p, ArgumentType type, String labelName,
                    Condition condition) {
        //Randomly select a value for this argument...
        this.type = type;
    }

    public String print() {
        switch(type) {
            case REGISTER:
            case VREGISTER:
                return value_register.name();
            case IMMEDIATE4:
            case IMMEDIATE8:
                return "" + value_immedaite;
            case LABEL:
                return value_label;
            case CONDITION:
                return value_condition.toString();
            default:
                return "INVALID ARGUMENT!";
        }
    }

    public ArgumentType getType() {
        return type;
    }
}

/**
 * Some arguments can be generated randomly.
 */
class RandomArgument extends Argument {

    public RandomArgument(Program p, ArgumentType type, String labelName, Condition condition) {
        super(p, type, labelName, condition);
        switch (type) {
            case REGISTER:
                value_register = p.getRandomRegister(false);
                break;
            case VREGISTER:
                value_register = p.getRandomRegister(true);
                break;
            case IMMEDIATE4:
                value_immedaite = (short)(Math.random() * (2 << 4));
                break;
            case IMMEDIATE8:
                value_immedaite = (short)(Math.random() * (2 << 8));
                break;
            case LABEL:
                value_label = labelName;
                break;
            case CONDITION:
                value_condition = condition;
                break;
            default:
                throw new RuntimeException("Unknown argument type.");
        }
    }
}