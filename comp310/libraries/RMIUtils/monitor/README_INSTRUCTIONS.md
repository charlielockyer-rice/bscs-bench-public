# RMI Registry Monitoring Application

This application serves two main purposes:

1. Displays the real-time contents of the local Registry (bound names and bound objects).  This helps verify that the contents of the Registry are as expected at any moment.

2. If started *before* any other RMI application, the monitoring app will "own" the RMI Registry process, enabling all subsequently started RMI applications to quit without disturbing the Registry process.
 
## Running the Application

**Run the RMI Monitoring App BEFORE any other RMI application to ensure that it "owns" the Registry process.**
The monitoring app will still run perfectly well if not the first RMI application started, it will just not own the RMI Registry process.  

The first-started RMI application always owns the RMI Registry process and will kill the RMI Registry when it exits. Starting the RMI Registry Monitoring app first will enable all subsequently started apps to quit without disturbing the RMI Registry.   In this case, the RMI Registry will be terminated when the monitoring app exits.   

**Launching the monitoring application:**
1. Highlight the `monitor.App.launch` file and click the green Run button on the Eclipse icon bar.

A frame will appear showing the current contents of the RMI Registry will be displayed.   The displayed table displays every bound name and its associated bound object.   The contents of this display are updated once a second to show newly bound items and when items are unbound.

Note: The launched application will not appear on Eclipse's usual "Run Configurations" drop list but rather on the "External Tools Configurations" (Green arrow with red toolbox icon) drop list.  This is because of how the launch configuration is written to be project-name independent.


## Do NOT modify any code in this package!   To make changes, copy the package to a location outside of the `provided` package and edit that code.

### If you copy and modify this code, you will need to create new launch config files so that they run your modified code.  Don't forget to set the working directory to the `bin` folder and to add `-Djava.security.manager=allow` as a VM Argument!

