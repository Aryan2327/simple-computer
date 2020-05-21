package project;

public class Test {
	public static void main(String[] args) {
		Instruction instr1 = new Instruction((byte)10, 1);
		Instruction.checkParity(instr1);
		
		Instruction instr2 = new Instruction((byte)12, 1);
		Instruction.checkParity(instr2);
		
		Instruction instr3 = new Instruction((byte)11, 1);
		Instruction.checkParity(instr3);
	}
}
