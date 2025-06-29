package rars.riscv.instructions;

import rars.Globals;

public class PMULS extends PositAbstract {

    public PMULS() {
        super("pmul.s", "Posit multiply: assigns pt1 to pt2 * pt3", "0000010");
    }

    @Override
    public float compute(Float f1 , Float f2) {
        return Globals.positInstance.multiply(f1,f2);
    }
}
