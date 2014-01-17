import bluej.extensions.*;
import bluej.extensions.event.*;
import bluej.extensions.editor.*;

import java.net.URL;
import javax.swing.*;
import java.awt.event.*;

/**
 * The AttacherExtension is the starting point for 
 * the Attacher Extension code base. Its main purpose
 * is to provide information and call the class which 
 * starts the majority of the extension's work.
 * 
 * @author Erik Owen
 * @version 1
 */
public class AttacherExtension extends Extension
{
    private String curProjFilePath;
    
    /**
     * When this method is called, the extension may start its work.
     * 
     * @param bluej BlueJ class itself
     */
    public void startup(BlueJ bluej)
    {
        // Register a generator for menu items
        bluej.setMenuGenerator(new ClassAttacher(bluej));
    } 
    
    /**
     * This method must decide if this Extension is compatible with the 
     * current release of the BlueJ Extensions API
     * 
     * @return whether or not the extension is compatible with their version of BlueJ
     */
    public boolean isCompatible()
    { 
        return true; 
    }

    /**
     * Returns the version number of this extension
     * 
     * @return the version of this extension
     */
    public String getVersion()
    { 
        return ("1");  
    }

    /**
     * Returns the user-visible name of this extension
     * 
     * @return the name of this extension
     */
    public String getName()
    { 
        return ("Attacher Extension");  
    }

    /**
     * This method is called when the extension is terminated
     */
    public void terminate()
    {
        System.out.println("Attacher Extension terminates");
    }
    
    /**
     * This method gives the description of the Attacher Extension
     * 
     * @return the description of this extension
     */
    public String getDescription()
    {
        return ("A BlueJ extension which allows the user " + 
            "to attach one class to the back of the other in the BlueJ IDE.");
    }
}