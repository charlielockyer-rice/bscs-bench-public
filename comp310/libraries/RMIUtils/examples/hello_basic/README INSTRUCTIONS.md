# Do NOT modify any code in this package!   To make changes, copy the package to a location outside of the `provided` package and edit that code.

## You will need to create new launch config files so that they run your modified code.  Don't forget to set the working directory to the `bin` folder and to add `-Djava.security.manager=allow` as a VM Argument!

## Hello_basic Demo
A simple, bare-bones, no-GUI demo of an RMI call from a client to a server.

To run the demo:

## SERVER:  
Highlight the server launch config file and click the green Run icon.   The server must be started BEFORE the client!

The server will print "Server ready" to the server's console when it is ready to receive RMI calls from a remote client.

The server, in red text that unfortunately looks like an error output, will print information about every RMI transaction that occurs.

Use the red Stop icon on the console to stop the server.  *Be sure to stop ALL running instances of the server!*  (Click the `XX` icon on the console to close all terminated consoles to find any ones that are still running.


## CLIENT
Highlight the client launch config file and click the green Run icon.

The client app runs once, printing the response received back from the remote RMI Server object on the server onto the client's console.

*The client app must be manually terminated (click the red Stop icon in the console pane) before it can be run again.*  This is needed because there are underlying RMI components still running, specifically the class file server.   The client can be made to automatically shutdown by adding the line `rmiUtils.stopRMI();` to the end of the `Client.run()` method.  However, this has the disadvantage of causing the server console pane to display instead of the client console pane after the client terminates, making it more difficult to see the results from the client. 

By default, the client connects to `localhost`.  To connect to a remote server, type its IP address into the `Arguments/Program Arguments` panel of the client's launch configuration.

## Notes

The provided launch config files are slightly different than those one would typically make.  This is because the typical launch config file is specific to the project and module name and is thus not usable as part of a library used in many different projects and modules.   

After being run, these launch configs will also not appear on the usual list of run/launch configurations.  Instead, they will appear under the "External Tools Configurations" (green Run with the red toolbox icon).
  
