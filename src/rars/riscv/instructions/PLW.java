package rars.riscv.instructions;

import rars.Globals;
import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;
import rars.riscv.hardware.AddressErrorException;
import rars.riscv.hardware.PositRegisterFile;
import rars.riscv.hardware.RegisterFile;


public class PLW extends BasicInstruction {
    public PLW() {
        super("plw pt1, -100(t1)", "Load a posit from memory",
                BasicInstructionFormat.I_FORMAT, "ssssssssssss ttttt 010 fffff 0011111");
    }

    public void simulate(ProgramStatement statement) throws SimulationException {
        int[] operands = statement.getOperands();
        operands[1] = (operands[1] << 20) >> 20;
        try {
            float value = Float.intBitsToFloat(Globals.memory.getWord(RegisterFile.getValue(operands[2]) + operands[1]));
            PositRegisterFile.setRegisterToFloat(operands[0],value);
        } catch (AddressErrorException e) {
            throw new SimulationException(statement, e);
        }
    }
}
