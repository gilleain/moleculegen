package handler;

public interface Handler<T> {
    
    public void handle(T object);
    
    public void finish();

}
