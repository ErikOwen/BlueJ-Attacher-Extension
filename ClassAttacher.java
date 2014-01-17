import bluej.extensions.*;
import bluej.extensions.event.*;
import bluej.extensions.editor.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * The ClassAttacher class creates the attacher menu item and
 * does all of the behind the scenes work to attach a unit test
 * class to a regular class
 * 
 * @author Erik Owen
 * @version 1
 */
public class ClassAttacher extends MenuGenerator
{
    private BPackage curPackage;    //The current package the user is in in BlueJ
    private BClass curClass;    //The current class the user is in in BlueJ
    private BObject curObject;  //The current object the user is referring to in BlueJ
    private BlueJ bluej;    //An instance of the BlueJ class itself
    private ArrayList<BClassDescriptor> classList; //List with the classes in the package
    
    private static final int kMaxClassNameLength = 50;
    private static final int kPixelOffset = 30;
    private static final int kTargetWordLength = 6;
    
    /**
     * Constructor to create a ClassAttacher object
     * 
     * @param bluej the highest level of class in the extension class
     */
    public ClassAttacher(BlueJ bluej)
    {
        this.bluej = bluej;
        this.classList = new ArrayList<BClassDescriptor>();
    }

    /**
     * Method which give the attach item to classes that are unit test classes
     * 
     * @param aClass the current class selected
     * @return a new JMenuItem
     */
    public JMenuItem getClassMenuItem(BClass aClass)
    {
        boolean allowAttachOption = false;
        JMenuItem returnItem;
        try
        {
            curPackage = aClass.getPackage();
            curPackage.getProject().save();
            String filePath = curPackage.getDir().getPath().concat("/bluej.pkg");
            
            parsePackageFile(filePath);
        }
        catch(PackageNotFoundException exc)
        {
            System.out.println("The current package could not be found.");
        }
        catch(ProjectNotOpenException exc)
        {
            System.out.println("Project is not open.");
        }

        //iterates through all of the classes in this package
        for(BClassDescriptor curClassDescriptor : this.classList)
        {
            //Checks to see if the current class is a test class
            if(curClassDescriptor.getClassName().equals(aClass.getName()) &&
                curClassDescriptor.isTestClass())
            {
                allowAttachOption = true;
            }
        }
        
        //If the current class is a test class then it adds the attach option
        if(allowAttachOption)
        {
            returnItem = new JMenuItem(new SimpleAction("Attach...",
                "Attach Test Class To: "));
        }
        //else it returns null
        else
        {
            returnItem = null;
        }
        
        return returnItem;
    }
    
    /**
     * This class notifies which current class is selected
     * 
     * @param bc the class that was clicked
     * @param jmi the JMenuItem added to the options in that class
     */
    public void notifyPostClassMenu(BClass bc, JMenuItem jmi)
    {
        System.out.println("Post on Class menu");
        curPackage = null;
        curObject = null;
        curClass = bc;

    }
    
    private void createClassChooserWindow()
    {
        try
        {
            //If the current object is not null, then get its class
            if (curObject != null)
            {
                curClass = curObject.getBClass();
            }
            //If the current class is not null, then get its package
            if (curClass != null)
            {
                curPackage = curClass.getPackage();
            }
            
            String filePath = curPackage.getDir().getPath().concat("/bluej.pkg");
            
            parsePackageFile(filePath);    
                
            String [] classNames = new String[kMaxClassNameLength];
            int curIndex = 0;
            
            //Iterates throught the list of all the classes
            for(BClassDescriptor currentClass : this.classList)
            {
                //If the current class is not a test class, then add it to the classes
                //that the current test class can attach to
                if(!currentClass.isTestClass())
                {
                    classNames[curIndex] = currentClass.getClassName();
                    curIndex++;
                }
            }
            
            JFrame frame = new JFrame("Attacher");
            String attacherClass = (String) JOptionPane.showInputDialog(frame,
                "Choose one:", "Select Target", JOptionPane.QUESTION_MESSAGE,
                null, classNames, classNames[0]);
                
            modifyBlueJPackageFile(attacherClass);
        }
        catch (ProjectNotOpenException exc)
        {
            System.out.println("Project is not open.");    
        }
        catch (PackageNotFoundException exc)
        {
            System.out.println("The current package could not be found.");
        }
        catch (bluej.extensions.ClassNotFoundException exc)
        {
            System.out.println("The current class could not be found.");
        }
    }
    
    private void modifyBlueJPackageFile(String classToAttachTo)
    {
        try
        {
            BProject curProject = curPackage.getProject();
            boolean foundTarget = false;
            String filePath = curPackage.getDir().getPath().concat("/bluej.pkg");
            File curProjDirectory = curProject.getDir();
            
            curProject.close();
            FileWriter fStream = new FileWriter(filePath, true);
            
            BClassDescriptor attacherClass = 
                findBClassDescriptorInList(curClass.getName());
            BClassDescriptor attachedClass = findLastAttachedClass(classToAttachTo);
            
            fStream.append("target" + attachedClass.getTargetNumber() +
                ".association=" + curClass.getName() + "\n");
            fStream.append("target" + attacherClass.getTargetNumber() +
                ".x=" + (attachedClass.getXPosition() + kPixelOffset) + "\n");
            fStream.append("target" + attacherClass.getTargetNumber() +
                ".y=" + (attachedClass.getYPosition() - kPixelOffset) + "\n");
            
            fStream.flush();
            fStream.close();
            
            this.bluej.openProject(curProjDirectory);
            
        }
        catch (ProjectNotOpenException exc)
        {
            System.out.println("Project is not open.");    
        }
        catch (PackageNotFoundException exc)
        {
            System.out.println("The current package could not be found.");
        }
        catch (IOException exc)
        {
            System.out.println("There was a problem creating the FileWriter.");    
        }
    }
    
    private void parsePackageFile(String filePath)
    {
        this.classList.clear();
        File packageInfoFile = new File(filePath);
        int curTargetNumber = 0;
        String curName = null;
        String curAssociation = null;
        boolean isTestClass = false;
        Integer curXPos = 0;
        Integer curYPos = 0;
        
        try
        {
            Scanner scan = new Scanner(packageInfoFile);
            //Continues to read lines while there is one to read in
            while(scan.hasNext())
            {
                String line = scan.nextLine();
            
                //Determines f the first part of the line is "target"
                if((line.substring(0, kTargetWordLength).equals("target")))
                {
                    //Determines if the class' target number is the same
                    //target number as the previous one read in
                    if(curTargetNumber != 0 && Integer.parseInt(line.substring(
                        kTargetWordLength, line.indexOf("."))) != curTargetNumber)
                    {
                        this.classList.add(new BClassDescriptor(curTargetNumber,
                            curName, curAssociation, isTestClass, curXPos, curYPos));
                        
                        curName = null;
                        curAssociation = null;
                        isTestClass = false;
                    }
                    
                    curTargetNumber = Integer.parseInt(line.substring(kTargetWordLength,
                        line.indexOf(".")));
                        
                    String tempName = curName;
                    String tempAssociation = curAssociation;
                    boolean tempIsTestClass = isTestClass;
                    Integer tempXPos = curXPos;
                    Integer tempYPos = curYPos;
                    
                    curName = checkForName(line, tempName);
                    curAssociation = checkForAssociation(line, tempAssociation);
                    isTestClass = checkForIsTestClass(line, tempIsTestClass);
                    curXPos = checkXPosition(line, tempXPos);
                    curYPos = checkYPosition(line, tempYPos);
                }
            }
            
            //Check to determine if the last class in the while loop was
            //added to the list or not
            if(curName != null)
            {
                this.classList.add(new BClassDescriptor(curTargetNumber,
                    curName, curAssociation, isTestClass, curXPos, curYPos));
            }
            
        }
        catch(java.io.FileNotFoundException exc)
        {
            System.out.println("The file could not be found.");
        }
        
    }
    
    private String checkForName(String line, String tempName)
    {
        String returnName = tempName;
        
        //Saves the classes name if current line is the name attribute
        if(line.substring(line.indexOf(".") + 1, line.indexOf("=")).equals("name"))
        {
            returnName = line.substring(line.indexOf("=") + 1, line.length());
        }
        
        return returnName;
    }
    
    private String checkForAssociation(String line, String tempAssociation)
    {
        String returnAssociation = tempAssociation;
        
        //Determines the class' association if current line is the
        //association attribute
        if(line.substring(line.indexOf(".") + 1,
            line.indexOf("=")).equals("association"))
        {
            returnAssociation = line.substring(line.indexOf("=") + 1, line.length());
        }
        
        return returnAssociation;
    }
    
    private boolean checkForIsTestClass(String line, boolean tempIsTestClass)
    {
        boolean returnIsTestClass = tempIsTestClass;
        
        //If the current line is the class type attribute, it
        //saves the type
        if(line.substring(line.indexOf(".") + 1, line.indexOf("=")).equals("type"))
        {
            returnIsTestClass = line.substring(line.indexOf("=") + 1,
                line.length()).equals("UnitTestTarget");
        }
        
        return returnIsTestClass;
    }
    
    private Integer checkXPosition(String line, Integer tempXPos)
    {
        Integer returnXPos = tempXPos;
        
        //If the current line is the class type attribute, it
        //saves the type
        if(line.substring(line.indexOf(".") + 1,
            line.indexOf("=")).equals("x"))
        {
            returnXPos = Integer.parseInt(line.substring(line.indexOf("=") + 1, 
                line.length()));
        }
        
        return returnXPos;
    }
    
    private Integer checkYPosition(String line, Integer tempYPos)
    {
        Integer returnYPos = tempYPos;
        
        //If the current line is the class type attribute, it
        //saves the type
        if(line.substring(line.indexOf(".") + 1,
            line.indexOf("=")).equals("y"))
        {
            returnYPos = Integer.parseInt(line.substring(line.indexOf("=") + 1,
                line.length()));
        }
        
        return returnYPos;
    }
    
    private BClassDescriptor findLastAttachedClass(String classToAttachTo)
    {
        BClassDescriptor currentClass = findBClassDescriptorInList(classToAttachTo);
        
        //Follows the linked path of associated classes until it gets to a
        //class which does not have an associated class
        while(currentClass.getAssociation() != null)
        {
            currentClass = findBClassDescriptorInList(currentClass.getAssociation());
        }
        
        return currentClass;
    }
    
    private BClassDescriptor findBClassDescriptorInList(String className)
    {
        boolean found = false;
        BClassDescriptor returnClass = null;
        
        //Iterates through all of the positions in the classList
        for(int index = 0; index < classList.size() && !found; index++)
        {
            //If the name of the current BClassDescriptor matches the anme
            //being searched for it stops the loop and returns that BClassDescriptor
            if(classList.get(index).getClassName().equals(className))
            {
                found = true;
                returnClass = classList.get(index);
            }
        }
        
        return returnClass;
    }
    
    /**
     * A nested class that instantiates the different (simple) menus.
     */
    class SimpleAction extends AbstractAction
    {
        private String msgHeader;
        
        public SimpleAction(String menuName, String msg)
        {
            putValue(AbstractAction.NAME, menuName);
            msgHeader = msg;
        }
        public void actionPerformed(ActionEvent anEvent)
        {
            createClassChooserWindow();
        }
    }
}