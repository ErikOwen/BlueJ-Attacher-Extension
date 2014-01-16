import bluej.extensions.*;
import bluej.extensions.event.*;
import bluej.extensions.editor.*;

import java.net.URL;
import javax.swing.*;
import java.awt.event.*;

/*
 * This is the starting point of a BlueJ Extension
 */
public class AttacherExtension extends Extension implements PackageListener {
    private String curProjFilePath;
    /*
     * When this method is called, the extension may start its work.
     */
    public void startup (BlueJ bluej) {
        // Register a generator for menu items
        bluej.setMenuGenerator(new ClassAttacher(bluej));

        // Listen for BlueJ events at the "package" level
        bluej.addPackageListener(this);
    }
    
    /*
     * A package has been opened. Print the name of the project it is part of.
     * System.out is redirected to the BlueJ debug log file.
     * The location of this file is given in the Help/About BlueJ dialog box.
     */
    public void packageOpened ( PackageEvent ev )
    {
    }  
  
    /*
     * A package is closing.
     */
    public void packageClosing ( PackageEvent ev )
    {
    }  
    
    /*
     * This method must decide if this Extension is compatible with the 
     * current release of the BlueJ Extensions API
     */
    public boolean isCompatible () { 
        return true; 
    }

    /*
     * Returns the version number of this extension
     */
    public String  getVersion () { 
        return ("1");  
    }

    /*
     * Returns the user-visible name of this extension
     */
    public String  getName () { 
        return ("Attacher Extension");  
    }

    public void terminate() {
        System.out.println ("Attacher Extension terminates");
    }
    
    public String getDescription () {
        return ("A BlueJ extension which allows the user to attach one class to the back of the other in the BlueJ IDE.");
    }
}