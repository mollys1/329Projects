package comparisonTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Molly
 *
 *	Comparison tool class to do the comparing
 *
 */
public class ComparisonTool 
{
	boolean lookingAtChangedFile = false;

	FileItems originalFileItems, changedFileItems;
	
	/**
	 * Compares the given javaFiles and returns string of their differences
	 * 
	 * @param originalJavaFile file location for first java file to compare
	 * @param changedJavaFile file location for second java file to compare
	 * @return string of differences
	 * @throws IOException 
	 */
	public String Compare(String originalJavaFile, String changedJavaFile) throws IOException
	{
		originalFileItems = ParseFile(originalJavaFile);
		lookingAtChangedFile = true;
		changedFileItems = ParseFile(changedJavaFile);
		
		compareMethods(originalFileItems.Methods, changedFileItems.Methods);
		compareFields(originalFileItems.Fields, changedFileItems.Fields);
		
		
		return "Not fully implemented";
	}
	
	private FileItems ParseFile(String javaFile) throws IOException
	{
		boolean withinClass = false; //is parser within a class (i.e. where fields would be declared)
		int openBracketCount = 0;
		ArrayList<Method> methodList = new ArrayList<Method>();
		ArrayList<Field> fieldList = new ArrayList<Field>();
		File file1 = new File(javaFile);
		LineNumberReader reader = new LineNumberReader(new FileReader(file1));
		String line = reader.readLine();
		int methodPos = 0;
		
		while (line != null) {
			//skip line if it is commented out or blank
			if (line.contains("//")) line = line.substring(0, line.indexOf("//"));		// Ignores everything after "//"
			
			String[] tokens = line.split("[ ]+");	// words save to an array, spaces as deliminators
			
			//Keep bracket count
			if (line.contains("{")) openBracketCount++;
			if (line.contains("}")) openBracketCount--;
			
			//Checks for methods
			if (line.contains("(") && line.contains(")") && (line.contains("public") || line.contains("private"))) {
				Method m = new Method();
				m.JavaFileName = javaFile;
				m.Position = methodPos++;
				m.MethodName = tokens[2].substring(0, tokens[2].indexOf('('));
				if (lookingAtChangedFile == true) {
					boolean checkIfNew = true;
					for (int i=0; i<originalFileItems.Methods.size(); i++) {	//Iterate through original file methods to see if this is a new method
						if (originalFileItems.Methods.get(i).MethodName.equals(m.MethodName)) checkIfNew = false;
					}
					m.IsNew = checkIfNew;
					if (m.IsNew) {
						System.out.println("The method " + m.MethodName + " has been added");
					}
				}
				m.FilePosition = reader.getLineNumber();
				methodList.add(m);
			}
			//Check for fields
			//Check for fields that have modifiers
			//If open bracket count is 1 then within class but not within method
//			else if (!(line.contains("(") && line.contains(")") && line.contains("{") && line.contains("}")) && (line.contains("public") || line.contains("private")) && openBracketCount == 1)
//			{
//				String fieldName = tokens[2];
//				boolean checkIfNew = true;
//				if (lookingAtChangedFile)
//				{
//					for (int i=0; i<originalFileItems.Fields.size(); i++)
//					{
//						if (originalFileItems.Fields.get(i).FieldName.equals(fieldName)) checkIfNew = false;
//					}
//					if (checkIfNew) System.out.println(fieldName + " has been added");
//				}
//				fieldList.add(new Field(checkIfNew, tokens[0], "", javaFile, fieldName));
//				System.out.println("Field with modifier: " + line.trim());
//			}
			
			
			//Check for fields
			else if (!(line.contains("(") || line.contains(")") || line.contains("{") || line.contains("}")) && line.length() > 1 && openBracketCount == 1)
			{
				String fieldName, fieldModifier, fieldInit;
				fieldInit = null;
				//field with modifier
				if (line.contains("public") || line.contains("private"))
				{
					fieldName = tokens[2];
					fieldModifier = tokens[0];
				}
				else fieldName = tokens[1];
				if (fieldName.contains(";")) fieldName = fieldName.substring(0, fieldName.indexOf(";"));	// Removes semi-colon if present
				
				// Check field initializations
				if (line.contains("=") && !line.contains("==")) {
					String lineSubstring = line.substring(line.indexOf("=")+1, line.length());
					if (lineSubstring.contains(";")) lineSubstring = lineSubstring.substring(0, lineSubstring.indexOf(";"));	//Remove semi-colon if present
					fieldInit = lineSubstring;
				}
				
				boolean checkIfNew = true;
				if (lookingAtChangedFile)
				{
					for (int i=0; i<originalFileItems.Fields.size(); i++)
					{
						if (originalFileItems.Fields.get(i).FieldName.equals(fieldName)) checkIfNew = false;
					}
					if (checkIfNew) System.out.println("The field " + fieldName + " has been added");
				}
				fieldList.add(new Field(checkIfNew, tokens[0], "", javaFile, fieldName, fieldInit));

			}
			
			line = reader.readLine();
		}
		
		reader.close();
		FileItems items = new FileItems();
		items.Methods = methodList;
		items.Fields = fieldList;
		return items;
	}
	
	private void compareMethods(List<Method> originalMethods, List<Method> changedMethods)
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
					
					if (changedM.Position != originalM.Position && changedMethods.get(j-1).IsNew) {
						System.out.println("The method " + originalM.MethodName + " changed position from " + originalM.Position + " to " + changedM.Position);
					}
					
					compareMethodBodies(originalM, changedM);
				}
				
				j++;
			}
			if (!presentInChangedFile) {
				System.out.println("The method " + originalM.MethodName + " was deleted");
			}
		}
	}
	
	private void compareMethodBodies(Method originalMethod, Method changedMethod) {
		
	}
	
	private void compareFields(List<Field> originalFields, List<Field> changedFields)
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
						System.out.println("The field modifier of " + originalF.FieldName + " was changed from " + originalF.Modifier + " to " + changedF.Modifier);
					}
					
					//Check initializations
					//1st check if initialization is added
					if (changedF.FieldInit != null && originalF.FieldInit == null) {
						System.out.println("The field initialization of " + changedF.FieldName + " has been added and is initialized to " + changedF.FieldInit);
					}
					//2nd check if initialization is deleted
					else if (changedF.FieldInit == null && originalF.FieldInit != null)
					{
						System.out.println("The initialization of field " + changedF.FieldName + " has been deleted");
					}
					//3rd check if modifer has changed
					else if (changedF.FieldInit != null && originalF.FieldInit != null) {
						if (!changedF.FieldInit.equals(originalF.FieldInit)) {
							System.out.println("The field initialization of " + changedF.FieldName + " has been changed from " + originalF.FieldInit + " to " + changedF.FieldInit);
						}
					}
				}
				j++;
			}
			if (!presentInChangedFile) System.out.println("The field " + originalF.FieldName + " was deleted");
		}
	}
	
}
