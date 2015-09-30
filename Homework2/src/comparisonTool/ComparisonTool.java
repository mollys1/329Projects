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
	
	ArrayList<Method> originalMethods, changedMethods;
	
	/**
	 * Compares the given javaFiles and returns string of their differences
	 * 
	 * @param priginalJavaFile file location for first java file to compare
	 * @param changedJavaFile file location for second java file to compare
	 * @return string of differences
	 * @throws IOException 
	 */
	public String Compare(String originalJavaFile, String changedJavaFile) throws IOException
	{
		File file1 = new File(originalJavaFile);
		File file2 = new File(changedJavaFile);
		BufferedReader reader = new BufferedReader(new FileReader(file1));
		originalMethods = (ArrayList<Method>) ParseFile(originalJavaFile);
		lookingAtChangedFile = true;
		changedMethods = (ArrayList<Method>) ParseFile(changedJavaFile);
		
		ArrayList<Method> list = (ArrayList<Method>) compareMethods(originalMethods, changedMethods);
		
		
		return "Not implemented";
	}
	
	private List<Method> ParseFile(String javaFile) throws IOException
	{
		ArrayList<Method> list = new ArrayList<Method>();
		File file1 = new File(javaFile);
		BufferedReader reader = new BufferedReader(new FileReader(file1));
		String line = reader.readLine();
		int methodPos = 0;
		
		while (line != null) {
			String[] tokens = line.split("[ ]+");	// words save to an arry, spaces as deliminators
			
			//Checks for initializers
			if (line.contains("=") && !line.contains("==")) ; //Does nothing
			
			//Checks for methods
			if (line.contains("(") && line.contains(")") && (line.contains("public") || line.contains("private"))) {
				Method m = new Method();
				m.JavaFileName = javaFile;
				m.Position = methodPos++;
				m.MethodName = tokens[2].substring(0, tokens[2].indexOf('('));
				if (lookingAtChangedFile == true) {
					boolean checkIfNew = true;
					for (int i=0; i<originalMethods.size(); i++) {
						if (originalMethods.get(i).MethodName.equals(m.MethodName)) checkIfNew = false;
					}
					m.IsNew = checkIfNew;
					if (m.IsNew) System.out.println(m.MethodName + " has been added");
				}
				list.add(m);
			}
			
			
			
			
			
			line = reader.readLine();
		}

		return list;
	}
	
	private ArrayList<Method> compareMethods(ArrayList<Method> orignalMethods, ArrayList<Method> changedMethods)
	{
		for (int i=0; (i<originalMethods.size() && i<changedMethods.size()); i++) {
			Method original = orignalMethods.get(i);
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
		
		return null;
	}
	
}
