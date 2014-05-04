package io.kersten.thefuzz;

/**
 * What types of value an opcode can expect for a particular argument.
 * * REGISTER: A register, R0-R14. (R15 excluded to avoid mindlessly clobbering
 * it.)
 * * VREGISTER: Same as REGISTER, but enforces that it must be a valid
 * register (i.e. not xx) because its value is being computed.
 * * IMMEDIATE4: A 4-bit immediate value.
 * * IMMEDIATE8: An 8-bit immediate value.
 * * LABEL: The name of a label.
 * * CONDITION: For branching, one of: NEQ, EQ, GT, LT, GTE, LTE, OVFL, UNCOND
 */
public enum ArgumentType {
    REGISTER, VREGISTER, IMMEDIATE4, IMMEDIATE8, LABEL, CONDITION
}
