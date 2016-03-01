package group.invariant;

import java.util.Arrays;

public class IntegerListInvariant implements Invariant {
    
    private int[] values;
    
    public IntegerListInvariant(int[] values) {
        this.values = values;
    }

    @Override
    public int compareTo(Invariant o) {
        int[] other = ((IntegerListInvariant)o).values;
        for (int index = 0; index < values.length; index++) {
            if (values[index] > other[index]) {
                return -1;
            } else if (values[index] < other[index]) {
                return 1;
            } else {
                continue;
            }
        }
        return 0;
    }
    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
    
    public boolean equals(Object other) {
        return other instanceof IntegerListInvariant
                && Arrays.equals(values, ((IntegerListInvariant)other).values);
    }
    
    public String toString() {
        return Arrays.toString(values);
    }
    
}
