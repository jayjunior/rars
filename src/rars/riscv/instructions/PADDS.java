package rars.riscv.instructions;

import rars.Globals;

public class PADDS extends PositAbstract {

    public PADDS() {
        super("padd.s", "Posit ADD: assigns pt1 to pt2 + pt3", "0000000");
    }

    @Override
    public float compute(Float f1 , Float f2) {
        return Globals.positInstance.add(f1,f2);
    }
}
