package com.agapple.tracer4java.dump;

public class InstrumentServer {

    private String ip;
    private String port;

    public InstrumentServer(String ip, String port){
        this.ip = ip;
        this.port = port;
    }

    public int start(String ip, String port) {
        System.out.println("start at : " + ip + ":" + port);
        return 123;
    }

    public void stop() {
        this.ip = null;
        this.port = null;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static void main(String args[]) throws Exception {
        for (int i = 0; i < 1000; i++) {
            InstrumentServer server = new InstrumentServer(String.valueOf(i), String.valueOf(i));
            server.start(String.valueOf(i), String.valueOf(i));
            Thread.sleep(1000);
            server.stop();
        }
    }

}
