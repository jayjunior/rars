package rars.riscv.instructions;

import rars.Globals;
import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;
import rars.riscv.hardware.AddressErrorException;
import rars.riscv.hardware.PositRegisterFile;
import rars.riscv.hardware.RegisterFile;


public class PSW extends BasicInstruction {
    public PSW() {
        super("psw pt1, -100(t1)", "Store a posit to memory",
                BasicInstructionFormat.S_FORMAT, "sssssss fffff ttttt 010 sssss 0101011");
    }

    public void simulate(ProgramStatement statement) throws SimulationException {
        int[] operands = statement.getOperands();
        operands[1] = (operands[1] << 20) >> 20;
        try {
            int posit = PositRegisterFile.getValue(operands[0]);
            float value = Globals.positInstance.convertToPosit(posit);
            Globals.memory.setWord(RegisterFile.getValue(operands[2]) + operands[1], Float.floatToIntBits(value));
        } catch (AddressErrorException e) {
            throw new SimulationException(statement, e);
        }
    }
}