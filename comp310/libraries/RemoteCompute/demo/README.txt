IMPORTANT: READ *ALL* THE DOCUMENTATION!!

THIS PACKAGE CONTAINS BOTH CLIENT AND ENGINE DEMOS AS EXECUTABLE JAR FILES.  
READ THE PACKAGE DOCUMENTATION *BEFORE* RUNNING THE DEMOS!
 
Please see the Javadocs (in the "Overview" tab, click the package name in the right-hand box)
or see the package-info.java file or simply hold the mouse over the package name in Eclipse.

Note that the demo JAR run/launch configurations are set up to run as if they were regular 
Java programs with the JAR files on the classpath and an explicitly specified main class (as opposed
to using the main class in the JAR's internal manifest).   This enables the app to load class files that
are not in the JAR.   It seems that running the JAR directly no longer (as of 10/2021, Java 16) includes 
the working directory as part of the classpath when directly running a JAR file.

Also, not that the classpath separator is NOT the same between Windows (";") and Mac/Linux (":") machines.
The demos' run configs are set up to pull the classpath separator character (the "path.separator" system property) 
from Eclipse itself and should thus be able to use the correct separator when defining the classpath.

Unfortunately, the classpath is read only once, when the JRE starts and modifying the java.class.path system 
parameter dynamically does NOT affect the classpath being used.

Note: As of Java 18, the demo launch configs now include command line parameter, -Djava.security.manager=allow, 
to enable the deprecated SecurityManager to run.   The parameter must be the first parameter on the command line
in order to be recognized.
 