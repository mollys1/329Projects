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
    
    /**
     * Initialization value of the field
     */
    String FieldInit;
    
    public Field(boolean isNew, String modifier, String className, String javaFile, String fieldName, String fieldInit)
    {
    	IsNew = isNew;
    	Modifier = modifier;
    	ClassName = className;
    	JavaFileName = javaFile;
    	FieldName = fieldName;
    	FieldInit = fieldInit;
    }
}
