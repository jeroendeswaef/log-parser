log-parser
==========

Common log format (nginx) parser in Java.

This project takes access.log files from nginx, parses them, and outputs JSON.

The log format can easily be configured.

Usage
-----

    java -jar target/parseweblog-[version]-jar-with-dependencies.jar --logfile /tmp/access.log
   
For example, a log line like this:
    74.86.158.106 - - [24/Mar/2014:06:33:04 +0100] "GET /en HTTP/1.1" 200 10589 "-" "Mozilla/5.0+(compatible; UptimeRobot/2.0; http://www.uptimerobot.com/)" "-"

will output JSON like this:
    {
        "status": "200",
        "remote_user": "-",
        "body_bytes_sent": "10589",
        "request": {
            "http": "HTTP/1.1",
            "method": "GET",
            "url": "/en"
        },
        "remote_addr": "74.86.158.106",
        "http_user_agent": "Mozilla/5.0+(compatible; UptimeRobot/2.0; http://www.uptimerobot.com/)",
        "time_local": "1395639184000",
        "http_referer": "-"
    }

Building
--------

Creating a jar that contains all dependencies:

    mvn compile assembly:single
  
  
