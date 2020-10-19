# How does this system work?
* This is basic implementation of Jetty Server using Jersey Framework.
* Client can connect to the server on port 8080 (which is configurable).
* For remote bank servers implementation, I have retained spark server framework.
* **When Jetty Server starts ,it automatically starts bank remote-server at defined port 1234 (I would have preferred higher port).**
* Both the servers can be configured to run on different ports, json property file "application-properties.json" can be used for the same.
* Jetty Server (application-server) can also be configured to run on any IP other than loopback/localhost.
* I have assumed that it's not possible that all the banks will have their remote endpoints up and responding always.
* So the philosophy of the solution is when a client invokes /v2/banks/all, system reads the json file for remote bank
* endpoint details and then in-parallel invokes those APIs and aggregates the result.
* Parallel invocation is handled using CompletableFuture.
* If one/few of the calls fails system will still aggregate the result and send the success/partial response.
* System will send 500/502/503/504 if all the requests fail with corresponding status code.
* In real world we should implement acceptable percentage success from remote endpoints and respond to client accordingly.

# How will the system perform as the number of requests per second increases?
* I have configured Jersey framework to use async servlet.
* This should help in increasing the throughput of the system.
* We are also using in-memory cache to avoid remote calls if  the data is already present.
* However, cache values have been configured with TTL, in case of evicted entries we still need to make remote calls.
* HttpClient implementation is also enhanced with retry mechanism (default is 3 times and it is configurable).
* In **real world system we use Resilience4J or Hysterix** like frameworks to handle failure condition on upstream services. 
* I have implemented connection pooling on Http-Client so that we can optimize via connection reuse.
* By default, JVM will start with 1024MB and can go upto 2048MB (that's only heap, actual memory on host would be more than that)
* I have configured G1GC for garbage collection.

* _Disclaimer_ : I have not done standard benchmarking of the system using Gatling or JMeter. 
* Though I was planning  to do so, but time factor pitched in :(

# What documentation, websites, papers, etc did I consult in doing this assignment?
* https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html
* https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/index.html

# Do we have any logging in the system?
* Yes! I have configured SL4J with log4j2, to avoid any issues while writing log file on disk all logs will roll on STDOUT.

# Do we have any Java docs for this ?
* Yes!! Java docs can be found at location /assignment-lineQueue/target/apidocs

#How to build
*In case the machine already has java and maven /assignment-lineQueue/src/main/scripts/build.sh can be used.

# What third-party libraries or other tools does the system use?
* Apache Commons lib.
* Netty
* SnakeYaml
* Log4J2

# How long did you spend on this exercise?
* Around 8 hrs spanning across 3 days.

# How to run the server?
* For simplicity I have generated a shaded/fat/uber jar with all the dependencies and simple (java -jar <assignment-lineQueue-0.0.1-SNAPSHOT-launcher.jar>) command should be good enough to start the server.
* Same has been done in run.sh file apart from specific jvm args like
- -Xms1024m
- -Xmx2048m 
- -XX:+UseG1GC  
- -XX:+HeapDumpOnOutOfMemoryError

# Location where the queue get's persisted in the event of graceful shutdown.
* Queue will be persisted as file in user's home directory with name "lineQueue".
* I have used System.getProperty("user.home") to get the same.
* It is also possible to explicitly mention any working directory as "workingDir" environment variable and it will override default location.

# What happens if system faces issues in persisting and reading from disk.
* Server will ignore issues while reading and writing to disk and will act as pure in-memory variant of the system.

# How was this system tested?
* Considering time in mind, I didn't write functional tests instead used "netcat" tool for all the testing.For eg. on Mac command is like "nc 127.0.0.1 10042"




 
# In case, I would like to run the server using IDE(eclipse/intelliJ)
* Of course it could be done, entry point is com.assignment.lineQueue.server.LineQueueServer.

#What is the location of scripts 
* Scripts can be found at location /assignment-lineQueue/src/main/scripts

#How to bind server other than localhost IP Address
* Update the bind address property in /assignment-lineQueue/src/main/resources/config.yaml
* After change in YAML file, build is needed as it makes it way to shaded jar.
* NOT to mention in case of EC2 or Azure Machine it would be the private IP address of the machine.
* Clients outside that machine should still connect with public IP address which gets Natted to the binded address.


