@echo off

rem copying log4j.properties to jar
jar uf getpassword-0.0.1-jar-with-dependencies.jar log4j.properties

rem for 2 seconds delay..
timeout /t 2

echo running jar
start javaw -jar getpassword-0.0.1-jar-with-dependencies.jar

exit
