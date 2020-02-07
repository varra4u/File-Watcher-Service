# File-Watcher-Service
File Watcher Service, that monitors the file system and notify the listeners.

#### Maven artifact details:
 ```xml 
 <!-- https://mvnrepository.com/artifact/com.github.varra4u/file-watcher-service -->
 <dependency>
     <groupId>com.github.varra4u</groupId>
     <artifactId>file-watcher-service</artifactId>
     <version>1.0.0</version>
 </dependency>
 ```
 
#
#####Dev
Need to run `mvn clean package install` from cmd prompt to to push the artifacts to maven.
 
 
 ### Getting a property (could be environment, properties files or Spring Context)
 
 It provides a very flexible and convenient interface to monitor the file system:
 
 ```java
 log.info("Started the job directory watcher service, monitoring: "+ getRootJobDirPath());
             FileWatcher.builder().interval(monitoringInterval)
                     .initialScanNotificationRequired(false)
                     .build()
                     .registerListener(this::validateAndProcess, getRootJobDirPath().toString())
                     .start();
 ```