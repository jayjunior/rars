package rars.riscv.instructions;

import rars.Globals;
import rars.SimulationException;

public class PDIVS extends PositAbstract {

    public PDIVS() {
        super("pdiv.s", "Posit divide: assigns pt1 to pt2 / pt3", "0000011");
    }

    @Override
    public float compute(Float f1 , Float f2) throws SimulationException {
        if(f2 == 0) throw new SimulationException("Divison by zero exception",SimulationException.ILLEGAL_INSTRUCTION);
        return Globals.positInstance.divide(f1,f2);
    }
}
