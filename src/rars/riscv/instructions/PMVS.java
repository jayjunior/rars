package rars.riscv.instructions;

import rars.Globals;
import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;
import rars.riscv.hardware.FloatingPointRegisterFile;
import rars.riscv.hardware.PositRegisterFile;

public class PMVS extends BasicInstruction {

    public PMVS() {
        super("pmv.s pt1 ft1 ", "Posit Move: move the value of f1 to pt1",
                BasicInstructionFormat.I_FORMAT,"0000000 00000 sssss 000 fffff 1101011");
    }

    @Override
    public void simulate(ProgramStatement statement) throws SimulationException {
        int[] operands = statement.getOperands();
        float value = Float.intBitsToFloat(FloatingPointRegisterFile.getValue(operands[1]));
        if(operands[0] == 32){
            if(value != 0) throw new SimulationException("quire accumulator can only be set to zero",SimulationException.ILLEGAL_INSTRUCTION);
            Globals.quireRoundedValue = "0";
            Globals.quireBitPattern = "";
        }
        PositRegisterFile.setRegisterToFloat(operands[0],value);
    }
}
