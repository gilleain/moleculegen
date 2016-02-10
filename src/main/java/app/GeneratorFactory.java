package app;

import augment.AugmentingGenerator;
import handler.Handler;

public interface GeneratorFactory {
    
    public AugmentingGenerator createForFormula(String elementFormula, Handler handler);

}
