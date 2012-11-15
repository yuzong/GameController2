This is the GameController2 made by team B-Human for the RoboCup SPL.

########## 1. Building from Source ##########

To build it from the source code you may use Apache Ant.
There are three ant targets:

- clean
	cleans up the project folder

- compile
	compiles the code and stores files in /build/classes
	
- distribute
	creates a jar package and stores it in /build/jar

########## 2. Executing the Jar ##########

After executing the target distribute just run java -jar GameController2.jar	

########## 3. Shortcuts ##########

While the GameController2 is running, you may use the following keys on the keyboard instead of pushing buttons:

Esc		- press it twice to close the GameController2
Delete		- toggle test-mode (everything is legal, every button is visible and enabled)
Backspace	- undo last action

1-5		- player 1-5 on the left side
6-0		- player 1-5 on the right side

The following keys are mapped on the keyboard nearly equal to the buttons on the GUI.

Q	- goal left side
I	- goal right side
A	- out by left side
K	- out by right side
Y	- time-out left side
,	- time-out right side

E	- initial
R	- ready
T	- set
Z	- play
There actually is no key for finish because pushing it by mistake would be troublesome.

D	- pushing
F	- leaving the field
G	- fallen robot
H	- inactive robot
C	- illegal defender
V	- ball holding
B	- playing with hands
N	- request for pickup

########## 4. Misc ##########

The packages which the GameController2 sends and may receive from robots are defined in RoboCupGameControlData.h. We did not change this and neither are we responsible for it.