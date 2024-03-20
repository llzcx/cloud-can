package ccw.serviceinnovation.hash.select;

import java.util.List;

public class HashCodeSelectorImpl<T> implements ItemSelector<T>{

    private List<T> list;

    public HashCodeSelectorImpl(List<T> list){
        this.list = list;
    }
    @Override
    public T select(String key) {
        return list.get(key.hashCode() % list.size());
    }
}
