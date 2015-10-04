package comparisonTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		BufferedReader reader = new BufferedReader(new FileReader(file1));
		String line = reader.readLine();
		int methodPos = 0;
		
		while (line != null) {
			//skip line if it is commented out or blank
			if (line.contains("//")) line = reader.readLine();
			
			String[] tokens = line.split("[ ]+");	// words save to an array, spaces as deliminators
			
			//Checks for initializers
			if (line.contains("=") && !line.contains("==")) ; //Does nothing
			
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
					for (int i=0; i<originalFileItems.Methods.size(); i++) {
						if (originalFileItems.Methods.get(i).MethodName.equals(m.MethodName)) checkIfNew = false;
					}
					m.IsNew = checkIfNew;
					if (m.IsNew) System.out.println(m.MethodName + " has been added");
				}
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
				String fieldName, fieldModifier;
				//field with modifier
				if (line.contains("public") || line.contains("private"))
				{
					fieldName = tokens[2];
					fieldModifier = tokens[0];
				}
				else fieldName = tokens[1];
				if (fieldName.contains(";")) fieldName.replace(";", "");	// Removes semi-colon if present
				
				boolean checkIfNew = true;
				if (lookingAtChangedFile)
				{
					for (int i=0; i<originalFileItems.Fields.size(); i++)
					{
						if (originalFileItems.Fields.get(i).FieldName.equals(fieldName)) checkIfNew = false;
					}
					if (checkIfNew) System.out.println(fieldName + " has been added");
				}
				fieldList.add(new Field(checkIfNew, tokens[0], "", javaFile, fieldName));

			}
			
			line = reader.readLine();
		}
		FileItems items = new FileItems();
		items.Methods = methodList;
		items.Fields = fieldList;
		return items;
	}
	
	private void compareMethods(List<Method> originalMethods, List<Method> changedMethods)
	{
		for (int i=0; (i<originalMethods.size() && i<changedMethods.size()); i++) {
			Method original = originalMethods.get(i);
			Method changed = changedMethods.get(i);
			
			//check if method name at position i are equal
			if (!original.MethodName.equals(changed.MethodName)) {
				
				//Iterate through Changed Methods to check for a moved position
				int j =0;
				boolean found = false;
				while (j<changedMethods.size()) {
					if (changedMethods.get(j).MethodName.equals(original.MethodName) && i!=j) {
					
						//CURRENTLY CHECKING IF POSITION CHANGED BECAUSE OF A NEW METHOD
						//IF SO IT DID NOT MOVE
						if (changedMethods.get(j-1).IsNew) {
							System.out.println(original.MethodName + " moved from position " + original.Position + " to " + changedMethods.get(j).Position);
						}
						
						found = true;
						break;
					}
					
					j++;
				}
				
				if (found == false) System.out.println(original.MethodName + " is Deleted");
			}
		}
	}
	
	private void compareFields(List<Field> originalFields, List<Field> changedFields)
	{
		for (int i=0; i < originalFields.size(); i++) {
			Field originalF = originalFields.get(i);
			int j=0;
			boolean presentInChangedFile = false;
			// Iterate through changedFields list to check if field is deleted or modifier is changed
			while (j < changedFields.size()) {
				Field changedF = changedFields.get(j);
				if (changedF.FieldName.equals(originalF.FieldName)) {
					presentInChangedFile = true;
					if (!changedF.Modifier.equals(originalF.Modifier)) {
						System.out.println("The field modifier of " + originalF.FieldName + " was changed from " + originalF.Modifier + " to " + changedF.Modifier);
					}
				}
				j++;
			}
			if (!presentInChangedFile) System.out.println("The field " + originalF.FieldName + " was deleted");
		}
	}
	
}
