/**
 * @author Aakash Bhattacharya
 * @version 1.0.0
 */
package util;

import java.util.HashMap;

public class Cache {

    public String variable;
    public HashMap<Integer, Integer> pair;

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public HashMap<Integer, Integer> getPair() {
        return pair;
    }

    public void setPair(HashMap<Integer, Integer> pair) {
        this.pair = pair;
    }

    /**
     * Constructor creating a cache object with the specified input.
     *
     * @param variable Variable name
     * @param time     Tick value of the Action
     * @param value    Value of the variable
     */
    public Cache(String variable, int time, int value) {
        this.variable = variable;
        if (this.pair == null) {
            pair = new HashMap<>();
        }
        this.pair.put(time, value);
    }

    /**
     * Adds a value to the hashmap.
     *
     * @param tick  Tick value
     * @param value Value of the variable
     */
    public void addValue(int tick, int value) {
        if (this.pair == null) {
            pair = new HashMap<>();
        }
        this.pair.put(tick, value);
    }
}
