package project;
import java.util.*;

public interface Assembler {
	class DataPair {
		protected int address;
		protected int value;
		
		public DataPair(int a, int v) {
			address = a;
			value = v;
		}
		
		public String toString() {
			return "DataPair (" + address + ", " + value + ")";
		}
	}
	
	Set<String> noArgument = new TreeSet(Arrays.asList("HALT", "NOP", "NOT" ));
	
	int assemble(String inputFileName, String outputFIleName, StringBuilder error);
}
