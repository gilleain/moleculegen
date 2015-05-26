package branch;

import java.util.Stack;

public class App {
    
    private static long start(boolean shouldTime) {
        if (shouldTime) {
            return System.nanoTime();
        } else {
            return 0;
        }
    }
    
    private static void time(long start, boolean shouldTime) {
        if (shouldTime) {
            long timeInNs = (System.nanoTime() - start);
            long timeInMs = timeInNs / 1000000;
            long timeInS = timeInNs / 1000000000;
            System.out.println("Time(ms) : [" + timeInMs + "]");
            System.out.println("Time(s) : [" + timeInS + "]");
        }
    }
    
    public static void count(String formula, boolean shouldTime) {
        long start = start(shouldTime);
        CountingHandler handler = new CountingHandler();
        AtomGenerator generator = new AtomGenerator(formula, handler);
        generator.run();
        System.out.println(handler.getCount());
        time(start, shouldTime);
    }
    
    public static void print(String formula, boolean showCount, boolean shouldTime) {
        long start = start(shouldTime);
        PrintStreamHandler handler = new PrintStreamHandler(System.out, showCount);
        AtomGenerator generator = new AtomGenerator(formula, handler);
        generator.run();
        time(start, shouldTime);
    }
    
    public static void main(String[] args) {
        Stack<String> argStack = new Stack<String>();
        for (String arg : args) {
            argStack.add(arg);
        }
        String formula = argStack.pop();
        
        boolean shouldPrint = false;
        boolean shouldNumber = false;
        boolean shouldTime = false;
        
        while (argStack.size() > 0) {
            String arg = argStack.pop();
            if (arg.equals("-p")) {
                shouldPrint = true;
            } else if (args.equals("-n")) {
                shouldNumber = true;
            } else if (arg.equals("-t")) {
                shouldTime = true;
            }
        }
        
        if (shouldPrint) {
            print(formula, shouldNumber, shouldTime);
        } else {
            count(formula, shouldTime);
        }
    }

}
