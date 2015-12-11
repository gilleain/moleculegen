package test.branch;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import branch.AtomGenerator;
import branch.CountingHandler;

/**
 * Test from a file list of formula-count pairs.
 * 
 * @author maclean
 *
 */
public class TestFromFile {
    
    public final static String filepath = "testformulae.txt";
    
    @Test
    public void runTest() throws IOException {
        Map<String, Integer> formulaeCounts = readFile(filepath);
        Map<String, Integer> fails = new HashMap<String, Integer>();
        for (String formula : formulaeCounts.keySet()) {
            int observedCount = count(formula);
            int expectedCount = formulaeCounts.get(formula);
            if (observedCount != expectedCount) {
                fails.put(formula, expectedCount - observedCount);
            } else {
                System.out.println(formula + " " + expectedCount + " = " + observedCount);
            }
        }
        assertTrue("Fail " + failsString(fails), fails.isEmpty());
    }
    
    private String failsString(Map<String, Integer> fails) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : fails.keySet()) {
            stringBuilder.append(key).append(" ").append(fails.get(key)).append("\n");
        }
        return stringBuilder.toString();
    }
    
    private int count(String elementFormula) {
        CountingHandler handler = new CountingHandler();
        AtomGenerator gen = new AtomGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount();
    }

    private Map<String, Integer> readFile(String filepath) throws IOException {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line, counts);
        }
        reader.close();
        return counts;
    }

    private void parseLine(String line, Map<String, Integer> counts) {
        String[] parts = line.split("\\s+");
        if (parts.length == 0) {
            parts = line.split(",");
        }
        int count = 0;
        String formula = "";
        for (String part : parts) {
            if (part.trim().length() == 0) continue;
            try {
                count = Integer.parseInt(part.trim());
            } catch (NumberFormatException n) {
                formula = part.trim();
            }
        }
//        System.out.println(Arrays.toString(parts));
        counts.put(formula, count);
    }

}
