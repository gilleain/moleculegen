package group.invariant;

public class IntegerInvariant implements Invariant {
    
    private Integer value;
    
    public IntegerInvariant(Integer value) {
        this.value = value;
    }

    @Override
    public int compareTo(Invariant o) {
        return value.compareTo(((IntegerInvariant)o).value);
    }
    
    public int hashCode() {
        return value;
    }
    
    public boolean equals(Object other) {
        return other instanceof IntegerInvariant && 
                value.equals(((IntegerInvariant)other).value);
    }
    
    public String toString() {
        return value.toString();
    }
    
}