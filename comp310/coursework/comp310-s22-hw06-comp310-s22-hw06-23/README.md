# HW6

**Please see the assignment instructions in Canvas.** 

## List ALL Partner Names and NetIDs:
1. Son Nguyen - spn1
2. Charlie Lockyer - cwl6

## Notes to Staff:

** Descriptions of our BallWorld Strategies **

Update Strategies:
- Color: strategy to change balls' color as it moves through the canvas. It is essentially a StraightBall that changes color periodically.
- Gravity: strategy to models how a ball bounces on earth with a fixed acceleration of 9 units per frame update in the y direction.
- Jittery: strategy that makes the ball vibrate horizontally.
- Shrinking: strategy to shrink the balls everytime it touches the wall by a random amount.
- Splitting: strategy to periodically splits balls into two halfs with the second ball moving in the opposite direction. The two balls will continue to periodically split up in the same manner. Once the radius is at or below 5, it is removed from the canvas. This ball models a cell moving in space while splitting.
- Straight: strategy to move straight.
- Teleport: strategy to make the balls teleport to the center of the canvas with new random velocity whenever the ball hits the wall.
- Wandering: strategy to models a drunken person. It randomizes the balls' velocity periodically as it moves through the canvas. If it hits a wall, it will bounce off the wall like a StraightBall.

(Interaction criteria update strategies below)
- Overlap: defines an interaction criteria in which the "context" ball and "other" ball have overlapping radii.
- SameColor: defines an interaction criteria in which in which two balls have similar colors.
- SameSize: defines an interaction criteria in which in which two balls have similar sizes.
- SameSpeed: defines an interaction criteria in which in which two balls have similar speeds.
- SameVelocity: defines an interaction criteria in which in which two balls have similar velocities.


Paint Strategies:
- Ball: Paint a non-affine transform-based ball.
- Earth: Paint an earth image affine transform-based.
- Ellipse:  Paint an affine transformed simple geometric ellipse.
- Moon: Paint an affine transformed of an image of the moon.
- NiceFish: Paint an affine transformed composite shape that stays upright
- Rectangle: Paint an affine transformed rectangle.
- SoccerImage: Paint an affine transformed image of a soccer ball.
- Square: Paint a non-affine transform square.
- Star: Paint a non-affine transform four pointy ends star.
- Sun: Paint a affine transform image of a sun.


Interaction Strategies:
- Collide: Interaction strategy for the ball to elastically collide with another ball.
- Infect: Interaction strategy for the context ball to "infect" other balls (once a given interaction criteria is met between the balls). Infection means changing the other balls' update strategies to the current ball's update strategy.
- Lead: Interaction strategy for creating a "leader" ball that makes other balls follow it at random speed for 50 counts after a given interaction criteria is met.
- Mate: Interaction strategy that, upon interaction, causes the creation of a new ball with a random velocity, which averages the color of the two balls ("context" and "other"), averages the radii of the balls, and combines their Update strategies. Note: this interaction strategy could go wild very quick since child balls combines multiple strategies, so make sure you clear the balls  when too many balls are created to prevent crashing.
- TradeColor: Interaction strategy for the context ball and the interacting ball to trade their colors (whenever a given interaction criteria is met).

Configuration Algos:
- FishWorld: Configure a similar demo to the FishWorld shown in class; fish have a specific probability of changing direction and can kill/follow one another based on dynamic configuration panel
- Kill: modified version of Dr.Wong's kill. The ball type wil kill differently: Gangsters can kill all with thrice the probability and make a noise when it does so. Leader can kill all but with the set probability. Followers and default don't kill. Make sure to combine with a criteria update Strategy and a paint strategy for this to work.
- Lead: different types of ball will lead differently: Followers will follow Leaders and Gangsters. Leaders will follows Gangsters, and Gangsters do not follow any one as they are lawless. No ball of the same type will follow each other, but Follow ball might decides to become a leader for very brief 50 ticks at some random instance. Default ball will not do anything but follows. Make sure to combine with a criteria update Strategy and a paint strategy for this to work.
- SolarSystem: Followers are moons, Leaders are Earths, Gangsters are Suns.  Proper interaction conditions with collide criteria can yield brief orbits and otherwise they bounce off each other.  Interactions can be dynamically configured as described in control panel
- SuckLife: Balls can "suck life" from one another each tick based on their size and ball type. Followers can suck from default balls and other followers who are smaller, Leaders can suck from Followers, and Gangsters can suck from anyone at twice the suck rate.  The suck rate and enable/disable can be adjusted in the dynamic control panel.


## Application Notes:

** Instructions to Run HW6 **
The hw06 package should be located under src folder. There are three separate packages within hw06. The controller package contains the Controller class which has the main method and can be run to see the BallWorld Application. Once run, follow the onscreen tooltips to use the app. In the textbox, make sure that you only type in the name of the specific strategy that you want to create. For example, if you want to create a Color, then type "Color" into the textbox and click "Add to list". The same applies for the Paint and Interaction strategies. Different strategies (of the same type or different types) can be composed. Follow the onscreen instruction from tooltips to combine strategies from top drop list and bottom drop list as well as switch features.


