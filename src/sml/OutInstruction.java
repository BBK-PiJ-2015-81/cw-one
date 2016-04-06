package sml;

/**
 * Created by andre on 06/04/2016.
 */
public class OutInstruction extends Instruction {

    private int op;

    public OutInstruction(String label, String op) {
        super(label, op);
    }

    public OutInstruction(String label, int op) {
        this(label, "out");
        this.op = op;
    }

    @Override
    public void execute(Machine m) {
        System.out.println();
        System.out.println("The contents of register " + op + " is " + m.getRegisters().getRegister(op));
        System.out.println();
    }

    public String toString() {
        return super.toString() + " register " + op;
    }

}
