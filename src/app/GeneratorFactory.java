package app;

import app.handler.Handler;

public interface GeneratorFactory {
    
    public Generator createForFormula(String elementFormula, Handler handler);

}
