# Do NOT modify any code in this package!   To make changes, copy the package to a location outside of the `provided` package and edit that code.

## You will need to create new launch config files so that they run your modified code.  Don't forget to set the working directory to the `bin` folder and to add `-Djava.security.manager=allow` as a VM Argument!

# You must be on the Rice Owls network for this demo to run!!

## Hello_discovery Demo
A simple GUI demo of an RMI call from a client to a server that can connect either directly or via the Discovery Server.  The demo also uses AppConfigs to enable multiple instances of the client and/or server to run simultaneously.

To run the demo:

## SERVER:  
Highlight the server launch config file and click the green Run icon.   The server must be started BEFORE the client!

A pop-up dialog will appear requesting that you choose one of 3 possible app configs that use different sets of ports and bound names.  
Multiple instances of the server can be run simultaneously so long as different port sets are selected from any other server or client instances.

To connect to the Discovery server:
1. Enter any desired "Category" name (use something unique to your team) in the `Discovery Server/Category` textfield.
2. Click the "Connect" button.   After a short delay, the corresponding entry should display in the `Registered Endpoints` panel.

Use the "Quit" button to exit the application.

The server's display area will indicate when it's `IHello.sayHello()` method is invoked.
The server, in red text that unfortunately looks like an error output, will print information about every RMI transaction that occurs to the server console.


*Be sure to stop ALL running instances of the server!*  (Click the `XX` icon on the console to close all terminated consoles to find any ones that are still running.

## CLIENT
Highlight the client launch config file and click the green Run icon. 

A pop-up dialog will appear requesting that you choose one of 3 possible app configs that use different sets of ports.  
Multiple instances of the client can be run simultaneously so long as different port sets are selected from any other server or client instances.

To connect directly to a server:  
1. Type in the IP address of the desired machine, or `localhost` for the local machine.
2. Click the "Connect" button.

To connect via the Discovery Server:
1. Enter the "Category" name used by the desired server in the `Discovery Server/Category` textfield.
2. Select the the desired endpoint from the displayed list of `Registed Endpoints`.
3. Click the "Get Selected Endpoint" button.

Use the "Quit" button to exit the application.

The remote RMI Server object's (on the server) `IHello.sayHello()` method is called immediately upon connection and whenever the "Say Hello" button is clicked.   The returned response is displayed in the main text area.

*Be sure to stop ALL running instances of the client!*  (Click the `XX` icon on the console to close all terminated consoles to find any ones that are still running.


## Notes

The provided launch config files are slightly different than those one would typically make.  This is because the typical launch config file is specific to the project and module name and is thus not usable as part of a library used in many different projects and modules.   

After being run, these launch configs will also not appear on the usual list of run/launch configurations.  Instead, they will appear under the "External Tools Configurations" (green Run with the red toolbox icon).
  
 