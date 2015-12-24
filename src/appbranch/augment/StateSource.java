package appbranch.augment;

public interface StateSource<T, S> {
    
    public Iterable<T> get();
    
    public S getNextExtension(T t);

}
