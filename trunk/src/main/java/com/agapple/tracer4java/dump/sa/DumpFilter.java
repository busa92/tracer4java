/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.agapple.tracer4java.dump.sa;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

/**
 * need import jdk.lib/sa-jdi.jar , tools.jar
 * 
 * <pre>
 * 读取-DdumpClasses=xxx,xxx参数
 * </pre>
 * 
 * @author jianghang 2011-4-18 上午11:10:40
 */
public class DumpFilter implements ClassFilter {

    private Pattern[]      patterns;
    private PatternMatcher matcher = new Perl5Matcher(); // 对应的Matcher

    public DumpFilter(){
        String dumpClasses = System.getProperty("dumpClasses", ".*");
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

    @Override
    public boolean canInclude(InstanceKlass kls) {
        String klassName = kls.getName().asString();
        for (int i = 0; i < this.patterns.length; i++) {
            boolean matched = this.matcher.matches(klassName, this.patterns[i]);
            if (matched) {
                return true;
            }
        }
        return false;
    }
}
