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
 
### To use this, just a single statement, you will start getting the notifications!
 
It provides a very flexible and convenient interface to monitor the file system:
 
```java
log.info("Started the job directory watcher service, monitoring: {}", getRootJobDirPath());
FileWatcher watcher = FileWatcher.builder().interval(monitoringInterval)
         .initialScanNotificationRequired(false)
         .build()
         .registerListener(this::validateAndProcess, getRootJobDirPath().toString())
         .start();

.......
.......
watcher.shutdown();
 ```
