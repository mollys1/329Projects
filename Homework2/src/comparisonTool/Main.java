package comparisonTool;

public class Main {

	public static void main(String[] args) {
		
		ComparisonTool compareTool = new ComparisonTool();
		String testFile = "C:\\Users\\Molly\\Documents\\GitHub\\329Projects\\Homework2\\src\\test\\Test.java";
		String testChangedFile = "C:\\Users\\Molly\\Documents\\GitHub\\329Projects\\Homework2\\src\\test\\TestChanged.java";
		System.out.println(compareTool.Compare(testFile, testChangedFile));
	}

}
