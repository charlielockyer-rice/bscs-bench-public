# RemoteCompute
API and support code for remote computations

## Running the demo JARs in provided.remoteCompute.demo ##


This package contains runnable demos for the project (executable JAR files) -- See the full package description for the demos' operating directions.

Note that the JAR files are not listed in the Javadocs.  Please go to the actual 
<code>provided.demo</code> package in the codebase to find them.   



### _MAKE SURE ALL THE REQUIRED PORTS ON YOUR COMPUTER ARE OPEN FIRST!!_ ###

Note that the JAR files do NOT contain any code for any tasks.   All tasks are dynamically loaded, where 
the demos assume that the desired ITask implementation classes all have a public static field 
called <code>FACTORY</code> that is am ITaskFactory instance that will instantiate that <code>ITask</code> implementation.


Some tasks in the provided code are pre-loaded for your convenience.   
The demos are capable of loading and executing student-written tasks as well
if the task class contains a public static FACTORY field as described above. 

The demos should be capable of inter-operating with any student-written client or engine solutions and this can used for testing student solutions.

Student <code>ITask</code> implementations can be written separately from the student client or engine solutions and tested with the demo client and engine (Note that the demo client requires that an <code>ITask</code> be instantiatable by an <code>ITaskFactory</code> and that the <code>ITaskFactory</code>'s <code>make()</code> method 
needs no more than a single String parameter.)


### How to Run the Demos: ###

#### Starting the demos: ####

* [***RECOMMENDED***] From inside of Eclipse:  
	1. Select the desired launch (run) configuration file in the "demo" folder.
	2. Click the green "Run" icon in Eclipse tool bar.   
			(Note: Since these are run configurations for JAR  files, after they are run, the run configurations will appear 
			under the "External Tools" run configurations (green run button with red briefcase icon), 
			NOT the regular run configurations list.)
	3. If prompted, be sure to select the IP address that corresponds to an on-campus IP address, typically a 10.X.Y.Z or 168.5.X.Y type address.    
		
	

* From the command line:
	1. Navigate to the project's "bin" folder
	2. Run the following command:  <code>java -jar provided/remoteCompute/demo/jarname.jar</code>    
   				where "<code>jarname.jar</code>" is either "<code>engine_demo.jar</code>" or "<code>client_demo.jar</code>"</li>
	
* From a file browser:  Do **NOT** run the JAR file in the "demo" folder from a file browser because the default package will not be correct.
	* Work-around if you absolutely need to run from a file browser: Copy the desired JAR file to the "<code>bin</code>" folder and run it from there.


Start a client and an engine on the same or different computers. Note that either client or engine could be a student solution.


**Connecting the client to an engine:**
1. On the client, enter the IP address of the engine computer 
	(displayed on the engine GUI).</li>
	1. Click the "Connect" button.</li>
	1. Both the client and the engine will display messages from  the other as well as connection status messages.</li>



**Testing connectivity:**

* From the client to the engine:
	1. On the client, type a message into the 
			"<code>Send msg to remote host's view</code>" text field and hit "Enter".
	2.  The message should appear on the engine's GUI 
			plus be echoed on the client's GUI.

* From the engine to the client:
	1. On the client, type a message into the 
			"<code>Send msg to remote client's view</code>" text field and hit "Enter".</li>
	2. The message should appear on the client's GUI plus be echoed on the engine's GUI.</li>



### Executing Tasks ###

**Adding new tasks:**

1. On the client, type in the fully-qualified classname of a desired 
	<code>ITask</code> implementation. The demo client assumes that the desired <code>ITask</code> class 
	has a public static field called "<code>FACTORY</code>" that is the singleton instance 
	of the <code>ITaskFactory</code> for that <code>ITask</code> implementation.</li>
2. Click the "<code>Add to lists</code>" button and the <code>ITaskFactory</code> will appear on the 
	two drop-lists.

**Running a task:**
1. Select the desired task to run from the top drop list.
2. Type in an appropriate parameter for constructing the task.
3. Click the "Run Task" button. 
4. Task results will appear on both the client and engine GUI's.
	* The engine will display the raw task results.
	* The client will display the task results as formatted by the task's 
	<code>ITaskResultFormatter</code> object.
  
**Combine tasks:**

_Note: By default, the client demo only utilizes the composite task included inside of the client demo JAR file, 
**NOT** the student-written version. In such, the supplied composite task can only be used when running the supplied engine demo._

1. Select the desired tasks from the top and bottom drop lists.
2. Click the "<code>Combine Tasks</code>" button.  
3. The combined task will appear on the drop lists. Combined tasks are true binary composite tasks where the composite ("<code>staff.client.model.task.MultiTask</code>") and the composees are all transmitted to the engine for execution as a single task. When the <code>MultiTask</code> runs, it indicates when the individual composees are run.

**Using student-written composite tasks:**

The demo client includes the ability to replace the composite task it uses with a specified student-written version. 

***Students are NOT required to implement this feature!  This is simple a convenience added to the demo to facilitate testing of student code!***

1. Enter the fully-qualifed classname (i.e. including the entire package names) for the desired *<code>ITaskFactory</code>* corresponding to the desired composite task.
	* Note: the "<code>$</code>" in the name of the default composite task <code>ITaskFactory</code> shown is due to the fact that the <code>ITaskFactory</code> is declared as a <code>public static</code> nested class of the default <code>MultiTask</code> class.   Students are NOT required to implement their task factories in this manner!
2. Click the <code>Change Composite Task Factory</code> button to set the application to use the new composite task type.



### Quitting the Demos ###
 
Both the client and the engine have "<code>Quit</code>" buttons that will gracefully shut down their systems.    Please use these quit buttons rather than simply killing the apps to avoid issues with phantom processes that could still be running afterwards.
 

