# Class Variant - Using Bytecode manipulation 

This project contains a Java agent and examples of bytecode manipulation with Javassist.

## To Build

```
$ # From the root dir
$ mvn package
```

## Run Global version

```
$ # From the root dir
$ java -javaagent:agent/target/agent-0.1-SNAPSHOT.jar -jar other/target/other-0.1-SNAPSHOT.jar
```

## Run Variant version

```
$ # From the root dir
$ java -javaagent:agent/target/agent-0.1-SNAPSHOT.jar=<variant> -jar other/target/other-0.1-SNAPSHOT.jar
$ # Example: Danang
$ java -javaagent:agent/target/agent-0.1-SNAPSHOT.jar=Danang -jar other/target/other-0.1-SNAPSHOT.jar
```
