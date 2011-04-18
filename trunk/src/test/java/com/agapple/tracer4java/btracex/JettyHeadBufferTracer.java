package com.agapple.tracer4java.btracex;

import static com.sun.btrace.BTraceUtils.field;
import static com.sun.btrace.BTraceUtils.get;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.str;
import static com.sun.btrace.BTraceUtils.strcat;

import java.lang.reflect.Field;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Self;

/**
 * @author jianghang 2011-4-8
 */

@BTrace
public class JettyHeadBufferTracer {

    @OnMethod(clazz = "org.eclipse.jetty.http.HttpBuffers", method = "/.*get.*Buffers/", location = @Location(value = Kind.ENTRY))
    public static void bufferMonitor(@Self Object self) {
        Field requestBuffersField = field("org.eclipse.jetty.http.HttpBuffers", "_requestBuffers");
        Field responseBuffersField = field("org.eclipse.jetty.http.HttpBuffers", "_responseBuffers");

        Field bufferSizeField = field("org.eclipse.jetty.io.ThreadLocalBuffers", "_bufferSize");
        Field headerSizeField = field("org.eclipse.jetty.io.ThreadLocalBuffers", "_headerSize");
        Object requestBuffers = get(requestBuffersField, self);
        int requestBufferSize = (Integer) get(bufferSizeField, requestBuffers);
        int requestHeaderSize = (Integer) get(headerSizeField, requestBuffers);
        Object responseBuffers = get(responseBuffersField, self);
        int responseBufferSize = (Integer) get(bufferSizeField, responseBuffers);
        int responseHeaderSize = (Integer) get(headerSizeField, responseBuffers);

        String str = "requestBufferSize" + str(requestBufferSize);
        println(strcat(strcat(strcat("requestBufferSize : ", str(requestBufferSize)), " requestHeaderSize : "),
                       str(requestHeaderSize)));
        println(strcat(strcat(strcat("responseBufferSize : ", str(responseBufferSize)), " responseHeaderSize : "),
                       str(responseHeaderSize)));
    }
}
