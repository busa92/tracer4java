package com.agapple.tracer4java.dump.instrument;

import com.sun.tools.attach.VirtualMachine;

/**
 * Simple attach-on-demand client tool that loads the given agent into the given Java process.
 * 
 * <pre>
 * docs:  
 *  1. http://blogs.sun.com/sundararajan/entry/retrieving_class_files_from_a
 *  2. http://blogs.sun.com/sundararajan/entry/hotspot_source_serviceability_agent
 * 
 * build step : 
 *  1. javac ClassDumperAgent.java
 *  2. jar cvfm classdumper.jar manifest.mf ClassDumperAgent.class
 *  3. javac -cp $JAVA_HOME/lib/tools.jar Attacher.java
 *  
 * run stemp : 
 *  1. start your target process -- I used java2d demo application in JDK
 *  2. find the process id of your process using "jps" tool
 *  3. java -cp $JAVA_HOME/lib/tools.jar:. Attacher <pid> <full-path-of-classdumper.jar> dumpDir=<dir>,classes=<name-pattern>
 *  
 *  java -cp $JAVA_HOME/lib/tools.jar:./target/classes com.agapple.jvm.instrument.Attacher 31769 /home/ljh/work/code/target/classes/classdumper.jar dumpDir=/tmp classes=sun.reflect.GeneratedMethodAccessor
 * </pre>
 */
public class Attacher {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: java Attach <pid> <agent-jar-full-path> [<agent-args>]");
            System.exit(1);
        }

        // JVM is identified by process id (pid).
        VirtualMachine vm = VirtualMachine.attach(args[0]);

        String agentArgs = (args.length > 2) ? args[2] : null;
        // load a specified agent onto the JVM
        vm.loadAgent(args[1], agentArgs);
    }
}
