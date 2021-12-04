package Site;

import Lock.Lock;

import java.util.ArrayList;
import java.util.List;

public class Variable {
    private int value;
    private List<Lock> locks;

    Variable(int value){
        this.value = value;
        this.locks = new ArrayList<Lock>();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Lock> getLocks() {
        return locks;
    }

    public void setLocks(List<Lock> locks) {
        this.locks = locks;
    }

    public void insertLock(Lock lock) {
        this.locks.add(lock);
    }
}
