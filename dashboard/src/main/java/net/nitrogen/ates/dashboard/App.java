package net.nitrogen.ates.dashboard;

import com.jfinal.core.JFinal;

public class App {
    public static void main(String[] args) {
        JFinal.start(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
    }

    public static void stop(String[] args) {
        JFinal.stop();
    }
}
