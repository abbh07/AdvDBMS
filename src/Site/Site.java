package Site;

import Lock.Lock;
import Lock.LOCKTYPES;
import Transaction.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Site {
    private static final int totalVariables = 20;
    private int siteId;
    private Map<String, TreeMap<Integer, Integer>> dataMap;
    private Map<String, List<Lock>> lockMap;

    private boolean siteStatus;


    public Site(int siteId, boolean siteStatus) {
        this.siteId = siteId;
        this.siteStatus = siteStatus;
        this.dataMap = new HashMap<>();
    }

    public void initData() {
    }

    public void failSite(){
        this.siteStatus = false;
    }

    public void recoverSite(){
        this.siteStatus = true;
    }

    public void writeValue(String key, int value, int time){
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        treeMap.put(time, value);
        dataMap.put(key, treeMap);
    }

    public int getLatestValue(String key){
        TreeMap<Integer, Integer> treeMap = dataMap.get(key);
        return treeMap.get(treeMap.lastKey());
    }

    public int getValue(String key, int time){
        TreeMap<Integer, Integer> treeMap = dataMap.get(key);
        int index = treeMap.lowerKey(time);
        return treeMap.get(index);
    }

    public boolean acquireLock(String variable, Transaction transaction, LOCKTYPES type) {
        //acquireLock
        return false;
    }

    public void releaseLock(String variable, Transaction transaction) {
        //release Lock
    }


    public void print() {
        //Implement print
    }
}
