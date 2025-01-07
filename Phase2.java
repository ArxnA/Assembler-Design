import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Phase 2 {
    static String opcode;
    static String modRegRM;
    static String line;
    static String instruction;
    static String outputLine;
    static int lineNumber = 0;
    static String offset = "0";

    public static void main(String[] args) {
        try {
            Scanner pathInput = new Scanner(System.in);
            System.out.println("how do you want to enter the input?\npress f to enter the file path\npress any other key to enter the input from terminal");
            String choose=pathInput.nextLine();
            String inputPath;
            String outputPath;
            File file=null;
            FileWriter fWriter=null;
            Scanner input;
            int isTerminal=0;
            if(choose.equals("f")){
                System.out.println("enter the input path!");
                inputPath = pathInput.nextLine();
                file = new File(inputPath);
                input = new Scanner(file);
            }
            else {
                input = new Scanner(System.in);
                isTerminal=1;
            }
            System.out.println("enter the output path!");
            outputPath = pathInput.nextLine();
            fWriter = new FileWriter(outputPath);
            if(isTerminal==1){
                System.out.println("enter the instruction or 0 to exit!");
            }
            while (input.hasNextLine()) {
                opcode = "";
                modRegRM = "";
                outputLine = "";
                instruction = "";
                line = input.nextLine();
                if(line.equals("0")){
                    break;
                }
                line = line.toUpperCase(Locale.ROOT);
                int i = 0;
                while (line.charAt(i) == ' ') {
                    ++i;
                }
                while (line.charAt(i) != ' ' && line.charAt(i) != ':') {
                    instruction = instruction + line.charAt(i);
                    ++i;
                }
                int start = i;
                instruction = instruction.replaceAll("\\s", "");
                if (instruction.equals("ADD")) {
                    TwoParametersInstructions(line, start, "000000", fWriter);
                } else if (instruction.equals("SUB")) {
                    TwoParametersInstructions(line, start, "001010", fWriter);
                } else if (instruction.equals("INC")) {
                    IncOrDec(start, 64, "11111110", "11000", fWriter);
                } else if (instruction.equals("DEC")) {
                    IncOrDec(start, 72, "11111110", "11001", fWriter);
                } else if (instruction.equals("AND")) {
                    TwoParametersInstructions(line, start, "001000", fWriter);
                } else if (instruction.equals("OR")) {
                    TwoParametersInstructions(line, start, "000010", fWriter);
                } else if (instruction.equals("XOR")) {
                    TwoParametersInstructions(line, start, "001100", fWriter);
                } else if (instruction.equals("PUSH")) {
                    outputLine += "0x";
                    for (int n = 0; n < 16 - (offset.length()); ++n) {
                        outputLine += "0";
                    }
                    outputLine += offset + ": ";
                    ++lineNumber;
                    String registerOrMemoryOrImm = "";
                    while (i != line.length()) {
                        registerOrMemoryOrImm += line.charAt(i);
                        ++i;
                    }
                    registerOrMemoryOrImm = registerOrMemoryOrImm.replaceAll("\\s", "");
                    if (IsMemory(registerOrMemoryOrImm)) {
                        if (registerOrMemoryOrImm.equals("[EBP]") || registerOrMemoryOrImm.equals("[ESP]")) {
                            System.out.println("not support!    (" + line + ")");
                            fWriter.write("not support!    (" + line + ")" + "\n");
                        } else if(Is16bitMemory(registerOrMemoryOrImm) || Is8bitMemory(registerOrMemoryOrImm)){
                            System.out.println("wrong!    (" + line + ")");
                            fWriter.write("wrong!    (" + line + ")" + "\n");
                        }
                        else {
                            if (Is8bit(registerOrMemoryOrImm)) {
                                System.out.println("you cant push 8bits memory!    (" + line + ")");
                                fWriter.write("you cant push 8bits memory!    (" + line + ")" + "\n");
                            } else {
                                opcode = "11111111";
                                modRegRM = "00110";
                                modRegRM += RegValue(registerOrMemoryOrImm);
                                if (Is16bit(registerOrMemoryOrImm)) {
                                    outputLine += "66 ";
                                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 3);
                                } else {
                                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                                }
                                if (Integer.toHexString(Integer.parseInt(opcode, 2)).length() % 2 != 0) {
                                    outputLine += "0";
                                }
                                outputLine += Integer.toHexString(Integer.parseInt(opcode, 2)) + " ";
                                if (Integer.toHexString(Integer.parseInt(modRegRM, 2)).length() % 2 != 0) {
                                    outputLine += "0";
                                }
                                outputLine += Integer.toHexString(Integer.parseInt(modRegRM, 2));
                                outputLine += "    " + "(" + line + ")";
                                System.out.println(outputLine);
                                fWriter.write(outputLine + "\n");
                            }
                        }
                    } else {
                        if (RegValue(registerOrMemoryOrImm) != null) {
                            if (Is8bit(registerOrMemoryOrImm)) {
                                System.out.println("you cant push 8bits register!    (" + line + ")");
                                fWriter.write("you cant push 8bits register!    (" + line + ")" + "\n");
                            } else {
                                if (Is16bit(registerOrMemoryOrImm)) {
                                    outputLine += "66 ";
                                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                                } else {
                                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 1);
                                }
                                outputLine += Integer.toHexString(80 + Integer.parseInt(RegValue(registerOrMemoryOrImm), 2));
                                outputLine += "    " + "(" + line + ")";
                                System.out.println(outputLine);
                                fWriter.write(outputLine + "\n");
                            }
                        } else {
                            int immediateInt = 0;
                            String immediate = "";
                            if (registerOrMemoryOrImm.charAt(registerOrMemoryOrImm.length() - 1) == 'H') {
                                immediate = registerOrMemoryOrImm.substring(0, registerOrMemoryOrImm.length() - 1);
                                immediateInt = Integer.parseInt(immediate, 16);
                            } else if (registerOrMemoryOrImm.charAt(registerOrMemoryOrImm.length() - 1) == 'B') {
                                immediate = registerOrMemoryOrImm.substring(0, registerOrMemoryOrImm.length() - 1);
                                immediateInt = Integer.parseInt(immediate, 2);
                            } else {
                                immediate = registerOrMemoryOrImm;
                                immediateInt = Integer.parseInt(immediate);

                            }
                            String finalHex = "";
                            if (immediateInt <= 127 && immediateInt >= -128) {
                                outputLine += "6a ";
                                if (immediateInt < 0) {
                                    if ((Integer.toHexString(immediateInt).charAt(Integer.toHexString(immediateInt).length() - 2) + "" + Integer.toHexString(immediateInt).charAt(Integer.toHexString(immediateInt).length() - 1)).length() % 2 != 0) {
                                        outputLine += "0";
                                    }
                                    outputLine += Integer.toHexString(immediateInt).charAt(Integer.toHexString(immediateInt).length() - 2) + "" + Integer.toHexString(immediateInt).charAt(Integer.toHexString(immediateInt).length() - 1);
                                } else {
                                    if (Integer.toHexString(immediateInt).length() % 2 != 0) {
                                        outputLine += "0";
                                    }
                                    outputLine += Integer.toHexString(immediateInt);
                                }
                                offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                            } else {
                                outputLine += "68 ";
                                finalHex += Integer.toHexString(immediateInt);
                                for (int k = 0; k < finalHex.length() % 8; ++k) {
                                    finalHex = "0" + finalHex;
                                }
                                for (int j = finalHex.length() - 1; j > 0; j = j - 2) {
                                    outputLine += finalHex.charAt(j - 1) + "" + finalHex.charAt(j) + " ";
                                }
                                offset = Integer.toHexString(Integer.parseInt(offset, 16) + 5);
                            }
                            outputLine += "    " + "(" + line + ")";
                            System.out.println(outputLine);
                            fWriter.write(outputLine + "\n");
                        }
                    }
                } else if (instruction.equals("POP")) {
                    outputLine += "0x";
                    for (int n = 0; n < 16 - (offset.length()); ++n) {
                        outputLine += "0";
                    }
                    outputLine += offset + ": ";
                    ++lineNumber;
                    String registerOrMemory = "";
                    while (i != line.length()) {
                        registerOrMemory += line.charAt(i);
                        ++i;
                    }
                    registerOrMemory = registerOrMemory.replaceAll("\\s", "");
                    if (IsMemory(registerOrMemory)) {
                        System.out.println("error!    (" + line + ")");
                        fWriter.write("error!    (" + line + ")" + "\n");
                    } else {
                        if (Is8bit(registerOrMemory)) {
                            System.out.println("you cant pop to 8bit register!    (" + line + ")");
                            fWriter.write("you cant pop to 8bit register!    (" + line + ")" + "\n");
                        } else {
                            if (Is16bit(registerOrMemory)) {
                                outputLine += "66 ";
                                offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                            } else {
                                offset = Integer.toHexString(Integer.parseInt(offset, 16) + 1);
                            }
                            outputLine += Integer.toHexString(88 + Integer.parseInt(RegValue(registerOrMemory), 2));
                            outputLine += "    " + "(" + line + ")";
                            System.out.println(outputLine);
                            fWriter.write(outputLine + "\n");
                        }
                    }
                } else if (instruction.equals("JMP")) {
                    outputLine += "0x";
                    for (int n = 0; n < 16 - (offset.length()); ++n) {
                        outputLine += "0";
                    }
                    outputLine += offset + ": ";
                    String label = "";
                    while (i != line.length()) {
                        label += line.charAt(i);
                        ++i;
                    }
                    label = label.replaceAll("\\s", "");
                    label = label.toUpperCase(Locale.ROOT);
                    int labelOffset = 0;
                    String labelOrIns = "";
                    String thisLine = "";
                    Scanner exploreForLabels=new Scanner(file);
                    while (exploreForLabels.hasNextLine()) {
                        labelOrIns = "";
                        thisLine = exploreForLabels.nextLine();
                        thisLine = thisLine.toUpperCase(Locale.ROOT);
                        int m = 0;
                        while (thisLine.charAt(m) == ' ') {
                            ++m;
                        }
                        while (thisLine.charAt(m) != ' ' && thisLine.charAt(m) != ':') {
                            labelOrIns = labelOrIns + thisLine.charAt(m);
                            ++m;
                        }
                        labelOrIns = labelOrIns.replaceAll("\\s", "");
                        if (labelOrIns.equals("ADD") || labelOrIns.equals("SUB") || labelOrIns.equals("AND") || labelOrIns.equals("OR") || labelOrIns.equals("XOR")) {
                            String destination = "";
                            String source = "";
                            while (thisLine.charAt(m) != ',') {
                                destination = destination + thisLine.charAt(m);
                                ++m;
                            }
                            ++m;
                            destination = destination.replaceAll("\\s", "");
                            while (m != thisLine.length()) {
                                source = source + thisLine.charAt(m);
                                ++m;
                            }
                            source = source.replaceAll("\\s", "");
                            if (Is16bit(destination) || Is16bit(source)) {
                                labelOffset += 3;
                            } else {
                                labelOffset += 2;
                            }
                        } else if (labelOrIns.equals("INC") || labelOrIns.equals("DEC")) {
                            String registerValue = "";
                            while (m != thisLine.length()) {
                                registerValue += thisLine.charAt(m);
                                ++m;
                            }
                            registerValue = registerValue.replaceAll("\\s", "");
                            if (Is16bit(registerValue) || Is8bit(registerValue)) {
                                labelOffset += 2;
                            } else {
                                labelOffset += 1;
                            }
                        } else if (labelOrIns.equals("PUSH")) {
                            String registerOrMemoryOrImm = "";
                            while (m != thisLine.length()) {
                                registerOrMemoryOrImm += thisLine.charAt(m);
                                ++m;
                            }
                            registerOrMemoryOrImm = registerOrMemoryOrImm.replaceAll("\\s", "");
                            if (IsMemory(registerOrMemoryOrImm)) {
                                labelOffset += 2;
                            } else if (RegValue(registerOrMemoryOrImm) != null) {
                                labelOffset += 1;
                                if (Is16bit(registerOrMemoryOrImm)) {
                                    labelOffset += 1;
                                }
                            } else {
                                int immediateInt = 0;
                                String immediate = "";
                                if (registerOrMemoryOrImm.charAt(registerOrMemoryOrImm.length() - 1) == 'H') {
                                    immediate = registerOrMemoryOrImm.substring(0, registerOrMemoryOrImm.length() - 1);
                                    immediateInt = Integer.parseInt(immediate, 16);
                                } else if (registerOrMemoryOrImm.charAt(registerOrMemoryOrImm.length() - 1) == 'B') {
                                    immediate = registerOrMemoryOrImm.substring(0, registerOrMemoryOrImm.length() - 1);
                                    immediateInt = Integer.parseInt(immediate, 2);
                                } else {
                                    immediate = registerOrMemoryOrImm;
                                    immediateInt = Integer.parseInt(immediate);
                                }
                                if (immediateInt >= -128 && immediateInt <= 127) {
                                    labelOffset += 2;
                                } else {
                                    labelOffset += 5;
                                }
                            }
                        } else if (labelOrIns.equals("POP")) {
                            String registerInPop = "";
                            while (m != thisLine.length()) {
                                registerInPop += thisLine.charAt(m);
                                ++m;
                            }
                            registerInPop = registerInPop.replaceAll("\\s", "");
                            if (Is16bit(registerInPop)) {
                                labelOffset += 1;
                            }
                            labelOffset += 1;
                        } else if (labelOrIns.equals("JMP")) {
                            labelOffset += 2;
                        } else {
                            if (label.equals(labelOrIns)) {
                                break;
                            }
                        }
                    }
                    outputLine += "eb ";
                    if (labelOffset > Integer.parseInt(offset, 16)) {
                        if (Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2).length() % 2 != 0) {
                            outputLine += "0";
                        }
                        outputLine += Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2);
                    } else {
                        outputLine += Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2).charAt(Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2).length() - 2) + "" + Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2).charAt(Integer.toHexString(labelOffset - Integer.parseInt(offset, 16) - 2).length() - 1);
                    }
                    outputLine += "    " + "(" + line + ")";
                    System.out.println(outputLine);
                    fWriter.write(outputLine + "\n");
                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                } else {
                    outputLine += "0x";
                    for (int n = 0; n < 16 - (offset.length()); ++n) {
                        outputLine += "0";
                    }
                    outputLine += offset + ": ";
                    ++lineNumber;
                    System.out.println(outputLine + "nothing!" + "    " + "(" + line + ")");
                    fWriter.write(outputLine + "nothing    (" + line + ")\n");
                }
            }
            fWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("an error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean IsMemory(String s) {
        return s.contains("[");
    }

    static boolean Is8bit(String s) {
        return s.equals("AL") || s.equals("AH") || s.equals("BL") || s.equals("BH") || s.equals("CL") || s.equals("CH") || s.equals("DL") || s.equals("DH");
    }

    static boolean Is16bit(String s) {
        return s.equals("AX") || s.equals("BX") || s.equals("CX") || s.equals("DX") || s.equals("SP") || s.equals("BP") || s.equals("SI") || s.equals("DI");
    }

    static boolean Is16bitMemory(String s) {
        return s.equals("[AX]") || s.equals("[BX]") || s.equals("[CX]") || s.equals("[DX]") || s.equals("[SP]") || s.equals("[BP]") || s.equals("[SI]") || s.equals("[DI]");
    }

    static boolean Is8bitMemory(String s) {
        return s.equals("[AL]") || s.equals("[AH]") || s.equals("[BL]") || s.equals("[BH]") || s.equals("[CL]") || s.equals("[CH]") || s.equals("[DL]") || s.equals("[DH]");
    }


    static String RegValue(String reg) {
        if (reg.contains("EAX") || reg.contains("AX") || reg.contains("AL")) {
            return "000";
        } else if (reg.contains("ECX") || reg.contains("CX") || reg.contains("CL")) {
            return "001";
        } else if (reg.contains("EDX") || reg.contains("DX") || reg.contains("DL")) {
            return "010";
        } else if (reg.contains("EBX") || reg.contains("BX") || reg.contains("BL")) {
            return "011";
        } else if (reg.contains("ESP") || reg.contains("SP") || reg.contains("AH")) {
            return "100";
        } else if (reg.contains("EBP") || reg.contains("BP") || reg.contains("CH")) {
            return "101";
        } else if (reg.contains("ESI") || reg.contains("SI") || reg.contains("DH")) {
            return "110";
        } else if (reg.contains("EDI") || reg.contains("DI") || reg.contains("BH")) {
            return "111";
        }
        return null;
    }

    static void TwoParametersInstructions(String line, int i, String insOpcode, FileWriter fWriter) throws
            IOException {
        ++lineNumber;
        String destination = "";
        String source = "";
        while (line.charAt(i) != ',') {
            destination = destination + line.charAt(i);
            ++i;
        }
        ++i;
        destination = destination.replaceAll("\\s", "");
        while (i != line.length()) {
            source = source + line.charAt(i);
            ++i;
        }
        source = source.replaceAll("\\s", "");
        opcode += insOpcode;
        outputLine += "0x";
        for (int n = 0; n < 16 - (offset.length()); ++n) {
            outputLine += "0";
        }
        outputLine += offset + ": ";
        if (IsMemory(destination) || IsMemory(source)) {
            if (Is16bitMemory(destination) || Is16bitMemory(source) || Is8bitMemory(destination) || Is8bitMemory(source)) {
                System.out.println("error!    (" + line + ")");
                fWriter.write("error!    (" + line + ")\n");
            } else {
                modRegRM += "00";
                if (IsMemory(source)) {
                    if (source.equals("[EBP]") || source.equals("[ESP]")) {
                        System.out.println("not support!    (" + line + ")");
                        fWriter.write("not support!    (" + line + ")" + "\n");
                    } else {
                        opcode += "1";
                        modRegRM += RegValue(destination);
                        modRegRM += RegValue(source);
                        if (Is8bit(destination)) {
                            opcode += "0";
                        } else {
                            opcode += "1";
                        }
                        if (Is16bit(destination)) {
                            outputLine += "66 ";

                            offset = Integer.toHexString(Integer.parseInt(offset, 16) + 3);
                        } else {
                            offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                        }
                        if (Integer.toHexString(Integer.parseInt(opcode, 2)).length() % 2 != 0) {
                            outputLine += "0";
                        }
                        outputLine += Integer.toHexString(Integer.parseInt(opcode, 2)) + " ";
                        if (Integer.toHexString(Integer.parseInt(modRegRM, 2)).length() % 2 != 0) {
                            outputLine += "0";
                        }
                        outputLine += Integer.toHexString(Integer.parseInt(modRegRM, 2));
                        outputLine += "    " + "(" + line + ")";
                        System.out.println(outputLine);
                        fWriter.write(outputLine + "\n");
                    }
                } else {
                    if (destination.equals("[EBP]") || destination.equals("[ESP]")) {
                        System.out.println("not support!    (" + line + ")");
                        fWriter.write("not support!    (" + line + ")" + "\n");
                    } else {
                        opcode += "0";
                        modRegRM += RegValue(source);
                        modRegRM += RegValue(destination);
                        if (Is8bit(source)) {
                            opcode += "0";
                        } else {
                            opcode += "1";
                        }
                        if (Is16bit(source)) {
                            outputLine += "66 ";
                            offset = Integer.toHexString(Integer.parseInt(offset, 16) + 3);
                        } else {
                            offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                        }
                        if (Integer.toHexString(Integer.parseInt(opcode, 2)).length() % 2 != 0) {
                            outputLine += "0";
                        }
                        outputLine += Integer.toHexString(Integer.parseInt(opcode, 2)) + " ";
                        if (Integer.toHexString(Integer.parseInt(modRegRM, 2)).length() % 2 != 0) {
                            outputLine += "0";
                        }
                        outputLine += Integer.toHexString(Integer.parseInt(modRegRM, 2));
                        outputLine += "    " + "(" + line + ")";
                        System.out.println(outputLine);
                        fWriter.write(outputLine + "\n");
                    }
                }
            }
        } else {
            opcode += "0";
            modRegRM += "11";
            modRegRM += RegValue(source);
            modRegRM += RegValue(destination);
            boolean flagD = Is8bit(destination);
            boolean flagS = Is8bit(source);
            boolean flagD1 = Is16bit(destination);
            boolean flagS1 = Is16bit(source);
            if (flagD != flagS || flagD1 != flagS1) {
                System.out.println("size error!    (" + line + ")");
                fWriter.write("size error!    (" + line + ")" + "\n");
            } else {
                if (flagD) {
                    opcode += "0";
                } else {
                    opcode += "1";
                }
                if (flagD1) {
                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 3);
                    outputLine += "66 ";
                } else {
                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                }
                if (Integer.toHexString(Integer.parseInt(opcode, 2)).length() % 2 != 0) {
                    outputLine += "0";
                }
                outputLine += Integer.toHexString(Integer.parseInt(opcode, 2)) + " ";
                if (Integer.toHexString(Integer.parseInt(modRegRM, 2)).length() % 2 != 0) {
                    outputLine += "0";
                }
                outputLine += Integer.toHexString(Integer.parseInt(modRegRM, 2));
                outputLine += "    " + "(" + line + ")";
                System.out.println(outputLine);
                fWriter.write(outputLine + "\n");
            }
        }
    }

    static void IncOrDec(int i, int opcodeInt, String instructionOpcode, String modRegRm, FileWriter fWriter) throws
            IOException {
        outputLine += "0x";
        for (int n = 0; n < 16 - (offset.length()); ++n) {
            outputLine += "0";
        }
        outputLine += offset + ": ";
        ++lineNumber;
        String registerValue = "";
        while (i != line.length()) {
            registerValue += line.charAt(i);
            ++i;
        }
        registerValue = registerValue.replaceAll("\\s", "");
        if (IsMemory(registerValue)) {
            System.out.println("error!    (" + line + ")");
            fWriter.write("error!    (" + line + ")" + "\n");
        } else {
            if (Is8bit(registerValue)) {
                opcode = instructionOpcode;
                modRegRM = modRegRm;
                modRegRM += RegValue(registerValue);
                if (Integer.toHexString(Integer.parseInt(opcode, 2)).length() % 2 != 0) {
                    outputLine += "0";
                }
                outputLine += Integer.toHexString(Integer.parseInt(opcode, 2)) + " ";
                if (Integer.toHexString(Integer.parseInt(modRegRM, 2)).length() % 2 != 0) {
                    outputLine += "0";
                }
                outputLine += Integer.toHexString(Integer.parseInt(modRegRM, 2));
                outputLine += "    " + "(" + line + ")";
                offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
            } else {
                if (Integer.toHexString(opcodeInt + Integer.parseInt(RegValue(registerValue), 2)).length() % 2 != 0) {
                    opcode += "0";
                }
                opcode += Integer.toHexString(opcodeInt + Integer.parseInt(RegValue(registerValue), 2));
                if (Is16bit(registerValue)) {
                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 2);
                    outputLine += "66 ";
                    outputLine += opcode + "    " + "(" + line + ")";
                } else {
                    offset = Integer.toHexString(Integer.parseInt(offset, 16) + 1);
                    outputLine += opcode + "    " + "(" + line + ")";
                }
            }
            System.out.println(outputLine);
            fWriter.write(outputLine + "\n");
        }
    }
}