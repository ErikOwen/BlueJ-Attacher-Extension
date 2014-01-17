
/**
 * The BDescriptor class is a data structure that holds important
 * information for classes held in the BlueJ iDE.
 * 
 * @author Erik Owen
 * @version 1
 */
public class BClassDescriptor
{
    private int targetNumber;
    private String name;
    private String association;
    private boolean isTestClass;
    private Integer xPos;
    private Integer yPos;

    /**
     * Constructor for objects of class BClassDescriptor
     * 
     * @param targetNumber the target number of the current class
     * @param name the name of the current class
     * @param association the association the current class has
     * @param isTestClass showing whether or not this class is a test class or not
     * @param x the x coordinate that the class is located on the IDE
     * @param y the y coordinate that the class is located on the IDE
     */
    public BClassDescriptor(int targetNumber, String name,
        String association, boolean isTestClass, Integer x, Integer y)
    {
        this.targetNumber = targetNumber;
        this.name = name;
        this.association = association;
        this.isTestClass = isTestClass;
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * Gets the target number of this class
     * 
     * @return the target number of the current class
     */
    public int getTargetNumber()
    {
        return this.targetNumber;
    }
    
    /**
     * Gets the current class name
     * 
     * @return the name of this class
     */
    public String getClassName()
    {
        return this.name;
    }
    
    /**
     * Gets the association class of this class
     * 
     * @return the association that this class has
     */
    public String getAssociation()
    {
        return this.association;
    }
    
    /**
     * Determines if this class is a test class or not
     * 
     * @return whether or not the this class is a test class
     */
    public boolean isTestClass()
    {
        return this.isTestClass;
    }
    
    /**
     * Returns the x coordinate location in the IDE of the 
     * current class
     * 
     * @return an x coordinate represented as an Integer
     */
    public Integer getXPosition()
    {
        return this.xPos;
    }
    
    /**
     * Returns the y coordinate location in the IDE of the 
     * current class
     * 
     * @return an y coordinate represented as an Integer
     */
    public Integer getYPosition()
    {
        return this.yPos;
    }

}
