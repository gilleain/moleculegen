package branch;

public class App {
    
    public static void count(String formula) {
        CountingHandler handler = new CountingHandler();
        AtomGenerator generator = new AtomGenerator(formula, handler);
        generator.run();
        System.out.println(handler.getCount());
    }
    
    public static void print(String formula) {
        PrintStreamHandler handler = new PrintStreamHandler(System.out);
        AtomGenerator generator = new AtomGenerator(formula, handler);
        generator.run();
    }
    
    public static void main(String[] args) {
        String formula = args[0];
        if (args.length > 1 && args[1].equals("-p")) {
            print(formula);
        } else {
            count(formula);
        }
    }

}
