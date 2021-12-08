package Site;

import Lock.Lock;
import Lock.LockTypes;
import Transaction.Transaction;

import java.util.*;

public class Site {


    private static final int totalVariables = 20;
    private int siteId;
    private Map<String, TreeMap<Integer, Integer>> dataMap;
    private Map<String, List<Lock>> lockMap;
    private TreeMap<Integer, Integer> startEndTimeMap;
    private boolean siteStatus;
    private HashSet<Transaction> visitedTransactions;
    private HashMap<String, Boolean> variableStaleStateMap;

    public Site(int siteId) {
        this.siteId = siteId;
        this.dataMap = new HashMap<>();
        this.lockMap = new HashMap<>();
        this.startEndTimeMap = new TreeMap<>();
        this.siteStatus = true;
        this.visitedTransactions = new HashSet<>();
        this.variableStaleStateMap = new HashMap<>();
    }

    public void addDataMap(String key, int time, int value){
        TreeMap<Integer, Integer> mapValue = this.dataMap.getOrDefault(key, new TreeMap<>());
        mapValue.put(time, value);
        this.dataMap.put(key, mapValue);
    }

    public void addLockMap(String key, Lock lock){
        List<Lock> locks = this.lockMap.getOrDefault(key, new ArrayList<Lock>());
        locks.add(lock);
        this.lockMap.put(key, locks);
    }

    public void addStartEndTimeMap(int key, int value){
        this.startEndTimeMap.put(key, value);
    }

    public void addTransaction(Transaction transaction) {
        this.visitedTransactions.add(transaction);
    }



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

    public boolean getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(boolean siteStatus) {
        this.siteStatus = siteStatus;
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
        int index = treeMap.lowerKey(time+1);
        return treeMap.get(index);
    }

    public boolean canAcquireLock(String variable, Transaction transaction, LockTypes lockType) {
        List<Lock> locksPresent = this.lockMap.getOrDefault(variable, new ArrayList<Lock>());
        return (lockType == LockTypes.READ) ? canAcquireReadLock(locksPresent, transaction) : canAcquireWriteLock(locksPresent, transaction);
    }

    private boolean canAcquireReadLock(List<Lock> locksPresent, Transaction transaction) {
        for(Lock lock : locksPresent){
            if(lock.getLockType() == LockTypes.WRITE && lock.getTransaction().getTransactionId() != transaction.getTransactionId()){
                return false;
            }
        }
        return true;
    }

    private boolean canAcquireWriteLock(List<Lock> locksPresent, Transaction transaction) {
        for(Lock lock : locksPresent){
            if(lock.getLockType() == LockTypes.WRITE && lock.getTransaction().getTransactionId() == transaction.getTransactionId()){
                return true;
            }
            if(lock.getLockType() == LockTypes.READ && lock.getTransaction().getTransactionId() == transaction.getTransactionId()){
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

    private boolean lockAlreadyPresent(String variable, Transaction transaction, LockTypes lockType){
        List<Lock> lockList = this.lockMap.get(variable);
        if(lockList==null || lockList.size()==0) return false;
        for(Lock lock : lockList){
            if(lock.getTransaction().getTransactionId() == transaction.getTransactionId() &&
                    (lock.getLockType() == lockType || (lockType == LockTypes.READ && lock.getLockType() == LockTypes.WRITE )))
                return true;
        }
        return false;
    }

    public void acquireLock(String variable, Transaction transaction, LockTypes lockType) {
        if(lockAlreadyPresent(variable, transaction, lockType)) return;
        if(lockType == LockTypes.WRITE && promoteReadLock(variable, transaction)) return;
        Lock lock = new Lock(lockType, transaction);
        List<Lock> lockList = this.lockMap.getOrDefault(variable, new ArrayList<Lock>());
        lockList.add(lock);
        this.lockMap.put(variable, lockList);
        //call Graph method
    }

    private boolean promoteReadLock(String variable, Transaction transaction){
        List<Lock> lockList = this.lockMap.get(variable);
        if(lockList==null || lockList.size()==0) return false;
        int index = -1;
        for (int i=0; i<lockList.size(); i++){
            Lock lock = lockList.get(i);
            if(lock.getTransaction().getTransactionId() == transaction.getTransactionId()){
                index = i;
                break;
            }
        }
        if(index == -1) return false;
        lockList.get(index).setLockType(LockTypes.WRITE);
        return true;
    }

    public TreeMap<Integer, Integer> getStartEndTimeMap() {
        return startEndTimeMap;
    }

    public void setStartEndTimeMap(TreeMap<Integer, Integer> startEndTimeMap) {
        this.startEndTimeMap = startEndTimeMap;
    }

    public boolean canAccessReadOnly(String variable, Transaction transaction){
        int transactionStartTime = transaction.getStartTime();
        int lastWriteTime = this.dataMap.get(variable).lowerKey(transactionStartTime+1);
        return isValidForReadOnly(lastWriteTime, transactionStartTime);
    }

    private boolean isValidForReadOnly(int startTime, int endTime){
        int siteUpTime = startEndTimeMap.lowerKey(startTime+1);
        for(int i=startTime; i<=endTime; i++){
            if(startEndTimeMap.get(siteUpTime)<=endTime)
                return false;
        }
        return true;
    }

    public void setEndTime(int time){
        int key = this.startEndTimeMap.lastKey();
        this.startEndTimeMap.put(key, time);
    }

    public void print() {
        System.out.print("site " + this.siteId + " - ");
        ArrayList<String> sortedKeys = new ArrayList<>();
        for(String key : this.dataMap.keySet()) {
            sortedKeys.add(key);
        }
        Collections.sort(sortedKeys, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return parseInteger(s1) - parseInteger(s2);
            }

            int parseInteger(String s) {
                String num = s.replaceAll("\\D", "");
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        for(String key : sortedKeys ){
            int latestTime = this.dataMap.get(key).lastKey();
            System.out.print(key + ":" + this.dataMap.get(key).get(latestTime) + " ");
        }
        System.out.println();
    }

    public HashSet<Transaction> getVisitedTransactions() {
        return visitedTransactions;
    }

    public void setVisitedTransactions(HashSet<Transaction> visitedTransactions) {
        this.visitedTransactions = visitedTransactions;
    }

    public HashMap<String, Boolean> getVariableStaleStateMap() {
        return variableStaleStateMap;
    }
}
