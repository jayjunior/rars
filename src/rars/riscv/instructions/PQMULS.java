package rars.riscv.instructions;

import rars.Globals;
import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;
import rars.riscv.hardware.PositRegisterFile;

public class PQMULS extends BasicInstruction {

    public PQMULS() {
        super("pqmul.s pt1 pt2 ", "Posit quire multiply : accumulate value of pt1 * pt2",
                BasicInstructionFormat.I_FORMAT,"0000000 00010 sssss 000 fffff 1101011");
    }

    @Override
    public void simulate(ProgramStatement statement) throws SimulationException {
        int[] operands = statement.getOperands();
        String[] res = Globals.positInstance.quireMul(Globals.quireBitPattern,PositRegisterFile.getFloatFromRegister(operands[0]),
                PositRegisterFile.getFloatFromRegister(operands[1]));
        Globals.quireBitPattern = res[0];
        Globals.quireRoundedValue = res[1];
        PositRegisterFile.setRegisterToFloat(31,Float.parseFloat(Globals.quireRoundedValue));
    }
}
