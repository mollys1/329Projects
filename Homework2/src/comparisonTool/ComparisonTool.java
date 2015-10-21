package comparisonTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Molly Schaeffer and Sarah Ulmer
 *
 *	Comparison tool class to do the comparing
 *
 */
public class ComparisonTool 
{
	boolean lookingAtChangedFile = false;
	FileWriter outputFile;

	FileItems originalFileItems, changedFileItems;
	
	/**
	 * Compares the given javaFiles and returns message once finished
	 * 
	 * @param originalJavaFile file location for first java file to compare
	 * @param changedJavaFile file location for second java file to compare
	 * @return string of differences
	 * @throws IOException 
	 */
	public String Compare(String originalJavaFile, String changedJavaFile) throws IOException
	{
		outputFile = new FileWriter("comparison_results.txt");
		outputFile.write("Results from comparing " + originalJavaFile + " and " + changedJavaFile + "\r\n");
		 
		originalFileItems = ParseFile(originalJavaFile);
		lookingAtChangedFile = true;
		changedFileItems = ParseFile(changedJavaFile);
		
		compareMethods(originalFileItems.Methods, changedFileItems.Methods);
		compareFields(originalFileItems.Fields, changedFileItems.Fields);
		
		outputFile.close();
		return "Finished Comparing " + originalJavaFile + " and " + changedJavaFile + "\r\nThe results are written to comparison_results.txt";
	}
	
	/**
	 * Private helper method to parse a file. It reads the file one line at a time,
	 * looking for methods and fields to save to Lists
	 * 
	 * @param javaFile The file to parse
	 * @return FileItems containing two lists of the methods and fields found in the file
	 * @throws IOException
	 */
	private FileItems ParseFile(String javaFile) throws IOException
	{
		int openBracketCount = 0;
		ArrayList<Method> methodList = new ArrayList<Method>();
		ArrayList<Field> fieldList = new ArrayList<Field>();
		File file1 = new File(javaFile);
		LineNumberReader reader = new LineNumberReader(new FileReader(file1));
		String line = reader.readLine();
		int methodPos = 0;
		
		while (line != null) {
			//Disregard comments
			if (line.contains("//")) line = line.substring(0, line.indexOf("//"));
			
			//Split the line into array of words, recognizing spaces as the deliminators separating words
			String[] tokens = line.split("[ ]+");
			
			//Keep bracket count
			if (line.contains("{")) openBracketCount++;
			if (line.contains("}")) openBracketCount--;
			
			//Checks for methods
			boolean checkIfNew = true;
			if (line.contains("(") && line.contains(")") && (line.contains("public") || line.contains("private")) && openBracketCount == 1) {
				String methodName;
				int methodPosition = methodPos++;
				methodName = tokens[2].substring(0, tokens[2].indexOf('('));
				if (lookingAtChangedFile == true) {
					for (int i=0; i<originalFileItems.Methods.size(); i++) {	//Iterate through original file methods to see if this is a new method
						if (originalFileItems.Methods.get(i).MethodName.equals(methodName)) checkIfNew = false;
					}
					if (checkIfNew) {
						outputFile.write("The method " + methodName + " has been added\r\n");
					}
				}
				int filePosition = reader.getLineNumber();
				Method m = new Method(checkIfNew, methodPosition, filePosition, "", javaFile, methodName);
				methodList.add(m);
			}
			
			//Check for fields & the modifiers or initializers of those fields
			//If open bracket count is 1 then within class but not within method
			else if (!(line.contains("(") || line.contains(")") || line.contains("{") || line.contains("}")) && line.length() > 1 && openBracketCount == 1)
			{
				String fieldName, fieldInit;
				String fieldModifier = "";
				fieldInit = null;
				//field with modifier
				if (line.contains("public") || line.contains("private"))
				{
					fieldName = tokens[2];
					fieldModifier = tokens[0];
					fieldModifier = fieldModifier.substring(fieldModifier.indexOf("p"), fieldModifier.length()); //Removes white spaces from start of line
				}
				else fieldName = tokens[1];
				if (fieldName.contains(";")) fieldName = fieldName.substring(0, fieldName.indexOf(";"));	// Removes semi-colon if present
				
				// Check field initializations
				if (line.contains("=") && !line.contains("==")) {
					String lineSubstring;
					if (line.contains("= ")) lineSubstring = line.substring(line.indexOf("=")+2, line.length());
					else lineSubstring = line.substring(line.indexOf("=")+1, line.length());
					if (lineSubstring.contains(";")) lineSubstring = lineSubstring.substring(0, lineSubstring.indexOf(";"));	//Remove semi-colon if present
					fieldInit = lineSubstring;
				}
				
				checkIfNew = true;
				if (lookingAtChangedFile)
				{
					for (int i=0; i<originalFileItems.Fields.size(); i++)
					{
						if (originalFileItems.Fields.get(i).FieldName.equals(fieldName)) checkIfNew = false;
					}
					if (checkIfNew) outputFile.write("The field " + fieldName + " has been added\r\n");
				}
				fieldList.add(new Field(checkIfNew, fieldModifier, "", javaFile, fieldName, fieldInit));

			}
			
			line = reader.readLine();
		}
		
		reader.close();
		FileItems items = new FileItems();
		items.Methods = methodList;
		items.Fields = fieldList;
		return items;
	}
	
	/**
	 * Private helper method comparing the method lists of the two files parsed.
	 * Given two list of methods from the original and changed files, the methods are examined and compared against
	 * each other. This method checks for methods that have been deleted in the changed file, and methods that have
	 * moved or changed position in the changed file. This method calls upon a helper method to compare method bodies.
	 * The results of the findings are written to the output file.
	 * 
	 * @param originalMethods List of methods from the original file
	 * @param changedMethods List of methods from the changed file
	 * @throws IOException
	 */
	private void compareMethods(List<Method> originalMethods, List<Method> changedMethods) throws IOException
	{
		for (int i=0; i<originalMethods.size(); i++) {
			Method originalM = originalMethods.get(i);
			int j=0;
			boolean presentInChangedFile = false;
			//Iterate through changed methods list to look for this method from original files
			while (j < changedMethods.size() && !presentInChangedFile) {
				Method changedM = changedMethods.get(j);
				
				//If method exists in changed methods, check for position change
				if (changedM.MethodName.equals(originalM.MethodName)) {
					presentInChangedFile = true;
					
					if (changedM.MethodPosition != originalM.MethodPosition && changedMethods.get(j-1).IsNew) {
						outputFile.write("The method " + originalM.MethodName + " changed position from " + originalM.MethodPosition + " to " + changedM.MethodPosition + "\r\n");
					}
					
					compareMethodBodies(originalM, changedM);
				}
				
				j++;
			}
			if (!presentInChangedFile) {
				outputFile.write("The method " + originalM.MethodName + " was deleted\r\n");
			}
		}
	}
	
	/**
	 * Private helper method that compares the bodies of the two given methods and prints a message
	 * to the output file if method bodies have changed.
	 * 
	 * @param originalMethod
	 * @param changedMethod
	 * @throws IOException
	 */
	private void compareMethodBodies(Method originalMethod, Method changedMethod) throws IOException
	{
		// Find start of method in both files
		File file1 = new File(originalMethod.JavaFileName);
		File file2 = new File(changedMethod.JavaFileName);
		LineNumberReader readOriginal = new LineNumberReader(new FileReader(file1));
		LineNumberReader readChanged = new LineNumberReader(new FileReader(file2));
		String originalLine = readOriginal.readLine();
		String changedLine = readChanged.readLine();
		for (int i=2; i<=originalMethod.FilePosition; i++) originalLine = readOriginal.readLine();
		for (int i=2; i<=changedMethod.FilePosition; i++) changedLine = readChanged.readLine();
		
		//Compare method bodies
		//First find the { to start the method
		int openBracketCount = 0;	//Keeping track of bracket count only in original method - if brackets were added or taken away, method change would show
		int closedBracketCount = 0;
		if (originalLine.contains("{")) {
			openBracketCount++;
		}
		else {
			originalLine = readOriginal.readLine();
			if (originalLine.contains("{")) openBracketCount++;
		}
		
		if (!changedLine.contains("{")) changedLine = readChanged.readLine();	// if { not on first line, move to second line
		
		while (openBracketCount != closedBracketCount)
		{
			if (!originalLine.equals(changedLine)) {
				outputFile.write("The method body of " + originalMethod.MethodName + " has been changed\r\n");
				break;
			}
			
			originalLine = readOriginal.readLine();
			changedLine = readChanged.readLine();
			
			if (originalLine.contains("{")) openBracketCount++;
			if (originalLine.contains("}")) closedBracketCount++;
		}
		
		readOriginal.close();
		readChanged.close();
	}

	/**
	 * Private helper method that compares the fields from the two files examined.
	 * Given a list of fields, it checks if fields have been deleted, if their modifiers have changed, or if their
	 * initializations have changed, been added, or deleted. The results are written to the output file.
	 * 
	 * @param originalFields List of fields found in the original file
	 * @param changedFields List of fields found in the changed file
	 * @throws IOException 
	 */
	private void compareFields(List<Field> originalFields, List<Field> changedFields) throws IOException
	{
		for (int i=0; i < originalFields.size(); i++) {
			Field originalF = originalFields.get(i);
			int j=0;
			boolean presentInChangedFile = false;
			// Iterate through changedFields list to check if field is deleted or modifier is changed or initalization changed
			while (j < changedFields.size() && !presentInChangedFile) {
				Field changedF = changedFields.get(j);
				if (changedF.FieldName.equals(originalF.FieldName)) {
					presentInChangedFile = true;
					
					//Check modifiers
					if (!changedF.Modifier.equals(originalF.Modifier)) {
						outputFile.write("The field modifier of " + originalF.FieldName + " was changed from " + originalF.Modifier + " to " + changedF.Modifier + "\r\n");
					}
					
					//Check initializations
					//1st check if initialization is added
					if (changedF.FieldInit != null && originalF.FieldInit == null) {
						outputFile.write("The field initialization of " + changedF.FieldName + " has been added and is initialized to " + changedF.FieldInit + "\r\n");
					}
					//2nd check if initialization is deleted
					else if (changedF.FieldInit == null && originalF.FieldInit != null)
					{
						outputFile.write("The field initialization of " + changedF.FieldName + " has been deleted\r\n");
					}
					//3rd check if modifer has changed
					else if (changedF.FieldInit != null && originalF.FieldInit != null) {
						if (!changedF.FieldInit.equals(originalF.FieldInit)) {
							outputFile.write("The field initialization of " + changedF.FieldName + " has been changed from " + originalF.FieldInit + " to " + changedF.FieldInit + "\r\n");
						}
					}
				}
				j++;
			}
			if (!presentInChangedFile) outputFile.write("The field " + originalF.FieldName + " was deleted\r\n");
		}
	}
	
}
