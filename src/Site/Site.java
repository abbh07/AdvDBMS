package Site;

import Lock.Lock;
import Lock.LOCKTYPES;
import Transaction.Transaction;

import java.util.*;

public class Site {
    private static final int totalVariables = 20;
    private int siteId;
    private Map<String, TreeMap<Integer, Integer>> dataMap;
    private Map<String, List<Lock>> lockMap;
    private boolean siteStatus;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Map<String, TreeMap<Integer, Integer>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, TreeMap<Integer, Integer>> dataMap) {
        this.dataMap = dataMap;
    }

    public Map<String, List<Lock>> getLockMap() {
        return lockMap;
    }

    public void setLockMap(Map<String, List<Lock>> lockMap) {
        this.lockMap = lockMap;
    }


    public Site(int siteId, boolean siteStatus) {
        this.siteId = siteId;
        this.siteStatus = siteStatus;
        this.dataMap = new HashMap<>();
        this.lockMap = new HashMap<>();
    }

    public void initData(String key, int value) {
        this.writeValue(key, value, 0);
    }

    public void failSite(){
        this.siteStatus = false;
    }

    public void recoverSite(){
        this.siteStatus = true;
    }

    public void writeValue(String key, int value, int time){
        TreeMap<Integer, Integer> treeMap = this.dataMap.getOrDefault(key, new TreeMap<>());
        treeMap.put(time, value);
        this.dataMap.put(key, treeMap);
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

    public boolean canAcquireLock(String variable, Transaction transaction, LOCKTYPES lockType) {
        List<Lock> locksPresent = this.lockMap.get(variable);
        return (lockType == LOCKTYPES.READ) ? canAcquireReadLock(locksPresent, transaction) : canAcquireWriteLock(locksPresent, transaction);
    }

    private boolean canAcquireReadLock(List<Lock> locksPresent, Transaction transaction) {
        for(Lock lock : locksPresent){
            if(lock.getLockType() == LOCKTYPES.WRITE && lock.getTransaction().getTransactionId() != transaction.getTransactionId()){
                return false;
            }
        }
        return true;
    }

    private boolean canAcquireWriteLock(List<Lock> locksPresent, Transaction transaction) {
        for(Lock lock : locksPresent){
            if(lock.getLockType() == LOCKTYPES.READ && lock.getTransaction().getTransactionId() == transaction.getTransactionId()){
                continue;
            }
            return false;
        }
        return true;
    }

    public void releaseLock(String variable, Transaction transaction) {
        List<Lock> lockList = this.lockMap.getOrDefault(variable, new ArrayList<Lock>());
        List<Lock> locksToRemove = new ArrayList<Lock>();
        for (Lock lock : lockList){
            if(lock.getTransaction().getTransactionId() == transaction.getTransactionId()){
                locksToRemove.add(lock);
            }
        }
        lockList.removeAll(locksToRemove);
        this.lockMap.put(variable, lockList);
        //call graph
    }

    public void acquireLock(String variable, Transaction transaction, LOCKTYPES lockType) {
        Lock lock = new Lock(lockType, transaction);
        List<Lock> lockList = this.lockMap.getOrDefault(variable, new ArrayList<Lock>());
        lockList.add(lock);
        this.lockMap.put(variable, lockList);
        //call Graph method
    }


    public void print() {
        //Implement print
    }
}
