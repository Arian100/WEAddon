package me.arian.wea.util;

import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

public class DualMap<K, V1, V2> extends HashMap<K, V1> {

    private final Map<K, V2> values2 = new HashMap<>();

    public void put(K key, V1 value1, V2 value2) {
        this.put(key, value1);
        values2.put(key, value2);
    }

    public void removeFromMap(K key) {
        this.remove(key);
        values2.remove(key);
    }

    public Pair<V1, V2> getFromMap(K key) {
        V1 value1 = this.get(key);
        V2 value2 = values2.get(key);
        return new Pair<>(value1, value2);
    }

    public void replaceInMap(K key, V1 value1, V2 value2) {
        this.replace(key, value1);
        values2.replace(key, value2);
    }

    public Map<K, V2> getMapForValue2() {
        return values2;
    }
}
