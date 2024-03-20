package ccw.serviceinnovation.hash.select;

public interface ItemSelector<T> {
    T select(String key);
}
