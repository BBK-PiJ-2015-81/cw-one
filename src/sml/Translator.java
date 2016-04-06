package sml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * The translator of a <b>S</b><b>M</b>al<b>L</b> program.
 */
public class Translator {

    private static final String PATH = "src/";
    // word + line is the part of the current line that's not yet processed
    // word has no whitespace
    // If word and line are not empty, line begins with whitespace
    private String line = "";
    private Labels labels; // The labels of the program being translated
    private ArrayList<Instruction> program; // The program to be created
    private String fileName; // source file of SML code

    public Translator(String fileName) {
        this.fileName = PATH + fileName;
    }

    // translate the small program in the file into lab (the labels) and
    // prog (the program)
    // return "no errors were detected"
    public boolean readAndTranslate(Labels lab, ArrayList<Instruction> prog) {

        try (Scanner sc = new Scanner(new File(fileName))) {
            // Scanner attached to the file chosen by the user
            labels = lab;
            labels.reset();
            program = prog;
            program.clear();

            try {
                line = sc.nextLine();
            } catch (NoSuchElementException ioE) {
                return false;
            }

            // Each iteration processes line and reads the next line into line
            while (line != null) {
                // Store the label in label
                String label = scan();

                if (label.length() > 0) {
                    Instruction ins = getInstruction(label);
                    if (ins != null) {
                        labels.addLabel(label);
                        program.add(ins);
                    }
                }

                try {
                    line = sc.nextLine();
                } catch (NoSuchElementException ioE) {
                    return false;
                }
            }
        } catch (IOException ioE) {
            System.out.println("File: IO error " + ioE.getMessage());
            System.exit(-1);
            return false;
        }
        return true;
    }

    // line should consist of an MML instruction, with its label already
    // removed. Translate line into an instruction with label label
    // and return the instruction
    public Instruction getInstruction(String label) {
        int s1; // Possible operands of the instruction
        int s2;
        String s3;
        int r;
        int x;

        if (line.equals(""))
            return null;

        //Get the first word as a string
        String ins = scan();

        // Generate the instruction classname that corresponds with the label
        String instructionName = ins.substring(0, 1).toUpperCase() + ins.substring(1).toLowerCase() + "Instruction" ;


        //System.out.println("The scanned label is: " + ins);
        //System.out.println("Use instruction class: " + instructionName);

        // Make a list of the remaining words
        List<String> wordList = new ArrayList<>();
        List<Class> paramTypes = new ArrayList<>();

        //The first constructor param is always the label string
        wordList.add(ins);
        paramTypes.add(String.class);

        String nextWord;

        /*
        if (!nextWord.equals("")){
            wordList.add(nextWord);
            paramTypes.add(nextWord.getClass());
        }
        */


        while (!(nextWord = scan()).equals("")){
            wordList.add(nextWord);
        }






        //System.out.println("The next word is: " + instructionName);

        Object wordArray [] = wordList.toArray();
        Class<?> paramArray [] = new Class<?> [wordArray.length];



         try {
             for (int i = 0; i < wordArray.length; i++){
                 String str = wordArray[i].toString();

                 if (Pattern.matches(("-?[0-9]+"), str)){
                     paramArray [i] = Integer.TYPE;
                     wordArray [i] =  Integer.parseInt((String) wordArray[i]);
                 } else {
                     paramArray [i] = String.class;

                 }
             }




             //System.out.println("My word array contains: " + Arrays.asList(wordArray));
             //System.out.println("My parameter array contains: " + Arrays.asList(paramArray));
             //System.out.println("Instruction name is: " + instructionName);

             //Reflect class
             Class reflectionClass = Class.forName("sml." + instructionName);

             //System.out.println("My reflection is: " + reflectionClass);


             Constructor con = reflectionClass.getConstructor(paramArray);


             // Cast return back to an Instruction
            return ((Instruction)con.newInstance(wordArray));

        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
             System.err.println("Error");
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
             System.err.println("Error");
        } catch (InvocationTargetException e) {
             //e.printStackTrace();
             System.err.println("Error");
        } catch (InstantiationException e) {
             //e.printStackTrace();
             System.err.println("Error");
        } catch (IllegalAccessException e) {
             //e.printStackTrace();
             System.err.println("Error");
        }


/*
        // Commented out switch statement

        switch (ins) {
            case "add":
                r = scanInt();
                s1 = scanInt();
                s2 = scanInt();
                return new AddInstruction(label, r, s1, s2);
            case "lin":
                r = scanInt();
                s1 = scanInt();
                return new LinInstruction(label, r, s1);
            case "out":
                r = scanInt();
                return new OutInstruction(label, r);
            case "sub":
                r = scanInt();
                s1 = scanInt();
                s2 = scanInt();
                return new SubInstruction(label, r, s1, s2);
            case "mul":
                r = scanInt();
                s1 = scanInt();
                s2 = scanInt();
                return new MulInstruction(label, r, s1, s2);
            case "div":
                r = scanInt();
                s1 = scanInt();
                s2 = scanInt();
                return new DivInstruction(label, r, s1, s2);
            case "bnz":
                r = scanInt();
                s3 = scan();
                return new BnzInstruction(label, r, s3);
        }

        // You will have to write code here for the other instructions.
*/

        return null;
    }

    /*
     * Return the first word of line and remove it from line. If there is no
     * word, return ""
     */
    private String scan() {
        line = line.trim();
        if (line.length() == 0)
            return "";

        int i = 0;
        while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
            i = i + 1;
        }
        String word = line.substring(0, i);
        line = line.substring(i);
        return word;
    }

    // Return the first word of line as an integer. If there is
    // any error, return the maximum int
    private int scanInt() {
        String word = scan();
        if (word.length() == 0) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(word);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}