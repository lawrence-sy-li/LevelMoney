LevelMoney client

I have implemented the main project assignment along with the --ignore-donuts
functionality.  The other two tasks have not completed due to time constraints.

Prerequisites
- Apache Maven installed on computer
- Java 8 JDK and above installed on computer
- Both Maven and Java 8 are on your PATH

The code is in an Apache Maven project.  To build the project and ultimately
a runnable jar file, do the following:

1.  Execute maven with the install goal in the directory where the pom.xml
is located.  Hopefully, all dependencies will be downloaded without issue,
although I could imagine possible proxy issues:

mvn install

2.  Change to the target directory

3.  Then execute the executable jar file with the following command:

java -jar levelmoney-0.0.1-SNAPSHOT-jar-with-dependencies.jar

You may also execute the --ignore-donuts feature with the following:

java -jar levelmoney-0.0.1-SNAPSHOT-jar-with-dependencies.jar --ignore-donuts

- Lawrence Li