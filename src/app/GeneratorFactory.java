package app;

import handler.Handler;

public interface GeneratorFactory {
    
    public Generator createForFormula(String elementFormula, Handler handler);

}
