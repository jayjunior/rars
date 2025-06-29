package rars.tools;

import rars.Globals;
import rars.riscv.hardware.ControlAndStatusRegisterFile;
import rars.riscv.hardware.PositRegisterFile;
import rars.riscv.hardware.Register;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class PositRepresentation extends AbstractToolAndApplication implements Tool{

    private static final String title = "Posit Representation";
    private static final String heading = "32-bit Posit Representation";
    public static final String NAR = "NaR";
    public static final String INVALID_BINARY_FOR_POSIT = "Invalid binary for Posit";
    public static final String ZERO_EQUAL = "0 =";
    private Register attachedRegister = null;
    private Register[] positRegisters = null;
    private JTextField hexDisplay ,decimalDisplay ,
        binarySignDisplay,binaryRegimeDisplay, binaryExponentDisplay, binaryFractionDisplay = null;
    private JPanel binarySignDecoratedDisplay , binaryRegimeDecoratedDisplay,
            binaryExponentDecoratedDisplay, binaryFractionDecoratedDisplay;
    private static final String defaultBinarySign = "0";
    private static final int defaultBinarySignLength = defaultBinarySign.length();
    private static final String defaultBinaryExponent = "0".repeat(Globals.positInstance.getEs());
    private static final String defaultBinaryRegime = "0".repeat((int) Math.floor((double) (Globals.positInstance.getNbits()
                                        - (defaultBinaryExponent.length() + defaultBinarySign.length())) / 2));;
    private static final int defaultBinaryRegimeLength = defaultBinaryRegime.length();
    private static final String defaultBinaryFraction = "0".repeat((int) Math.ceil((double) (Globals.positInstance.getNbits()
                                                        - (defaultBinaryExponent.length() + defaultBinarySign.length())) / 2));;
    private static final int defaultBinaryFractionLength = defaultBinaryFraction.length();
    private static final String defaultHex = "00000000";
    private static final int maxLengthHex = 8;
    private static final Font hexDisplayFont = new Font("Courier", Font.PLAIN, 32);
    private static final Color hexDisplayColor = Color.red;
    private static final Font binaryDisplayFont = new Font("Courier", Font.PLAIN, 18);
    private static final Color binaryDisplayColor = Color.black;
    private final String defaultInstructions = "Modify any value then press the Enter key to update all values.";
    private static final Font instructionsFont = new Font("Arial", Font.PLAIN, 14);
    InstructionsPane instructionsPane = null;
    private static final String defaultDecimal = "0.0";
    private static final int maxLengthDecimal = 20;
    private static final Font decimalDisplayFont = new Font("Courier", Font.PLAIN, 18);
    private static final Color decimalDisplayColor = Color.blue;
    private static final String expansionFontTag = "<font size=\"+1\" face=\"Courier\" color=\"#000000\">";
    JPanel leftPanel = new JPanel(new GridLayout(5, 1, 0, 0));
    JPanel rightPanel = new JPanel(new GridLayout(5, 1, 0, 0));
    JLabel expansionDisplay , quirePane ;
    String fontTag = "<font size=\\\"+1\\\" face=\\\"Courier\\\" color=\\\"#000000\\\">";
    String htmlOpeningHeader = "<html><head>";
    String htmlClosingHeader = "</head></body>";


    protected PositRepresentation(String title, String heading) {
        super(title, heading);
    }

    public PositRepresentation() {
        this(title, heading);
    }

    @Override
    public String getName() {
        return "Posit Representation";
    }


    private String buildFormula(String sign , String regime , String exponent , String fraction){
        int useed = (int) Math.pow(2,Math.pow(2, (Globals.positInstance.getEs())));
        int regimeAsInt = getRegime(regime);
        int exponentAsInt = exponent.isEmpty() ? -1 : Integer.parseInt(exponent,2);
        int fractionAsInt = fraction.isEmpty() ? - 1 : Integer.parseInt(fraction,2);
        if(regimeAsInt == 0 && exponentAsInt <= 0 && fractionAsInt == 0){
            return htmlOpeningHeader + fontTag
                    + "0"
                    + " ="
                    + htmlClosingHeader;
        }
        return  htmlOpeningHeader + fontTag
                + (sign.equalsIgnoreCase("0") ? "+ " : "- ")
                + useed + "<sup>" + regimeAsInt + "</sup>" + "&nbsp; *&nbsp;"
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + (exponentAsInt >= 0 ?  (" 2" + "<sup>" + exponentAsInt + "</sup>" + "&nbsp; *&nbsp;") : "")
                + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + (fractionAsInt >= 0 ? (" (1 + " + fractionAsInt + "/" + (int) Math.pow(2,fraction.length()) + ")") :"")
                + " ="
                + "</font>"+htmlClosingHeader;

    }

    private int getRegime(String regime){
        int counter = 0;
        if(regime.charAt(0) == '0'){
            while(counter < regime.length() && regime.charAt(counter) == '0'){
                counter++;
            }
            return counter == regime.length() ? 0 : -counter;
        }
        while(counter < regime.length() && regime.charAt(counter) == '1'){
            counter++;
        }
        return counter - 1;
    }

    @Override
    protected JComponent buildMainDisplayArea() {
        // root containers
        Box mainPanel = Box.createVerticalBox();
        Box subMainPanel = Box.createHorizontalBox();
        subMainPanel.add(leftPanel);
        subMainPanel.add(rightPanel);
        mainPanel.add(subMainPanel);

        hexDisplay = new JTextField(defaultHex, maxLengthHex + 1);
        hexDisplay.setFont(hexDisplayFont);
        hexDisplay.setForeground(hexDisplayColor);
        hexDisplay.setHorizontalAlignment(JTextField.RIGHT);
        hexDisplay.setToolTipText("" + maxLengthHex + "-digit hexadecimal (base 16) display");
        hexDisplay.setEditable(true);
        hexDisplay.revalidate();
        hexDisplay.addKeyListener(new HexDisplayKeystrokeListener(maxLengthHex));
        JPanel hexPanel = new JPanel();
        hexPanel.add(hexDisplay);
        leftPanel.add(hexPanel);

        JPanel binaryPanel = addBinaryDisplays();


        HexToBinaryGraphicPanel hexToBinaryGraphicPanel = new HexToBinaryGraphicPanel();

        leftPanel.add(hexToBinaryGraphicPanel);
        leftPanel.add(binaryPanel);
        BinaryToDecimalFormulaGraphic binaryToDecimalFormulaGraphic = new BinaryToDecimalFormulaGraphic();
        leftPanel.add(binaryToDecimalFormulaGraphic);

        String formula = buildFormula(defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction);
        expansionDisplay = new JLabel(formula,JLabel.CENTER);
        expansionDisplay.setFont(new Font("Monospaced", Font.PLAIN, 20));
        expansionDisplay.setFocusable(false);
        expansionDisplay.setBackground(leftPanel.getBackground());
        leftPanel.add(expansionDisplay);

        /// ////////////////// DECIMAL DISPLAY BOX //////////////////////
        decimalDisplay = new JTextField(defaultDecimal, maxLengthDecimal + 1);
        decimalDisplay.setFont(decimalDisplayFont);
        decimalDisplay.setForeground(decimalDisplayColor);
        decimalDisplay.setHorizontalAlignment(JTextField.RIGHT);
        decimalDisplay.setToolTipText("Decimal floating point value");
        decimalDisplay.setMargin(new Insets(0, 0, 0, 0));
        decimalDisplay.setEditable(true);
        decimalDisplay.revalidate();
        decimalDisplay.addKeyListener(new DecimalDisplayKeystrokeListener());
        Box decimalDisplayBox = Box.createVerticalBox();
        decimalDisplayBox.add(Box.createVerticalStrut(5));
        decimalDisplayBox.add(decimalDisplay);
        decimalDisplayBox.add(Box.createVerticalStrut(15));

        FlowLayout rightPanelLayout = new FlowLayout(FlowLayout.LEFT);
        JPanel place1 = new JPanel(rightPanelLayout);
        JPanel place2 = new JPanel(rightPanelLayout);
        JPanel place3 = new JPanel(rightPanelLayout);
        JPanel place4 = new JPanel(rightPanelLayout);

        ///  ////////////////////// UI EXPLANATION TEXTS /////////////////////////////////
        JEditorPane hexExplain = new JEditorPane("text/html", expansionFontTag + "&lt;&nbsp;&nbsp;Hexadecimal representation" + "</font>");
        hexExplain.setEditable(false);
        hexExplain.setFocusable(false);
        hexExplain.setForeground(Color.black);
        hexExplain.setBackground(place1.getBackground());
        JEditorPane hexToBinExplain = new JEditorPane("text/html", expansionFontTag + "&lt;&nbsp;&nbsp;Each hex digit represents 4 bits" + "</font>");
        hexToBinExplain.setEditable(false);
        hexToBinExplain.setFocusable(false);
        hexToBinExplain.setBackground(place2.getBackground());
        JEditorPane binExplain = new JEditorPane("text/html", expansionFontTag + "&lt;&nbsp;&nbsp;Binary representation" + "</font>");
        binExplain.setEditable(false);
        binExplain.setFocusable(false);
        binExplain.setBackground(place3.getBackground());
        JEditorPane binToDecExplain = new JEditorPane("text/html", expansionFontTag + "&lt;&nbsp;&nbsp;Binary-to-decimal conversion" + "</font>");
        binToDecExplain.setEditable(false);
        binToDecExplain.setFocusable(false);
        binToDecExplain.setBackground(place4.getBackground());
        place1.add(hexExplain);
        place2.add(hexToBinExplain);
        place3.add(binExplain);
        place4.add(binToDecExplain);

        rightPanel.add(place1);
        rightPanel.add(place2);
        rightPanel.add(place3);
        rightPanel.add(place4);


        rightPanel.add(decimalDisplayBox);

        ////////////////////////// QUIRE TEXT PANEL ///////////////////////////////////
        /*
        JPanel quirePanelUI = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quirePane = new JLabel("Hello My Name is Jay Junior");
        quirePanelUI.add(quirePane);
        quirePanelUI.setBorder(new TitledBorder("Quire bit pattern"));
        mainPanel.add(quirePanelUI);
        */
        JPanel instructionsPanelUI = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionsPane = new InstructionsPane(instructionsPanelUI);
        instructionsPanelUI.add(instructionsPane);
        instructionsPanelUI.setBorder(new TitledBorder("Instructions"));
        mainPanel.add(instructionsPanelUI);

        /// /////////////////// POSIT TEXT PANEL ////////////////////////////////////
        JComboBox<String> registerSelect = getRegisterSelect();
        JPanel registerPanel = new JPanel(new BorderLayout(5, 5));
        JPanel registerAndLabel = new JPanel();
        registerAndLabel.add(new JLabel("Posit Register of interest: "));
        registerAndLabel.add(registerSelect);
        registerPanel.add(registerAndLabel, BorderLayout.WEST);
        registerPanel.add(new JLabel(" "), BorderLayout.NORTH); // just for padding
        mainPanel.add(registerPanel);

        JComboBox<Integer> esSelect = getEsSelect();
        JPanel esSelectPanel = new JPanel(new BorderLayout(5, 5));
        JPanel esSelectAndLabel = new JPanel();
        esSelectAndLabel.add(new JLabel("Posit exponent size: "));
        esSelectAndLabel.add(esSelect);
        esSelectPanel.add(esSelectAndLabel, BorderLayout.WEST);
        esSelectPanel.add(new JLabel(" "), BorderLayout.NORTH);
        mainPanel.add(esSelectPanel);
        return mainPanel;
    }

    private JPanel addBinaryDisplays() {
        binarySignDisplay = new JTextField(defaultBinarySign, defaultBinarySignLength + 1);
        binarySignDisplay.setBackground(null);
        binarySignDisplay.setFont(binaryDisplayFont);
        binarySignDisplay.setForeground(Color.RED);
        binarySignDisplay.setHorizontalAlignment(JTextField.RIGHT);
        binarySignDisplay.setToolTipText("The sign bit");
        binarySignDisplay.setEditable(true);
        binarySignDisplay.revalidate();


        binaryRegimeDisplay = new JTextField(defaultBinaryRegime, defaultBinaryRegimeLength + 1);
        binaryRegimeDisplay.setBackground(null);
        binaryRegimeDisplay.setFont(binaryDisplayFont);
        binaryRegimeDisplay.setForeground(Color.magenta);
        binaryRegimeDisplay.setHorizontalAlignment(JTextField.RIGHT);
        binaryRegimeDisplay.setToolTipText("The regime bits");
        binaryRegimeDisplay.setEditable(true);
        binaryRegimeDisplay.revalidate();

        binaryExponentDisplay = new JTextField(defaultBinaryExponent, Globals.positInstance.getEs() + 1);
        binaryExponentDisplay.setBackground(null);
        binaryExponentDisplay.setFont(binaryDisplayFont);
        binaryExponentDisplay.setForeground(Color.BLUE);
        binaryExponentDisplay.setHorizontalAlignment(JTextField.RIGHT);
        binaryExponentDisplay.setToolTipText("The exponent bits");
        binaryExponentDisplay.setEditable(true);
        binaryExponentDisplay.revalidate();

        binaryFractionDisplay = new JTextField(defaultBinaryFraction, defaultBinaryFractionLength + 1);
        binaryFractionDisplay.setBackground(null);
        binaryFractionDisplay.setFont(binaryDisplayFont);
        binaryFractionDisplay.setForeground(binaryDisplayColor);
        binaryFractionDisplay.setHorizontalAlignment(JTextField.RIGHT);
        binaryFractionDisplay.setToolTipText("The fraction bits");
        binaryFractionDisplay.setEditable(true);
        binaryFractionDisplay.revalidate();

        binarySignDisplay.addKeyListener(new BinaryDisplayKeystrokeListener(defaultBinarySignLength));
        binaryRegimeDisplay.addKeyListener(new BinaryDisplayKeystrokeListener(defaultBinaryRegimeLength));
        binaryExponentDisplay.addKeyListener(new BinaryDisplayKeystrokeListener(Globals.positInstance.getEs()));
        binaryFractionDisplay.addKeyListener(new BinaryDisplayKeystrokeListener(defaultBinaryFractionLength));

        binarySignDecoratedDisplay = new JPanel(new BorderLayout());
        binaryRegimeDecoratedDisplay = new JPanel(new BorderLayout());
        binaryExponentDecoratedDisplay = new JPanel(new BorderLayout());
        binaryFractionDecoratedDisplay = new JPanel(new BorderLayout());

        binarySignDecoratedDisplay.add(binarySignDisplay, BorderLayout.CENTER);
        binarySignDecoratedDisplay.add(new JLabel("sign", JLabel.CENTER), BorderLayout.SOUTH);

        binaryExponentDecoratedDisplay.add(binaryExponentDisplay, BorderLayout.CENTER);
        binaryRegimeDecoratedDisplay.add(binaryRegimeDisplay, BorderLayout.CENTER);

        binaryRegimeDecoratedDisplay.add(new JLabel("regime", JLabel.CENTER), BorderLayout.SOUTH);
        binaryExponentDecoratedDisplay.add(new JLabel("exponent", JLabel.CENTER), BorderLayout.SOUTH);

        binaryFractionDecoratedDisplay.add(binaryFractionDisplay, BorderLayout.CENTER);
        binaryFractionDecoratedDisplay.add(new JLabel("fraction", JLabel.CENTER), BorderLayout.SOUTH);
        JPanel binaryPanel = new JPanel();
        binaryPanel.add(binarySignDecoratedDisplay);
        binaryPanel.add(binaryRegimeDecoratedDisplay);
        binaryPanel.add(binaryExponentDecoratedDisplay);
        binaryPanel.add(binaryFractionDecoratedDisplay);
        return binaryPanel;
    }

    private JComboBox<Integer> getEsSelect(){
        Integer[] exponentList = new Integer[]{0,1,2,3,4,5};
        JComboBox<Integer> esSelect = new JComboBox<>(exponentList);
        esSelect.setSelectedIndex(Globals.positInstance.getEs());
        esSelect.setToolTipText("Set Exponent size");
        esSelect.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JComboBox cb = (JComboBox) e.getSource();
                        Globals.setPositEs(cb.getSelectedIndex());
                        binaryExponentDisplay.setColumns(Globals.positInstance.getEs() + 1);
                        ControlAndStatusRegisterFile.updateRegister(80,Globals.positInstance.getEs());
                        if (Globals.getGui() != null) {
                            Globals.getGui().getRegistersPane().getControlAndStatusWindow().updateRegisters();
                        }
                        String sign = "0";
                        String exponent = "0".repeat(Globals.positInstance.getEs());
                        String regime = "0".repeat((int) Math.floor((double) (Globals.positInstance.getNbits() - (exponent.length() + sign.length())) / 2));
                        String fraction = "0".repeat((int) Math.ceil((double) (Globals.positInstance.getNbits() - (exponent.length() + sign.length())) / 2));
                        updateDisplayAndAttachedRegister(defaultHex,new String[]{sign,regime,exponent,fraction},ZERO_EQUAL,defaultDecimal,defaultInstructions);
                    }
                });
        return esSelect;
    }

    private JComboBox<String> getRegisterSelect() {
        positRegisters = PositRegisterFile.getRegisters();
        String[] registerList = new String[positRegisters.length + 1];
        registerList[0] = "None";
        for (int i = 0; i < positRegisters.length; i++) {
            registerList[i + 1] = positRegisters[i].getName();
        }
        JComboBox<String> registerSelect = new JComboBox<>(registerList);
        registerSelect.setSelectedIndex(0);  // No register attached
        registerSelect.setToolTipText("Attach to selected Posit register");
        registerSelect.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JComboBox cb = (JComboBox) e.getSource();
                        int selectedIndex = cb.getSelectedIndex();
                        if(selectedIndex == 0){
                            attachedRegister = null;
                            return;
                        }
                        attachedRegister = PositRegisterFile.getRegister(registerList[selectedIndex]);
                        if (isObserving()) {
                            deleteAsObserver();
                        }
                    }
                });

        return registerSelect;
    }

    private String getHexFromBinary(String binary){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < binary.length(); i+=4){
            int decimal = Integer.parseInt(binary.substring(i,i+4), 2);
            result.append(Integer.toHexString(decimal).toUpperCase());
        }
        return result.toString();
    }

    private String getBinaryFromHex(String hex){
        StringBuilder result = new StringBuilder();
        for(int i = 0 ; i < hex.length(); i++){
            int decimal = Integer.parseInt(hex.substring(i,i+1), 16);
            StringBuilder decimalAsString = new StringBuilder(Integer.toBinaryString(decimal));
            decimalAsString.reverse();
            decimalAsString.append("0".repeat(Math.max(0,4 - decimalAsString.length())));
            result.append(decimalAsString.reverse());
        }
        return result.toString();
    }

    private void updateDisplayAndAttachedRegister(String hexText , String[] binaryComponents , String formula , String decimal,String instuctions){
        updateDisplay(hexText,binaryComponents,formula,decimal,instuctions);
        if(decimal == null) decimal = decimalDisplay.getText();
        if(decimal.equals(NAR)) decimal = "0.0";
        updateAnyAttachedRegister(Float.parseFloat(decimal));
    }

    private void updateDisplay(String hexText , String[] binaryComponents , String formula , String decimal,String instuctions){
        if(hexText != null && !hexText.isEmpty()){
            hexDisplay.setText(hexText);
        }
        if(binaryComponents != null && !binaryComponents[0].isEmpty()){
            binarySignDisplay.setText(binaryComponents[0]);
            binaryRegimeDisplay.setColumns(binaryComponents[1].length()+1);
            binaryRegimeDisplay.setText(binaryComponents[1]);
            binaryExponentDisplay.setColumns(binaryComponents[2].length()+1);
            binaryExponentDisplay.setText(binaryComponents[2]);
            binaryFractionDisplay.setColumns(binaryComponents[3].length()+1);
            binaryFractionDisplay.setText(binaryComponents[3]);
        }
        if(formula != null && !formula.isEmpty()){
            expansionDisplay.setText(formula);
        }
        if(decimal != null && !decimal.isEmpty()){
            decimalDisplay.setText(decimal);
        }
        if(instuctions != null && !instuctions.isEmpty()){
            instructionsPane.setText(instuctions);
        }
    }

    // If display is attached to a register then update the register value.
    private synchronized void updateAnyAttachedRegister(float decimal) {
        if (attachedRegister != null && isObserving()) {
            Globals.memoryAndRegistersLock.lock();
            try {
                PositRegisterFile.setRegisterToFloat(attachedRegister.getNumber(),decimal);
            } finally {
                Globals.memoryAndRegistersLock.unlock();
            }
            // HERE'S A HACK!!  Want to immediately display the updated register value in RARS
            // but that code was not written for event-driven update (e.g. Observer) --
            // it was written to poll the registers for their values.  So we force it to do so.
            if (Globals.getGui() != null) {
                Globals.getGui().getRegistersPane().getPositWindow().updateRegisters();
            }
        }
    }

    private boolean isValidBinary(String binary){
        return binary.length() == Globals.positInstance.getNbits();
    }
    /**
     * Override the inherited method, which registers us as an Observer over the static data segment
     * (starting address 0x10010000) only.  This version will register us as observer over the selected
     * floating point register, if any. If no register is selected, it will not do anything.
     * If you use the inherited GUI buttons, this method is invoked when you click "Connect" button
     * on Tool or the "Assemble and Run" button on a Rars-based app.
     */
    protected void addAsObserver() {
        addAsObserver(attachedRegister);
    }

    protected void reset() {
        updateDisplayAndAttachedRegister(defaultHex
                ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                ,ZERO_EQUAL,defaultDecimal,defaultInstructions);
    }

    /**
     * Delete this app/tool as an Observer of the attached register.  This overrides
     * the inherited version which deletes only as an Observer of memory.
     * This method is called when the default "Disconnect" button on a Tool is selected or
     * when the program execution triggered by the default "Assemble and run" on a stand-alone
     * app terminates (e.g. when the button is re-enabled).
     */
    protected void deleteAsObserver() {
        deleteAsObserver(attachedRegister);
    }


    public static void main(String[] args) {
        new PositRepresentation(title,heading).go();
    }



    private class InstructionsPane extends JLabel {

        InstructionsPane(Component parent) {
            super(defaultInstructions);
            this.setFont(instructionsFont);
            this.setBackground(parent.getBackground());
        }

        public void setText(String text) {
            super.setText(text);
        }
    }
    private class HexDisplayKeystrokeListener extends KeyAdapter {

        private final int digitLength; // maximum number of digits long

        public HexDisplayKeystrokeListener(int length) {
            digitLength = length;
        }


        // Process user keystroke.  If not valid for the context, this
        // will consume the stroke and beep.
        public void keyTyped(KeyEvent e) {
            JTextField source = (JTextField) e.getComponent();
            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_TAB)
                return;
            if (!isHexDigit(e.getKeyChar()) ||
                    source.getText().length() == digitLength && source.getSelectedText() == null) {
                if (e.getKeyChar() != KeyEvent.VK_ENTER && e.getKeyChar() != KeyEvent.VK_TAB) {
                    Toolkit.getDefaultToolkit().beep();
                    if (source.getText().length() == digitLength && source.getSelectedText() == null) {
                        instructionsPane.setText("Maximum length of this field is " + digitLength + ".");
                    } else {
                        instructionsPane.setText("Only digits and A-F (or a-f) are accepted in hexadecimal field.");
                    }
                }
                e.consume();
            }
        }

        // Enter key is echoed on component after keyPressed but before keyTyped?
        // Consuming the VK_ENTER event in keyTyped does not suppress it but this will.
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_TAB) {
                //updateDisplaysAndRegister(new FloatRepresentation.FlavorsOfFloat().buildOneFromHexString(((JTextField) e.getSource()).getText()));
                StringBuilder hexpayLoad = new StringBuilder(hexDisplay.getText());
                if(hexpayLoad.length() < maxLengthHex) {
                    hexpayLoad.reverse();
                    hexpayLoad.append("0".repeat(Math.max(0, maxLengthHex - hexpayLoad.length())));
                    hexpayLoad.reverse();
                }

                String binaryConversion = getBinaryFromHex(hexpayLoad.toString());
                if(!isValidBinary(binaryConversion)){
                    updateDisplayAndAttachedRegister(null
                            ,null,null,null,"Rars only supports " + Globals.positInstance.getNbits() + " Posit bits numbers");
                    return;
                }
                if(!binaryConversion.contains("1")){
                    updateDisplayAndAttachedRegister(defaultHex
                            ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                            ,ZERO_EQUAL,defaultDecimal,defaultInstructions);
                    return;
                }
                String[] components = Globals.positInstance.getComponents(binaryConversion);
                if(components == null){
                    updateDisplayAndAttachedRegister(defaultHex
                                ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                                ,ZERO_EQUAL,NAR,INVALID_BINARY_FOR_POSIT);
                    return;
                }
                String exponent = components.length > 2 ? components[2] : "";
                String fraction = components.length > 3 ? components[3] : "";
                String decimal = Globals.positInstance.binaryToPosit(binaryConversion);
                if(decimal.equals(NAR)){
                    updateDisplayAndAttachedRegister(defaultHex
                                ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                                ,ZERO_EQUAL,NAR, INVALID_BINARY_FOR_POSIT);
                    return;
                }
                String formula = buildFormula(components[0],components[1],exponent,fraction);
                updateDisplayAndAttachedRegister(null
                            ,new String[]{components[0],components[1],exponent,fraction},formula,decimal,defaultInstructions);
                e.consume();
            }
        }

        // handy utility.
        private boolean isHexDigit(char digit) {
            boolean result = false;
            switch (digit) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    result = true;
            }
            return result;
        }
    }
    private class DecimalDisplayKeystrokeListener extends KeyAdapter {


        public void keyTyped(KeyEvent e) {
            JTextField source = (JTextField) e.getComponent();
            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                return;
            if (!isDecimalFloatDigit(e.getKeyChar())) {
                if (e.getKeyChar() != KeyEvent.VK_ENTER) {
                    instructionsPane.setText("Only digits, period, signs and E (or e) are accepted in decimal field.");
                    Toolkit.getDefaultToolkit().beep();
                }
                e.consume();
            }
        }

        // Enter key is echoed on component after keyPressed but before keyTyped?
        // Consuming the VK_ENTER event in keyTyped does not suppress it but this will.
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                //updateDisplaysAndRegister(fof);
                String positAsString = decimalDisplay.getText();
                if(Float.parseFloat(positAsString) == 0.0f){
                    updateDisplayAndAttachedRegister(defaultHex
                            ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                            ,ZERO_EQUAL,defaultDecimal,defaultInstructions);
                    return;
                }
                String[] components = Globals.positInstance.positToBinary(positAsString);

                if(components == null){
                    updateDisplayAndAttachedRegister(defaultHex
                            ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                            ,ZERO_EQUAL,null,INVALID_BINARY_FOR_POSIT);
                    return;
                }
                String exponent = components.length > 2 ? components[2] : "";
                String fraction = components.length > 3 ? components[3] : "";
                String hex = getHexFromBinary(String.join("",components));
                String formula = buildFormula(components[0],components[1],exponent,fraction);
                updateDisplayAndAttachedRegister(hex
                        ,new String[]{components[0],components[1],exponent,fraction},formula,null,defaultInstructions);
                e.consume();
            }
        }

        // handy utility
        private boolean isDecimalFloatDigit(char digit) {
            boolean result = false;
            switch (digit) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '-':
                case '+':
                case '.':
                case 'e':
                case 'E':
                    result = true;
            }
            return result;
        }

    }
    private class BinaryDisplayKeystrokeListener extends KeyAdapter {

        private int bitLength;  // maximum number of bits permitted

        public BinaryDisplayKeystrokeListener(int length) {
            bitLength = length;
        }

        // Process user keystroke.  If not valid for the context, this
        // will consume the stroke and beep.
        public void keyTyped(KeyEvent e) {
            JTextField source = (JTextField) e.getComponent();
            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                return;
            if (!isBinaryDigit(e.getKeyChar()) ||
                    e.getKeyChar() == KeyEvent.VK_ENTER ||
                    source.getText().length() == bitLength && source.getSelectedText() == null) {
                if (e.getKeyChar() != KeyEvent.VK_ENTER) {
                    Toolkit.getDefaultToolkit().beep();
                    if (source.getText().length() == bitLength && source.getSelectedText() == null) {
                        instructionsPane.setText("Maximum length of this field is " + bitLength + ".");
                    } else {
                        instructionsPane.setText("Only 0 and 1 are accepted in binary field.");
                    }
                }
                e.consume();
            }
        }
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                String signText = binarySignDisplay.getText();
                String regimeText = binaryRegimeDisplay.getText();
                String exponentText = binaryExponentDisplay == null ? "" :  binaryExponentDisplay.getText();
                String fractionText = binaryFractionDisplay == null ? "" : binaryFractionDisplay.getText();
                String binaryPayLoad = signText + regimeText + exponentText + fractionText;
                if(!isValidBinary(binaryPayLoad)){
                    updateDisplayAndAttachedRegister(null,null,null,null,"Rars only supports " + Globals.positInstance.getNbits() + " Posit bits numbers");
                    return;
                }
                if(exponentText.length() != Globals.positInstance.getEs()){
                    updateDisplayAndAttachedRegister(null,null,null,null,"Exponent must " +
                            "be " + Globals.positInstance.getEs() + " bits ");
                    return;
                }
                if(!binaryPayLoad.contains("1")){
                    updateDisplayAndAttachedRegister(defaultHex
                            ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                            ,ZERO_EQUAL,defaultDecimal,defaultInstructions);
                    return;
                }
                String decimal = Globals.positInstance.binaryToPosit(binaryPayLoad);
                if(decimal.equals(NAR)){
                    updateDisplayAndAttachedRegister(defaultHex
                            ,new String[]{defaultBinarySign,defaultBinaryRegime,defaultBinaryExponent,defaultBinaryFraction}
                            , ZERO_EQUAL,NAR,INVALID_BINARY_FOR_POSIT);
                    return;
                }
                String hexConversion = getHexFromBinary(binaryPayLoad);
                String formula = buildFormula(signText,regimeText,exponentText,fractionText);
                updateDisplayAndAttachedRegister(hexConversion
                        ,new String[]{signText,regimeText,exponentText,fractionText},formula,decimal,defaultInstructions);
                e.consume();
            }
        }

        private boolean isBinaryDigit(char digit) {
            boolean result = false;
            switch (digit) {
                case '0':
                case '1':
                    result = true;
            }
            return result;
        }
    }
    private class HexToBinaryGraphicPanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.red);
            int totalWidth = binaryExponentDecoratedDisplay.getWidth() + binaryRegimeDecoratedDisplay.getWidth() +
                            binarySignDecoratedDisplay.getWidth() + ( binaryExponentDecoratedDisplay == null ? 0 :
                            binaryFractionDecoratedDisplay.getWidth() ) ;
            int x0 = binarySignDecoratedDisplay.getWidth();
            int x1 = x0 + totalWidth;
            int x2 = hexDisplay.getX() + (hexDisplay.getWidth() / 2 );
            int y01 = 60;
            int y2 = 0;
            Polygon p = new Polygon();
            p.addPoint(x0, y01);
            p.addPoint(x1,y01);
            p.addPoint(x2, y2);
            g.fillPolygon(p);

        }

    }
    private class BinaryToDecimalFormulaGraphic extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
            int x2 = hexDisplay.getX() + (hexDisplay.getWidth() / 2 );
            int y01 = 0;
            int y2 = 60;
            int yAngle = 15;
            int y3 = y2 - yAngle;
            g.drawLine(x2, y01, x2, y2);
            g.drawLine(x2 - yAngle,y3,x2,y2);
            g.drawLine(x2 + yAngle,y3,x2,y2);
        }

    }
}
