package appbranch;

import appbranch.handler.Handler;

public interface GeneratorFactory {
    
    public Generator createForFormula(String elementFormula, Handler handler);

}
