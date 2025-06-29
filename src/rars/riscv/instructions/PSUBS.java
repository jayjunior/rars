package rars.riscv.instructions;

import rars.Globals;

public class PSUBS extends PositAbstract {

    public PSUBS() {
        super("psub.s", "Posit Subtract: assigns pt1 to pt2 - pt3", "0000001");
    }

    @Override
    public float compute(Float f1 , Float f2) {
        return Globals.positInstance.subtract(f1,f2);
    }
}
