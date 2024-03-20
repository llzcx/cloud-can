package ccw.serviceinnovation.hash.directcalculator;

import java.nio.file.Path;

public interface EtagDirectCalculator {
    String get(byte[] data);

    String get(Path path);
}
