package comparisonTool;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		
		String testFile, testChangedFile;
		if (args.length == 2) //if files provided at the command line
		{
			testFile = args[0];
			testChangedFile = args[1];
			ComparisonTool compareTool = new ComparisonTool();
			try
			{
				System.out.println(compareTool.Compare(testFile, testChangedFile));
			}
			catch (Exception ex)
			{
				System.out.println("An exception occurred:");
				System.out.println(ex.getMessage());
			}
		}
		else
		{
			System.out.println("Please specify two files to compare");
		}
//		testFile = "C:\\Users\\Molly\\Documents\\GitHub\\329Projects\\Homework2\\src\\test\\Test.java";
//		testChangedFile = "C:\\Users\\Molly\\Documents\\GitHub\\329Projects\\Homework2\\src\\test\\TestChanged.java";
	}

}
