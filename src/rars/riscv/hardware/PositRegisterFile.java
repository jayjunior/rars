package rars.riscv.hardware;

import rars.Globals;

import java.util.Observer;


    /**
     * Represents the Posit Register File (FPU)
     *
     **/


    public class PositRegisterFile {
        private static final RegisterBlock instance = new RegisterBlock('p', new Register[]{
                new Register("pt0", 0, 0), new Register("pt1", 1, 0),
                new Register("pt2", 2, 0), new Register("pt3", 3, 0),
                new Register("pt4", 4, 0), new Register("pt5", 5, 0),
                new Register("pt6", 6, 0), new Register("pt7", 7, 0),
                new Register("ps0", 8, 0), new Register("ps1", 9, 0),
                new Register("pa0", 10, 0), new Register("pa1", 11, 0),
                new Register("pa2", 12, 0), new Register("pa3", 13, 0),
                new Register("pa4", 14, 0), new Register("pa5", 15, 0),
                new Register("pa6", 16, 0), new Register("pa7", 17, 0),
                new Register("ps2", 18, 0), new Register("ps3", 19, 0),
                new Register("ps4", 20, 0), new Register("ps5", 21, 0),
                new Register("ps6", 22, 0), new Register("ps7", 23, 0),
                new Register("ps8", 24, 0), new Register("ps9", 25, 0),
                new Register("ps10", 26, 0), new Register("ps11", 27, 0),
                new Register("pt8", 28, 0), new Register("pt9", 29, 0),
                new Register("pt10", 30, 0), new Register("ptq",31,0)
        });


        /**
         * Sets the value of the posit register given to the value given.
         *
         * @param reg Register to set the value of.
         * @param val The desired float value for the register.
         **/

        public static void setRegisterToFloat(int reg, float val) {
            updateRegister(reg, Globals.positInstance.convertToInt(val));
        }

    /**
     * Gets the float value stored in the given posit register.
     *
     * @param num Register to get the value of.
     * @return The  float value stored by that register.
     **/

    public static float getFloatFromRegister(int num) {
        return Globals.positInstance.convertToPosit(getValue(num));
    }

    /**
     * This method updates the posit register value who's number is num.  Note the
     * registers themselves hold an int value.
     *
     * @param num posit register to set the value of.
     * @param val The desired int value for the register.
     **/

    public static void updateRegister(int num, int val) {
        if ((Globals.getSettings().getBackSteppingEnabled())) {
            Globals.program.getBackStepper().addPositRestore(num, instance.updateRegister(num, val));
        } else {
            instance.updateRegister(num, val);
        }
    }

    public static void updateRegisterLong(int num, long val) {
        if ((Globals.getSettings().getBackSteppingEnabled())) {
            Globals.program.getBackStepper().addPositRestore(num, instance.updateRegister(num, val));
        } else {
            instance.updateRegister(num, val);
        }
    }
    /**
     * Gets the raw int value actually stored in a Register.
     *
     * @param num The posit register number.
     * @return The int value of the given register.
     **/

    public static int getValue(int num) {
        int val = (int) instance.getValue(num);
        return val;
    }

    public static long getValueLong(int num) {
        return instance.getValue(num);
    }

    /**
     * Gets the raw int value actually stored in a Register.
     *
     * @param name The posit register name.
     * @return The int value of the given register.
     **/

    public static int getValue(String name) {
        long lval = instance.getValue(name);
        if((lval & 0xFFFFFFFF_00000000L) == 0xFFFFFFFF_00000000L){
            return (int)lval;
        }else{
            return 0x7FC00000; //TODO: Nar in this case
        }
    }

    /**
     * For returning the set of registers.
     *
     * @return The set of registers.
     **/

    public static Register[] getRegisters() {
        return instance.getRegisters();
    }

    /**
     * Get register object corresponding to given name.  If no match, return null.
     *
     * @param name The posit register name.
     * @return The register object,or null if not found.
     **/

    public static Register getRegister(String name) {
        return instance.getRegister(name);
    }


    /**
     * Method to reinitialize the values of the registers.
     **/

    public static void resetRegisters() {
        instance.resetRegisters();
    }


    /**
     * Each individual register is a separate object and Observable.  This handy method
     * will add the given Observer to each one.
     */
    public static void addRegistersObserver(Observer observer) {
        instance.addRegistersObserver(observer);
    }

    /**
     * Each individual register is a separate object and Observable.  This handy method
     * will delete the given Observer from each one.
     */
    public static void deleteRegistersObserver(Observer observer) {
        instance.deleteRegistersObserver(observer);
    }
}
