package com.agapple.tracer4java.dump.instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * This is a java.lang.instrument agent to dump .class files from a running Java application.
 * 
 * <pre>
 * docs :
 *  1. http://www.ibm.com/developerworks/cn/java/j-lo-jse61/index.html
 * </pre>
 */
public class ClassDumperAgent implements ClassFileTransformer {

    private static String         dumpDir;
    private static Pattern[]      patterns;
    private static PatternMatcher matcher = new Perl5Matcher(); // 对应的Matcher

    /**
     * java5 style, before main method call
     * 
     * <pre>
     * run step :
     *  1. java -javaagent:jar <full path>
     * </pre>
     * 
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    /**
     * java6 style
     * 
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        parseArgs(agentArgs);
        inst.addTransformer(new ClassDumperAgent(), true);

        // 获取当前已经加载的class，检查下是否匹配
        Class[] classes = inst.getAllLoadedClasses();
        List<Class> candidates = new ArrayList<Class>();
        for (Class c : classes) {
            if (isCandidate(c.getName())) {
                candidates.add(c);
            }
        }
        try {
            if (!candidates.isEmpty()) {
                inst.retransformClasses(candidates.toArray(new Class[0])); // replace
            }
        } catch (UnmodifiableClassException uce) {
        }
    }

    public byte[] transform(ClassLoader loader, String className, Class redefinedClass, ProtectionDomain protDomain,
                            byte[] classBytes) {
        if (isCandidate(className)) {
            dumpClass(className, classBytes);
        }

        return null;
    }

    private static boolean isCandidate(String className) {
        // ignore array classes
        if (className.charAt(0) == '[') {
            return false;
        }

        // convert the class name to external name
        className = className.replace('/', '.');
        // check for name pattern match
        for (int i = 0; i < patterns.length; i++) {
            boolean matched = matcher.matches(className, patterns[i]);
            if (matched) {
                return true;
            }
        }
        return false;
    }

    private static void dumpClass(String className, byte[] classBuf) {
        try {
            // create package directories if needed
            className = className.replace("/", File.separator);
            StringBuilder buf = new StringBuilder();
            buf.append(dumpDir);
            buf.append(File.separatorChar);
            int index = className.lastIndexOf(File.separatorChar);
            if (index != -1) {
                buf.append(className.substring(0, index));
            }
            String dir = buf.toString();
            new File(dir).mkdirs();

            // write .class file
            String fileName = dumpDir + File.separator + className + ".class";
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(classBuf);
            fos.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    // parse agent args of the form arg1=value1,arg2=value2
    private static void parseArgs(String agentArgs) {
        String dumpClasses = ".*";
        if (agentArgs != null) {
            String[] args = agentArgs.split(",");
            for (String arg : args) {
                String[] tmp = arg.split("=");
                if (tmp.length == 2) {
                    String name = tmp[0];
                    String value = tmp[1];
                    if (name.equals("dumpDir")) {
                        dumpDir = value;
                    } else if (name.equals("dumpClasses")) {
                        dumpClasses = value;
                    }
                }
            }
        }

        if (dumpDir == null) {
            dumpDir = ".";
        }

        if (patterns == null) {
            String[] classes = dumpClasses.split(",");
            Perl5Compiler compiler = new Perl5Compiler();
            patterns = new Pattern[classes.length];
            for (int i = 0; i < classes.length; i++) {
                try {
                    patterns[i] = compiler.compile(classes[i], Perl5Compiler.READ_ONLY_MASK);
                } catch (MalformedPatternException ex) {
                }
            }
        }
    }
}
