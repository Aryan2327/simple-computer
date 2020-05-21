package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FullAssembler implements Assembler {
	 public int assemble(String inputFileName, String outputS, StringBuilder error) {
		 List<String> source = null;
		 try (Stream<String> lines = Files.lines(Paths.get(inputFileName))) {
			 source = lines.collect(Collectors.toList());
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 
		 if (error == null) throw new IllegalArgumentException("Coding error: the error buffer is null");
		 Map<Integer, String> errors = new TreeMap<>();
		 boolean flag = true;
		 List<String> inputCodeText = new ArrayList<>();
		 List<String> inputDataText = new ArrayList<>();
		 
		 for (int i = 0; i < source.size(); i++) {
			 if (source.get(i).trim().length() != 0) continue;
			 else {
				 for (int j = i + 1; j < source.size(); j++) {
					 if (source.get(j).trim().length() != 0) {
						 errors.put(i+1, "Error on line " + (i+1) + ": Illegal blank line in the source file");
						 break;
					 }
				 }
			 }
		 }
		 
		 for (int i = 0; i < source.size(); i++) {
			 if (source.get(i).trim().length() > 0 && (source.get(i).charAt(0) == ' ' || source.get(i).charAt(0) == '\t')) {
				 if (errors.get(i+1) == null) {
					 errors.put(i+1, "Error on line " + (i+1) + ": Line starts with illegal white space");
				 }
			 }
		 }
		 
		 for (int i = 0; i < source.size(); i++) {
			 if (source.get(i).trim().toUpperCase().contains("DATA")) {
				 for (int j = i + 1; j < source.size(); j++) {
					 if (source.get(j).trim().toUpperCase().contains("DATA")) {
						 if (errors.get(j+1) == null) {
							 errors.put(j+1, "Error on line " + (j+1) + ": Illegal repetition of DATA");
							 break;
						 }
					 }
				 }
				 if (!source.get(i).trim().toUpperCase().equals("DATA")) {
					 if (errors.get(i+1) == null) {
						 errors.put(i+1, "Error on line " + (i+1) + ": DATA line should only contain \"DATA\"");

					 }
				 }
				 if(!source.get(i).trim().equals("DATA")) {
					 if (errors.get(i+1) == null) {
						 errors.put(i+1, "Error on line " + (i+1) + ": DATA not in upper case");
					 }
				 }
				 //Adding to codeText and dataText
				 if (flag) {
					 for (int j = 0; j < i; j++) {
						 inputCodeText.add(source.get(j).trim());
					 }
					 for (int j = i + 1; j < source.size(); j++) {
						 if(source.get(j).trim().length() > 0) {
							 inputDataText.add(source.get(j).trim()); 
						 }
					 }
					 flag = false;
				 }
			 }
		 }
		 if (flag) {
			 for (int j = 0; j < source.size(); j++) {
				 if(source.get(j).trim().length() > 0) {
					 inputCodeText.add(source.get(j).trim());
				 }
			 }
		 }
		 
		 for (int i = 0; i < inputCodeText.size(); i++) {
			 String[] parts = inputCodeText.get(i).trim().split("\\s+");
			 if(!Instruction.OPCODES.keySet().contains(parts[0].toUpperCase())) {
				 if (errors.get(i+1) == null) {
					 errors.put(i+1, "Error on line " + (i+1) + ": illegal mnemonic " + parts[0]);
				 }
			 }
			 if(!Instruction.OPCODES.keySet().contains(parts[0])) {
				 if (errors.get(i+1) == null) {
					 errors.put(i+1, "Error on line " + (i+1) + ": mnemonic " + parts[0] + " is not in upper case");
				 }
			 }
			 if(noArgument.contains(parts[0])) {
				 if (parts.length > 1) {
					 if (errors.get(i+1) == null) {
						 errors.put(i+1, "Error on line " + (i+1) + ": illegal argument for mnemonic " + parts[0]);
					 }
				 }
			 }
			 else {
				 if (parts.length == 1) {
					 if (errors.get(i+1) == null) errors.put(i+1, "Error on line " + (i+1) + ": " + parts[0] + " requires an argument");
				 }
				 else if (parts.length > 2) {
					 if (errors.get(i+1) == null) errors.put(i+1, "Error on line " + (i+1) + ": " + parts[0] + " has too many arguments (should be one)");
				 }
				 else if(parts.length == 2) {
					 if (parts[1].charAt(0) == '#' || parts[1].charAt(0) == '@' || parts[1].charAt(0) == '&') parts[1] = parts[1].substring(1);
					 try {
						 Integer.parseInt(parts[1],16);
					 } catch(NumberFormatException e) {
						 if(errors.get(i+1) == null)
							 errors.put(i+1, "Error on line " + (i+1) + ": argument is not a hex number");
					 }

				 }
			 }
		 }
		 
		 int offset = inputCodeText.size() + 1;
		 for (int i = 0; i < inputDataText.size(); i++) {
			 String[] parts = inputDataText.get(i).trim().split("\\s+");
			 
			 if (parts.length != 2) {
				 if(errors.get(offset+i+1) == null)
					 errors.put(offset+i+1, "Error on line " + (offset+i+1) + ": invalid data pair");
			 }
			 else {
				 try {
					 Integer.parseInt(parts[0],16);
				 } catch(NumberFormatException e) { 
					 if(errors.get(offset+i+1) == null)
						 errors.put(offset+i+1, "Error on line " + (offset+i+1) +": data address is not a hex number");
				 }
				 
				 try {
					 Integer.parseInt(parts[1],16);
				 } catch(NumberFormatException e) { 
					 if(errors.get(offset+i+1) == null)
						 errors.put(offset+i+1, "Error on line " + (offset+i+1) +": data address is not a hex number");
				 }


			 }
		 }
		 
		 if(errors.keySet().size() > 0) {
			 for(Integer key : errors.keySet()) error.append(errors.get(key) + "\n");
			 return ((TreeMap<Integer, String>)errors).firstKey();
		 }
		 return new SimpleAssembler().assemble(inputFileName, outputS, error);
	 }
	 
	 public static void main(String[] args) {
		 String[] names = {"z001", "z002e", "z003", "z004e", "z005e", "z006e", "z007e", "z008e", "z009e",
				 "z010e", "z011e", "z012e", "z013e", "z014e", "z015e", "z016e",
				 "z017", "z018", "z019e", "z020e", "z021e", "z022e", "z023e",
				 "z024e", "z025e", "z026e", "z027e", "z028e", "z029e", "z030e",
				 "z031e", "z032e", "z033e", "z034e", "z035e", "z036e", "z037e",
				 "z038e", "z039e", "factorial", "factorialindirect7",
				 "merge", "qsort", "test", "100rt", "101rt", "102rt", "103rt", "104rt"};
		 //System.out.println("\"pasm\" should be in directory \"pasm\"");
		 //System.out.println("There has to be a directory \"pexe\"");
		 //System.out.println("Enter the name of the file in \"pasm\" directory without extension: ");
		 //Scanner keyboard = new Scanner(System.in);
		 //String filename = keyboard.nextLine();
		 for(String filename : names) {
			 StringBuilder builder = new StringBuilder();
			 System.out.println("Filename: " + filename);
			 System.out.println(new FullAssembler().assemble("pasm/" + filename + ".pasm", "pexe/" + filename + ".pexe", builder));
			 System.out.println("Builder");
			 System.out.println(builder);
			 System.out.println("-----------------");
		 }
	 }

}
