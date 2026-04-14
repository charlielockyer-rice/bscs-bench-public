# Simple Extended Visitors Demo
Demo of a simple extended visitor system.

## Run the demo: <code>provided.simpleExtVisitorsDemo.demo.controller.DemoController</code>.

## To Make a New Host: 

1. Type in a unique identifier in the <code>ID</code> textfield
2. Click the <code>Make Host</code> button.   The new host will appear in the drop list.

## To Make a New Visitor:  
1. Type in a unique name in the <code>Make Visitor with name</code> textfield.
2. Click the <code>Make Visitor</code> with name button.   The new visitor will appear in the drop list.    The new visitor will only have a default case defined.

## To Add or Replace a Command in a Visitor:  

1. Type the desired host ID to be associated with the new command in the <code>ID</code> textfield.
2. From the visitor drop list, select the desired visitor in which to add/replace the command.
3. In the <code>Make Cmd for Visitor for ID</code> textfield, type in the desired result for running the new command on the given host.
4. Click the <code>Make Cmd for Visitor for ID</code> button to either add the new command to the selected visitor or to replace any existing command in that visitor associated with the given host ID.
 

##Be sure to try all the multitude of permutations that are possible with this simple demo!

Note:  This demo allows run-time modifications of the visitor algorithms for illustration purposes.   In a real-life situation, where the possibility of malicious hacking exists, this feature might be removed to increase the security of the system.  The ability to change the commands at run-time is not part of the fundamental notions of the extended visitor and is something that one would only include in scenarios where dynamic re-programming of the system is required.