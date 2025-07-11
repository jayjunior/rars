package rars.riscv.instructions;

import rars.Globals;
import rars.ProgramStatement;
import rars.SimulationException;
import rars.riscv.hardware.ControlAndStatusRegisterFile;
import rars.riscv.hardware.RegisterFile;
import rars.riscv.BasicInstruction;
import rars.riscv.BasicInstructionFormat;

/*
Copyright (c) 2017,  Benjamin Landers

Developed by Benjamin Landers (benjaminrlanders@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */
public class CSRRWI extends BasicInstruction {
    public CSRRWI() {
        super("csrrwi t0, fcsr, 10", "Atomic Read/Write CSR Immediate: read from the CSR into t0 and write a constant into the CSR",
                BasicInstructionFormat.I_FORMAT, "ssssssssssss ttttt 101 fffff 1110011");
    }

    public void simulate(ProgramStatement statement) throws SimulationException {
        int[] operands = statement.getOperands();
        try {
            long csr = ControlAndStatusRegisterFile.getValueLong(operands[1]);
            if(operands[1] == 80 && (operands[2] < 0 || operands[2] > 5)){
                throw new SimulationException(statement,"exponent size for pcsr can neither be negative nor greater than 5",SimulationException.ILLEGAL_INSTRUCTION);
            }
            if(ControlAndStatusRegisterFile.updateRegister(operands[1], operands[2])){
                throw new SimulationException(statement, "Attempt to write to read-only CSR", SimulationException.ILLEGAL_INSTRUCTION);
            }
            RegisterFile.updateRegister(operands[0], csr);
        } catch (NullPointerException e) {
            throw new SimulationException(statement, "Attempt to access unavailable CSR", SimulationException.ILLEGAL_INSTRUCTION);
        }
        if(operands[1] == 80){
            int csr = (int) ControlAndStatusRegisterFile.getValueLong(operands[1]);
            Globals.setPositEs(csr);
        }
    }
}
