import bluej.extensions.*;
import bluej.extensions.event.*;
import bluej.extensions.editor.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClassAttacher extends MenuGenerator {
    private BPackage curPackage;
    private BClass curClass;
    private BObject curObject;
    private BlueJ bluej;
    private ArrayList<BClassDescriptor> classList;

    public ClassAttacher(BlueJ bluej)
    {
        this.bluej = bluej;
        this.classList = new ArrayList<BClassDescriptor>();
    }
    
    public JMenuItem getToolsMenuItem(BPackage aPackage)
    {
        return new JMenuItem(new SimpleAction("Click Tools", "Tools menu:"));
    }

    public JMenuItem getClassMenuItem(BClass aClass) {
        boolean allowAttachOption = false;
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
        
        JMenuItem returnItem;

        for(BClassDescriptor curClassDescriptor : this.classList)
        {
            if(curClassDescriptor.getClassName().equals(aClass.getName()) && curClassDescriptor.isTestClass())
            {
                allowAttachOption = true;
            }
        }
        if(allowAttachOption)
        {
            returnItem = new JMenuItem(new SimpleAction("Attach...", "Attach Test Class To: "));
        }
        else
        {
            returnItem = null;
        }
        
        return returnItem;
    }
    
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
            if (curObject != null)
            {
                curClass = curObject.getBClass();
            }
            if (curClass != null)
            {
                curPackage = curClass.getPackage();
            }
            
            String filePath = curPackage.getDir().getPath().concat("/bluej.pkg");
            
            parsePackageFile(filePath);    
                
            String [] classNames = new String[50];
            int curIndex = 0;
            
            for(BClassDescriptor curClass : this.classList)
            {
                if(!curClass.isTestClass())
                {
                    classNames[curIndex] = curClass.getClassName();
                    curIndex++;
                }
            }
            
            JFrame frame = new JFrame("Attacher");
            String attacherClass = (String) JOptionPane.showInputDialog(frame, "Choose one:", "Select Target", JOptionPane.QUESTION_MESSAGE, null, classNames, classNames[0]);
                
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
            String targetString = "";
            boolean foundTarget = false;
            String filePathDir = curPackage.getDir().getPath();
            
            String filePath = filePathDir.concat("/bluej.pkg");

            File curProjDirectory = curProject.getDir();
            curProject.close();
            FileWriter fStream = new FileWriter(filePath, true);
            
            BClassDescriptor attachedClass = findLastAttachedClass(classToAttachTo);
            
            fStream.append("target" + attachedClass.getTargetNumber() + ".association=" + curClass.getName() + "\n");
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
        String curLine = "";
        int curTargetNumber = 0;
        String curName = null;
        String curAssociation = null;
        boolean isTestClass = false;
        try {
            Scanner scan = new Scanner(packageInfoFile);
            while(scan.hasNext())
            {
                curLine = scan.nextLine();
            
                if((curLine.substring(0, 6).equals("target")))
                {
                    if(curTargetNumber != 0 && Integer.parseInt(curLine.substring(6, curLine.indexOf("."))) != curTargetNumber)
                    {
                        this.classList.add(new BClassDescriptor(curTargetNumber, curName, curAssociation, isTestClass));
                        //classList.add(new BClassDescriptor(curTargetNumber, curName, curAssociation, isTestClass));
                        curName = null;
                        curAssociation = null;
                        isTestClass = false;
                    }
                    curTargetNumber = Integer.parseInt(curLine.substring(6, curLine.indexOf(".")));
                    
                    if(curLine.substring(curLine.indexOf(".") + 1, curLine.indexOf("=")).equals("name"))
                    {
                        curName = curLine.substring(curLine.indexOf("=") + 1, curLine.length());
                    }
                    if(curLine.substring(curLine.indexOf(".") + 1, curLine.indexOf("=")).equals("association"))
                    {
                        curAssociation = curLine.substring(curLine.indexOf("=") + 1, curLine.length());
                    }
                    if(curLine.substring(curLine.indexOf(".") + 1, curLine.indexOf("=")).equals("type"))
                    {
                        isTestClass = curLine.substring(curLine.indexOf("=") + 1, curLine.length()).equals("UnitTestTarget");
                    }
                }
            }
            if(curName != null)
            {
                this.classList.add(new BClassDescriptor(curTargetNumber, curName, curAssociation, isTestClass));
            }
        }
        catch(java.io.FileNotFoundException exc)
        {
            System.out.println("The file could not be found.");
        }
        
    }
    
    private BClassDescriptor findLastAttachedClass(String classToAttachTo)
    {
        BClassDescriptor curClass = findBClassDescriptorInList(classToAttachTo);
        while(curClass.getAssociation() != null)
        {
            curClass = findBClassDescriptorInList(curClass.getAssociation());
        }
        
        return curClass;
    }
    
    private BClassDescriptor findBClassDescriptorInList(String className) {
        boolean found = false;
        BClassDescriptor returnClass = null;
        
        for(int index = 0; index < classList.size() && !found; index++)
        {
            if(classList.get(index).getClassName().equals(className))
            {
                found = true;
                returnClass = classList.get(index);
            }
        }
        
        return returnClass;
    }
    
    // The nested class that instantiates the different (simple) menus.
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