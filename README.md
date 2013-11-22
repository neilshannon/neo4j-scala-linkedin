neo4j-linkedin
==============

A demo project to import a user's connections into a neo4j graph database via the LinkedIn Connections API.  This project is intended to accompany Neil Shannon's "Neo4j In Practice" presentation.

*Included features:*
- Full OAuth2 authentication with LinkedIn
- Linkedin Connections API usage
- Embedded Neo4j graph database
- Cypher query langage examples
- Embedded H2 database
- Squeryl implementation

Prerequisites
--------------------
- JDK 6+ installed
- sbt 0.12.2+ installed
- LinkedIn API key
- Update application.conf in src/main/resources to reflect your LinkedIn API key

How to Test
--------------------

At the command line, run

    sbt
    test

How to Build and Run
--------------------

You must have sbt 0.12.2+ installed to build this project.

At the command line, run

	sbt
	container:start

Note it's important to run the sbt command on its own first, so the container will not immediately shut down when sbt is finished processing your command.

Point your browser at [localhost:8080](http://localhost:8080/ "Your Machine") and you will be running the application in an embedded Jetty instance. 

How to Build a .WAR
-------------------
At the command line, simply run:

	sbt clean package	


Build Status (provided by [CircleCI](http://circleci.com/ "CircleCI"))
-----------------------------------
![Build Status](https://circleci.com/gh/neilshannon/neo4j-linkedin.png?circle-token=48eab09c392f21b57fef5f79851a756854dd0467)
