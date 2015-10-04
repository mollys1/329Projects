package comparisonTool;

public class Field 
{
	boolean IsNew;
	
	String Modifier;
	
	/**
	 * Class which the field is contained in
	 */
	String ClassName;
	
	/**
	 * File which the field is contained in
	 */
	String JavaFileName;
	
	
    /**
     * Name of the field.
     */
    String FieldName;
    
    public Field(boolean isNew, String modifier, String className, String javaFile, String fieldName)
    {
    	IsNew = isNew;
    	Modifier = modifier;
    	ClassName = className;
    	JavaFileName = javaFile;
    	FieldName = fieldName;
    }
}
