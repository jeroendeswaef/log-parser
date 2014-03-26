log-parser
==========

Common log format (nginx) parser in Java.

This project takes access.log files from nginx, parses them, and outputs JSON.

The log format can easily be configured.

Usage
-----

    java -jar target/parseweblog-[version]-jar-with-dependencies.jar --logfile /tmp/access.log
   

Building
--------

Creating a jar that contains all dependencies:

    mvn compile assembly:single
  
  
