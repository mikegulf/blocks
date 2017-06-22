Open comand prompt and change directory to where the jar file is located. Use the play or replay command. 
For replay there is an additonal argument which is the directory that the files will be loaded from.

Play
java -jar blocks.jar <start level> false 

java -jar blocks.jar 0 false


Replay
java -jar blocks.jar <start level> true <speed> <directory name>

java -jar blocks.jar 0 true 500 1497456481869



<start level> - starting level -> should start with 0

false for play
true for replay

<speed> - time in milliseconds between moves during replay

<directory name> - the directory (created when user plays the game - time in milliseconds)