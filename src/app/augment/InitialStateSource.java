package app.augment;

public interface InitialStateSource<T> {
    
    public Iterable<T> get();
}
