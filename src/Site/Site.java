/**
 * @author Aakash Bhattacharya and Shobhit Sinha
 * @version 1.0.0
 * @date 12/07/2021
 */
package Site;

import Lock.Lock;
import Lock.LockTypes;
import Transaction.Transaction;
import util.Cache;

import java.util.*;

/**
 * A class packaging a Site with all its contents, locks, data, cache along with its getters and setters.
 */
public class Site {

    private static final int totalVariables = 20;
    private int siteId;
    private Map<String, TreeMap<Integer, Integer>> dataMap;
    private HashMap<String, Set<Cache>> datacache;
    private Map<String, List<Lock>> lockMap;
    private TreeMap<Integer, Integer> startEndTimeMap;
    private boolean siteStatus;
    private HashSet<Transaction> visitedTransactions;
    private HashMap<String, Boolean> variableStaleStateMap;

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

    public TreeMap<Integer, Integer> getStartEndTimeMap() {
        return startEndTimeMap;
    }

    public void setStartEndTimeMap(TreeMap<Integer, Integer> startEndTimeMap) {
        this.startEndTimeMap = startEndTimeMap;
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

    public Map<String, Set<Cache>> getDatacache() {
        return datacache;
    }

    /**
     * Constructor initializing the site object.
     *
     * @param siteId ID of the site
     */
    public Site(int siteId) {
        this.siteId = siteId;
        this.dataMap = new HashMap<>();
        this.lockMap = new HashMap<>();
        this.startEndTimeMap = new TreeMap<>();
        this.siteStatus = true;
        this.visitedTransactions = new HashSet<>();
        this.variableStaleStateMap = new HashMap<>();
        this.datacache = new HashMap<>();
    }

    /**
     * Adds an entry to the data map.
     *
     * @param key   Variable name
     * @param time  Tick of the Action
     * @param value Value of the variable
     */
    public void addDataMap(String key, int time, int value) {
        TreeMap<Integer, Integer> mapValue = this.dataMap.getOrDefault(key, new TreeMap<>());
        mapValue.put(time, value);
        this.dataMap.put(key, mapValue);
    }

    /**
     * Adds an entry to the lock map.
     *
     * @param key  Variable name
     * @param lock Lock object
     */
    public void addLockMap(String key, Lock lock) {
        List<Lock> locks = this.lockMap.getOrDefault(key, new ArrayList<>());
        locks.add(lock);
        this.lockMap.put(key, locks);
    }

    /**
     * Adds a timestamp to the start end map.
     *
     * @param key   Start time
     * @param value End time
     */
    public void addStartEndTimeMap(int key, int value) {
        this.startEndTimeMap.put(key, value);
    }

    /**
     * Adds a transaction to the visited transaction list.
     *
     * @param transaction Transaction to add
     */
    public void addTransaction(Transaction transaction) {
        this.visitedTransactions.add(transaction);
    }

    /**
     * Inits the datamap with default values.
     *
     * @param key   Initial key
     * @param value Initial value
     */
    public void initData(String key, int value) {
        this.writeValue(key, value, 0);
    }

    /**
     * Fails a site by toggling boolean.
     */
    public void failSite() {
        this.siteStatus = false;
    }

    /**
     * Recovers a site by toggling boolean.
     */
    public void recoverSite() {
        this.siteStatus = true;
    }

    /**
     * Writes a value to the datamap.
     *
     * @param key   Variable name;
     * @param value Variable value
     * @param time  Tick
     */
    public void writeValue(String key, int value, int time) {
        TreeMap<Integer, Integer> treeMap = this.dataMap.getOrDefault(key, new TreeMap<>());
        treeMap.put(time, value);
        this.dataMap.put(key, treeMap);
    }

    /**
     * Returns the latest value of a variable.
     *
     * @param key Variable name
     * @return Latest value
     */
    public int getLatestValue(String key) {
        TreeMap<Integer, Integer> treeMap = dataMap.get(key);
        return treeMap.get(treeMap.lastKey());
    }

    /**
     * Gets the lower bound time specified by the input.
     *
     * @param key  Variable name
     * @param time Time for which lower bound has to be computed
     * @return Lower bound time
     */
    public int getValue(String key, int time) {
        TreeMap<Integer, Integer> treeMap = dataMap.get(key);
        int index = treeMap.lowerKey(time + 1);
        return treeMap.get(index);
    }

    /**
     * Function to check if a lock can be acquired.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     * @param lockType    Type of the lock to be acquired
     * @return Boolean specifying if a lock can be acquired
     */
    public boolean canAcquireLock(String variable, Transaction transaction, LockTypes lockType) {
        List<Lock> locksPresent = this.lockMap.getOrDefault(variable, new ArrayList<>());
        return (lockType == LockTypes.READ) ? canAcquireReadLock(locksPresent, transaction) : canAcquireWriteLock(locksPresent, transaction);
    }

    /**
     * Checks if a read lock can be acquired.
     *
     * @param locksPresent List of locks present already
     * @param transaction  Transaction object
     * @return Boolean signifying if the lock can be acquired
     */
    private boolean canAcquireReadLock(List<Lock> locksPresent, Transaction transaction) {
        for (Lock lock : locksPresent) {
            if (lock.getLockType() == LockTypes.WRITE && !lock.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a write lock can be acquired.
     *
     * @param locksPresent List of locks present already
     * @param transaction  Transaction object
     * @return Boolean signifying if the lock can be acquired
     */
    private boolean canAcquireWriteLock(List<Lock> locksPresent, Transaction transaction) {
        for (Lock lock : locksPresent) {
            if (lock.getLockType() == LockTypes.WRITE && lock.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                return true;
            }
            if (lock.getLockType() == LockTypes.READ && lock.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * Releases a lock held by the variable.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     */
    public void releaseLock(String variable, Transaction transaction) {
        List<Lock> lockList = this.lockMap.getOrDefault(variable, new ArrayList<>());
        List<Lock> locksToRemove = new ArrayList<>();
        for (Lock lock : lockList) {
            if (lock.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                locksToRemove.add(lock);
            }
        }
        lockList.removeAll(locksToRemove);
        this.lockMap.put(variable, lockList);
        //call graph
    }

    /**
     * Checks if a lock is already present.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     * @param lockType    LockType to be checked
     * @return Boolean signifying if the lock is present
     */
    private boolean lockAlreadyPresent(String variable, Transaction transaction, LockTypes lockType) {
        List<Lock> lockList = this.lockMap.get(variable);
        if (lockList == null || lockList.size() == 0) return false;
        for (Lock lock : lockList) {
            if (lock.getTransaction().getTransactionId().equals(transaction.getTransactionId()) &&
                    (lock.getLockType() == lockType || (lockType == LockTypes.READ && lock.getLockType() == LockTypes.WRITE)))
                return true;
        }
        return false;
    }

    /**
     * Acquires a lock at the site.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     * @param lockType    LockType to be acquired
     */
    public void acquireLock(String variable, Transaction transaction, LockTypes lockType) {
        if (lockAlreadyPresent(variable, transaction, lockType)) return;
        if (lockType == LockTypes.WRITE && promoteReadLock(variable, transaction)) return;
        Lock lock = new Lock(lockType, transaction);
        List<Lock> lockList = this.lockMap.getOrDefault(variable, new ArrayList<>());
        lockList.add(lock);
        this.lockMap.put(variable, lockList);
        //call Graph method
    }

    /**
     * Promotes a read lock to a write lock.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     * @return Boolean signifying if the lock was promoted
     */
    private boolean promoteReadLock(String variable, Transaction transaction) {
        List<Lock> lockList = this.lockMap.get(variable);
        if (lockList == null || lockList.size() == 0) return false;
        int index = -1;
        for (int i = 0; i < lockList.size(); i++) {
            Lock lock = lockList.get(i);
            if (lock.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;
        lockList.get(index).setLockType(LockTypes.WRITE);
        return true;
    }

    /**
     * Checks if a read only transaction can access a data.
     *
     * @param variable    Variable name
     * @param transaction Transaction object
     * @return Boolean signifying if it can be accessed
     */
    public boolean canAccessReadOnly(String variable, Transaction transaction) {
        int transactionStartTime = transaction.getStartTime();
        int lastWriteTime = this.dataMap.get(variable).lowerKey(transactionStartTime + 1);
        return isValidForReadOnly(lastWriteTime, transactionStartTime);
    }

    /**
     * Helper function to check if the start end time is valid for a read only transaction.
     *
     * @param startTime Start tick
     * @param endTime   End tick
     * @return Boolean signifying if it is valid
     */
    private boolean isValidForReadOnly(int startTime, int endTime) {
        int siteUpTime = startEndTimeMap.lowerKey(startTime + 1);
        for (int i = startTime; i <= endTime; i++) {
            if (startEndTimeMap.get(siteUpTime) <= endTime)
                return false;
        }
        return true;
    }

    /**
     * Sets the end time to the map.
     *
     * @param time Tick
     */
    public void setEndTime(int time) {
        int key = this.startEndTimeMap.lastKey();
        this.startEndTimeMap.put(key, time);
    }

    /**
     * Prints the site details in a sorted order.
     */
    public void print() {
        System.out.print("site " + this.siteId + " - ");
        ArrayList<String> sortedKeys = new ArrayList<>();
        for (String key : this.dataMap.keySet()) {
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
        for (String key : sortedKeys) {
            int latestTime = this.dataMap.get(key).lastKey();
            System.out.print(key + ": " + this.dataMap.get(key).get(latestTime) + " ");
        }
        System.out.println();
    }


}
