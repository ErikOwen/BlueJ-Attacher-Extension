
/**
 * Write a description of class BClassDescriptor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BClassDescriptor
{
    private int targetNumber;
    private String name;
    private String association;
    private boolean isTestClass;

    /**
     * Constructor for objects of class BClassDescriptor
     */
    public BClassDescriptor(int targetNumber, String name, String association, boolean isTestClass)
    {
        this.targetNumber = targetNumber;
        this.name = name;
        this.association = association;
        this.isTestClass = isTestClass;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public int getTargetNumber()
    {
        return this.targetNumber;
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public String getClassName()
    {
        return this.name;
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public String getAssociation()
    {
        return this.association;
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public boolean isTestClass()
    {
        return this.isTestClass;
    }
}
