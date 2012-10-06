package group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A partition of a set of integers, such as the discrete partition {{1}, {2},
 * {3}, {4}} or the unit partition {{1, 2, 3, 4}} or an intermediate like {{1,
 * 2}, {3, 4}}.
 * 
 * @author maclean
 * @cdk.module group
 */
public class Partition {

    private List<SortedSet<Integer>> cells;
    
    /**
     * Creates a new, empty partition with no cells.
     */
    public Partition() {
        this.cells = new ArrayList<SortedSet<Integer>>();
    }
    
    /**
     * XXX - isn't this confusing a partition of a set with a partition of an integer!?
     * @param parts
     */
    public Partition(int[] parts) {
        this();
        for (int part : parts) {
            addCell(part);
        }
    }
    
    /**
     * Copy constructor to make one partition from another.
     * 
     * @param other the partition to copy
     */
    public Partition(Partition other) {
        this();
        for (SortedSet<Integer> block : other.cells) {
            this.cells.add(new TreeSet<Integer>(block));
        }
    }
    
    /**
     * Create a unit partition - in other words, the coarsest possible partition
     * where all the elements are in one cell.
     * 
     * @param size the number of elements
     * @return a new Partition with one cell containing all the elements
     */
    public static Partition unit(int size) {
        Partition unit = new Partition();
        unit.cells.add(new TreeSet<Integer>());
        for (int i = 0; i < size; i++) {
            unit.cells.get(0).add(i);
        }
        return unit;
    }
    
    /**
     * Gets the size of the partition, in terms of the number of cells.
     * 
     * @return the number of cells in the partition
     */
    public int size() {
        return this.cells.size();
    }
    
    /**
     * The total size, as the sums of the sizes of the cells.
     * 
     * @return the number of elements in all cells
     */
    public int numberOfElements() {
        int n = 0;
        for (SortedSet<Integer> cell : cells) {
            n += cell.size();
        }
        return n;
    }
    
    /**
     * Checks that all the cells are singletons - that is, they only have one
     * element. A discrete partition is equivalent to a permutation.
     * 
     * @return true if all the cells are discrete
     */
    public boolean isDiscrete() {
        for (SortedSet<Integer> cell : cells) {
            if (cell.size() == 1) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Converts the whole partition into a permutation.
     * 
     * @return the partition as a permutation
     */
    public Permutation toPermutation() {
        Permutation p = new Permutation(this.size());
        for (int i = 0; i < this.size(); i++) {
            p.set(i, this.cells.get(i).first());
        }
        return p;
    }
    
    public boolean inOrder() {
        SortedSet<Integer> prev = null;
        for (SortedSet<Integer> cell : cells) {
            if (prev == null) {
                prev = cell;
            } else {
                int first = cell.first();
                int last = cell.last();
                if (first > prev.first() && last > prev.last()) {
                    prev = cell;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Gets the first element in the specified cell.
     * 
     * @param cellIndex the cell to use
     * @return the first element in this cell
     */
    public int getFirstInCell(int cellIndex) {
        return this.cells.get(cellIndex).first();
    }
    
    /**
     * Gets the cell at this index.
     * 
     * @param cellIndex the index of the cell to return
     * @return the cell at this index
     */
    public SortedSet<Integer> getCell(int cellIndex) {
        return this.cells.get(cellIndex);
    }

    /**
     * Splits this partition by taking the cell at cellIndex and making two
     * new cells - the first with the singleton splitElement and the second
     * with the rest of the elements from that cell.
     * 
     * @param cellIndex the index of the cell to split on
     * @param splitElement the element to put in its own cell
     * @return a new (finer) Partition
     */
    public Partition splitBefore(int cellIndex, int splitElement) {
        Partition r = new Partition();
        // copy the cells up to cellIndex
        for (int j = 0; j < cellIndex; j++) {
            r.addCell(this.copyBlock(j));
        }
        
        // split the block at block index
        r.addSingletonCell(splitElement);
        SortedSet<Integer> splitBlock = this.copyBlock(cellIndex);
        splitBlock.remove(splitElement);
        r.addCell(splitBlock);
        
        // copy the blocks after blockIndex, shuffled up by one
        for (int j = cellIndex + 1; j < this.size(); j++) {
            r.addCell(this.copyBlock(j));
        }
        return r;
    }
    
    /**
     * Splits this partition by taking the cell at cellIndex and making two
     * new cells - the first with the the rest of the elements from that cell
     * and the second with the singleton splitElement. 
     * 
     * @param cellIndex the index of the cell to split on
     * @param splitElement the element to put in its own cell
     * @return a new (finer) Partition
     */
    public Partition splitAfter(int cellIndex, int splitElement) {
        Partition r = new Partition();
        // copy the blocks up to blockIndex
        for (int j = 0; j < cellIndex; j++) {
            r.addCell(this.copyBlock(j));
        }
        
        // split the block at block index
        SortedSet<Integer> splitBlock = this.copyBlock(cellIndex);
        splitBlock.remove(splitElement);
        r.addCell(splitBlock);
        r.addSingletonCell(splitElement);
        
        // copy the blocks after blockIndex, shuffled up by one
        for (int j = cellIndex + 1; j < this.size(); j++) {
            r.addCell(this.copyBlock(j));
        }
        return r;
    }
    
    
    /**
     * Fill the elements of the permutation from the first element of each
     * cell, up to the point <code>upTo</code>.
     * 
     * @param permutation the permutation to fill with elements
     * @param upTo the point to stop at
     */
    public void setAsPermutation(Permutation permutation, int upTo) {
        for (int i = 0; i < upTo; i++) {
            permutation.set(i, this.cells.get(i).first());
        }
    }
    
    /**
     * Check to see if the cell at <code>cellIndex</code> is discrete - that is,
     * it only has one element.
     * 
     * @param cellIndex the index of the cell to check
     * @return true of the cell at this index is discrete
     */
    public boolean isDiscreteCell(int cellIndex) {
        return this.cells.get(cellIndex).size() == 1;
    }
    
    /**
     * Gets the index of the first cell in the partition that is discrete.
     * 
     * @return the index of the first discrete cell
     */
    public int getIndexOfFirstNonDiscreteCell() {
        for (int i = 0; i < this.cells.size(); i++) {
            if (this.cells.get(i).size() > 1) return i;
        }
        return -1;  // XXX
    }
    
    /**
     * Add a new singleton cell to the end of the partition containing only 
     * this element.
     *  
     * @param element the element to add in its own cell
     */
    public void addSingletonCell(int element) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        cell.add(element);
        this.cells.add(cell);
    }
    
    /**
     * Removes the cell at the specified index.
     * 
     * @param index the index of the cell to remove
     */
    public void removeCell(int index) {
        this.cells.remove(index);
    }
    
    /**
     * Adds a new cell to the end of the partition containing these elements.
     * 
     * @param elements the elements to add in a new cell
     */
    public void addCell(int... elements) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        for (int element : elements) {
            cell.add(element);
        }
        this.cells.add(cell);
    }
    
    /**
     * Adds a new cell to the end of the partition.
     * 
     * @param cell the cell to add
     */
    public void addCell(SortedSet<Integer> cell) {
        this.cells.add(cell);
    }
    
    /**
     * Add a cell as a list of elements.
     * 
     * @param cellElements the cell elements to add
     */
    public void addCell(List<Integer> cellElements) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        cell.addAll(cellElements);
        this.cells.add(cell);
    }
    
    public void addToCell(int index, int element) {
        cells.get(index).add(element);
    }
    
    /**
     * Insert a cell into the partition at the specified index.
     * @param index the index of the cell to add
     * @param cell the cell to add
     */
    public void insertCell(int index, SortedSet<Integer> cell) {
        this.cells.add(index, cell);
    }
    
    /**
     * Creates and returns a copy of the cell at cell index.
     *  
     * @param cellIndex the cell to copy
     * @return the copy of the cell
     */
    public SortedSet<Integer> copyBlock(int cellIndex) {
        return new TreeSet<Integer>(this.cells.get(cellIndex));
    }
    
    /**
     * Sort the cells in increasing order.
     */
    public void order() {
    	Collections.sort(cells, new Comparator<SortedSet<Integer>>() {

			@Override
			public int compare(SortedSet<Integer> cellA, SortedSet<Integer> cellB) {
				return cellA.first().compareTo(cellB.first());
			}
    		
    	});
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
            SortedSet<Integer> cell = cells.get(cellIndex);
            int elementIndex = 0;
            for (int element : cell) {
                sb.append(element);
                if (cell.size() > 1 && elementIndex < cell.size() - 1) {
                    sb.append(",");
                }
                elementIndex++;
            }
            if (cells.size() > 1 && cellIndex < cells.size() - 1) {
                sb.append("|");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parse a string like "[0,2|1,3]" to form the partition; cells are 
     * separated by '|' characters and elements within the cell by commas.
     * 
     * @param strForm
     * @return
     */
    public static Partition fromString(String strForm) {
        Partition p = new Partition();
        int index = 0;
        if (strForm.charAt(0) == '[') {
            index++;
        }
        int endIndex;
        if (strForm.charAt(strForm.length() - 1) == ']') {
            endIndex = strForm.length() - 2;
        } else {
            endIndex = strForm.length() - 1;
        }
        int currentCell = -1;
        int numStart = -1; 
        while (index <= endIndex) {
            char c = strForm.charAt(index);
            if (Character.isDigit(c)) {
                if (numStart == -1) {
                    numStart = index;
                }
            } else if (c == ',') {
                int element = Integer.parseInt(
                        strForm.substring(numStart, index));
                if (currentCell == -1) {
                    p.addCell(element);
                    currentCell = 0;
                } else {
                    p.addToCell(currentCell, element);
                }
                numStart = -1;
            } else if (c == '|') {
                int element = Integer.parseInt(
                        strForm.substring(numStart, index));
                p.addToCell(currentCell, element);
                currentCell++;
                p.addCell();
                numStart = -1;
            }
            index++;
        }
        int lastElement = Integer.parseInt(strForm.substring(numStart, endIndex + 1));
        p.addToCell(currentCell, lastElement);
        return p;
    }

}
