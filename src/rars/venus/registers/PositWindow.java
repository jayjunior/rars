package rars.venus.registers;

import rars.riscv.hardware.PositRegisterFile;
import rars.riscv.hardware.Register;
import rars.venus.NumberDisplayBaseChooser;

public class PositWindow extends RegisterBlockWindow {
    /*
     * The tips to show when hovering over the names of the registers
     */
    private static final String[] regToolTips = {
            /* pt0  */  "posit point temporary",
            /* pt1  */  "posit point temporary",
            /* pt2  */  "posit point temporary",
            /* pt3  */  "posit point temporary",
            /* pt4  */  "posit point temporary",
            /* pt5  */  "posit point temporary",
            /* pt6  */  "posit point temporary",
            /* pt7  */  "posit point temporary",
            /* pa0  */  "saved temporary (preserved across call)",
            /* pa1  */  "saved temporary (preserved across call)",
            /* pa0  */  "posit point argument / return value",
            /* pa1  */  "posit point argument / return value",
            /* pa2  */  "posit point argument",
            /* pa3  */  "posit point argument",
            /* pa4  */  "posit point argument",
            /* pa5  */  "posit point argument",
            /* pa6  */  "posit point argument",
            /* pa7  */  "posit point argument",
            /* pa2  */  "saved temporary (preserved across call)",
            /* pa3  */  "saved temporary (preserved across call)",
            /* pa4  */  "saved temporary (preserved across call)",
            /* pa5  */  "saved temporary (preserved across call)",
            /* pa6  */  "saved temporary (preserved across call)",
            /* pa7  */  "saved temporary (preserved across call)",
            /* pa8  */  "saved temporary (preserved across call)",
            /* pa9  */  "saved temporary (preserved across call)",
            /* pa10 */  "saved temporary (preserved across call)",
            /* pa11 */  "saved temporary (preserved across call)",
            /* pt8  */  "posit point temporary",
            /* pt9  */  "posit point temporary",
            /* pt10 */  "posit point temporary",
            /* ptq */  "quire register"
    };

    public PositWindow() {
        super(PositRegisterFile.getRegisters(), regToolTips, "32-bit single precision posit point");
    }

    protected String formatRegister(Register value, int base) {
        long val = value.getValue();
        return NumberDisplayBaseChooser.formatPositNumber((int) val, base);
    }

    protected void beginObserving() {
        PositRegisterFile.addRegistersObserver(this);
    }

    protected void endObserving() {
        PositRegisterFile.deleteRegistersObserver(this);
    }

    protected void resetRegisters() {
        PositRegisterFile.resetRegisters();
    }
}
