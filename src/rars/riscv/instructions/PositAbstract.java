package rars.riscv.instructions;

import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;
import rars.riscv.hardware.PositRegisterFile;


/**
 * Base class for Posit to Posit operations
 *
 * @author Benjamin Landers
 * @version June 2017
 */
public abstract class PositAbstract extends BasicInstruction {
    protected PositAbstract(String name, String description, String funct) {
        super(name + " pt1, pt2, pt3", description, BasicInstructionFormat.R_FORMAT, funct + "ttttt sssss qqq fffff 0001011");
    }


    public void simulate(ProgramStatement statement) throws SimulationException{
        int[] operands = statement.getOperands();
        float result = compute(PositRegisterFile.getFloatFromRegister(operands[1]),
                PositRegisterFile.getFloatFromRegister(operands[2]));
        PositRegisterFile.setRegisterToFloat(operands[0],result);
    }


    public abstract float compute(Float f1 , Float f2) throws SimulationException;

}