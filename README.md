neo4j-linkedin (deprecated)
===========================

*LinkedIn has turned off public access to the API features necessary for this project and made them accessible to paid partners only.*

This project allows a group of people in a room to log in to their LinkedIn accounts and determine how everyone
in the room is connected to one another.  Authentication occurs with LinkedIn via OAuth2.  The connections are
fetched for each user via the LinkedIn Connections API and loaded into a private graph database.  As each user
logs in, the connections are formed between them and other users (if they know each other on LinkedIn) in the private
graph database.  Eventually the degree of connectedness on different levels (1st degree - direct connection, 2nd degree
friend-of-a-friend connection, 3rd degree - friend reachable from friend's network) can be displayed to the logged
in user.

*Included features:*
- Full OAuth2 authentication with LinkedIn
- LinkedIn Connections API usage
- Embedded Neo4j graph database
- Cypher query langage examples
- Embedded H2 SQL database
- Squeryl implementation
- Dispatch HTTP client to make API requests
- unfiltered HTTP routing for the application
- Jade templating
- specs2 automated tests
- Lift json parsing

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
