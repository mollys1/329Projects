package comparisonTool;

/**
 * @author Molly
 *
 */
public class Method 
{
	/**
	 * Is the method new
	 */
	boolean IsNew;
	/**
	 * Method's position within the class
	 */
	int MethodPosition; 
	
	/**
	 * Method's line position in the file
	 */
	int FilePosition;
	
	/**
	 * Class which the method is contained in
	 */
	String ClassName;
	
	/**
	 * File which the method is contained in
	 */
	String JavaFileName;
	
	
    /**
     * Name of the method.
     */
    String MethodName;
    
    public Method(boolean isNew, int methodPosition, int filePosition, String className, String javaFile, String methodName)
    {
    	IsNew = isNew;
    	MethodPosition = methodPosition;
    	FilePosition = filePosition;
    	ClassName = className;
    	JavaFileName = javaFile;
    	MethodName = methodName;
    }
}
