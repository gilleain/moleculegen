package handler;

import java.io.Serializable;

public interface Handler<T> extends Serializable {
    
    public void handle(T object);
    
    public void finish();

}
