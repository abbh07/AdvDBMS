package TransactionManager;

import Action.*;
import Deadlock.Deadlock;
import IOManager.IOManager;
import Lock.Lock;
import Lock.LockTypes;
import Site.Site;
import Transaction.Transaction;
import Transaction.TransactionType;
import util.Cache;

import java.util.*;

public class TransactionManager {

    private int tick = 0;
    private List<Transaction> transactions;
    private List<Site> sites;
    private Queue<Action> waitQueue;
    private Deadlock deadlock;
    private Map<String, HashSet<Site>> variableSiteMap;

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.sites = new ArrayList<>();
        this.deadlock = new Deadlock();
        this.waitQueue = new LinkedList<>();
        this.variableSiteMap = new HashMap<>();
    }

    public void init() {
        for (int j = 1; j <= 10; j++) {
            Site site = new Site(j);
            sites.add(site);
        }
        for (int i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                for (int j = 1; j <= 10; j++) {
                    Site site = sites.get(j - 1);
                    site.addDataMap("x" + i, 0, 10 * i);
                    HashSet<Site> set = variableSiteMap.getOrDefault("x" + i, new HashSet<>());
                    set.add(site);
                    variableSiteMap.put("x" + i, set);
                    site.addStartEndTimeMap(0, Integer.MAX_VALUE);
                    site.getVariableStaleStateMap().put("x" + i, false);
                }
            } else {
                Site site = sites.get((i % 10));
                site.addDataMap("x" + i, 0, 10 * i);
                HashSet<Site> set = variableSiteMap.getOrDefault("x" + i, new HashSet<>());
                set.add(site);
                variableSiteMap.put("x" + i, set);
                site.addStartEndTimeMap(0, Integer.MAX_VALUE);
                site.getVariableStaleStateMap().put("x" + i, false);
            }
        }
    }


    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public List<Site> getSites() {
        return this.sites;
    }

    public void addSite(Site site) {
        this.sites.add(site);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    private void readOnlyAction(ReadAction action, boolean firstAttempt) {
        HashSet<Site> sites = variableSiteMap.get(action.getVariable());
        boolean isRead = false;
        for (Site site : sites) {
            if (site.getSiteStatus() && !site.getVariableStaleStateMap().get(action.getVariable()) && site.canAccessReadOnly(action.getVariable(), action.getTransaction())) {
                isRead = true;
                site.addTransaction(action.getTransaction());
                System.out.println(action.getTransaction().getTransactionId() + ": " +action.getVariable() + ": " + site.getValue(action.getVariable(), action.getTransaction().getStartTime()));
                break;
            }
        }
        if (!isRead) {
            if (firstAttempt)
                System.out.println("Transaction " + action.getTransaction().getTransactionId() + " is being added to the wait queue because of lock conflict");
            waitQueue.add(action);
        }

    }

    private void readAction(ReadAction action, boolean firstAttempt) {
        boolean isAvailable = false;
        HashSet<Site> allValidSites = variableSiteMap.getOrDefault(action.getVariable(), new HashSet<>());
        boolean dependencyFound = false;

        for (Site s : allValidSites) {
            if (s.getSiteStatus() && !s.getVariableStaleStateMap().get(action.getVariable()) && !dependencyFound) {
                if (s.canAcquireLock(action.getVariable(), action.getTransaction(), LockTypes.READ)) {
                    s.acquireLock(action.getVariable(), action.getTransaction(), LockTypes.READ);
                    s.addTransaction(action.getTransaction());
                    System.out.println(action.getTransaction().getTransactionId() + ": " +action.getVariable() + ": " + s.getLatestValue(action.getVariable()));
                    isAvailable = true;
                    break;
                } else {
                    for (Lock lock : s.getLockMap().getOrDefault(action.getVariable(), new ArrayList<>())) {
                        if (lock.getLockType().equals(LockTypes.WRITE)) {
                            deadlock.addEdge(action.getTransaction().getTransactionId(), lock.getTransaction().getTransactionId());
                            dependencyFound = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!isAvailable) {
            if (firstAttempt)
                System.out.println("Transaction " + action.getTransaction().getTransactionId() + " is being added to the wait queue because of lock conflict");
            waitQueue.add(action);
        }
    }

    private void writeAction(WriteAction action, boolean firstAttempt) {

        boolean isAllAvailable = true;

        for (Site s : sites) {
            if (s.getSiteStatus() && s.getDataMap().containsKey(action.getVariable())) {
                if (!s.canAcquireLock(action.getVariable(), action.getTransaction(), LockTypes.WRITE)) {
                    List<Lock> locks = s.getLockMap().get(action.getVariable());
                    for (Lock l : locks) {
                        if (l.getLockType().equals(LockTypes.WRITE) || (!l.getTransaction().getTransactionId().equals(action.getTransaction().getTransactionId()))) {
                            deadlock.addEdge(action.getTransaction().getTransactionId(), l.getTransaction().getTransactionId());
                        }
                    }
                    isAllAvailable = false;
                    break;
                }
            }
        }

        if (isAllAvailable) {
            for (Site s : sites) {
                if (s.getSiteStatus() && s.getDataMap().containsKey(action.getVariable())) {
                    s.acquireLock(action.getVariable(), action.getTransaction(), LockTypes.WRITE);
                    s.addTransaction(action.getTransaction());
                    Map<String, Set<Cache>> cache = s.getDatacache();
                    Set<Cache> classes = cache.getOrDefault(action.getTransaction().getTransactionId(), new HashSet<>());
                    Cache obj = null;
                    for (Cache c : classes) {
                        if (Objects.equals(c.getVariable(), action.getVariable())) {
                            obj = c;
                            break;
                        }
                    }
                    if (obj == null) {
                        obj = new Cache(action.getVariable(), tick, action.getValue());
                    } else {
                        obj.addValue(tick, action.getValue());
                    }
                    classes.add(obj);
                    s.getDatacache().put(action.getTransaction().getTransactionId(), classes);
                }
            }
        } else {
            if (firstAttempt)
                System.out.println("Transaction " + action.getTransaction().getTransactionId() + " is being added to the wait queue because of lock conflict");
            waitQueue.add(action);
        }
    }

    private void beginAction(BeginAction action) {
        this.addTransaction(action.getTransaction());
    }

    private void beginRoAction(BeginRoAction action) {
        this.addTransaction(action.getTransaction());
    }

    private void dumpAction() {
        for (Site site : sites) {
            site.print();
        }
    }

    private void recoverAction(RecoverAction action) {
        Site site = action.getSite();
        site.setSiteStatus(true);
        site.setLockMap(new HashMap<>());
        TreeMap<Integer, Integer> treeMap = site.getStartEndTimeMap();
        treeMap.put(tick, Integer.MAX_VALUE);
        site.setStartEndTimeMap(treeMap);
        HashMap<String, Boolean> staleStateMap = site.getVariableStaleStateMap();

        for(String entry : staleStateMap.keySet()) {
            if(variableSiteMap.get(entry).size()>1)
                staleStateMap.put(entry, true);
        }
    }

    private void endAction(EndAction action) {
        //To check what to do with Q?
        if (action.getTransaction().getLive()) {
            for (Site site : sites) {
                Map<String, Set<Cache>> dataCache = site.getDatacache();
                Set<Cache> transactionCache = dataCache.getOrDefault(action.getTransaction().getTransactionId(), new HashSet<>());
                for (Cache c : transactionCache) {
                    String variable = c.getVariable();
                    for (int timeOfCache : c.getPair().keySet()) {
                        int valueOfCache = c.getPair().get(timeOfCache);
                        site.getDataMap().get(variable).put(timeOfCache, valueOfCache);
                        System.out.println("Variable " + variable + " is updated on Site " + site.getSiteId() + " with value " + valueOfCache);
                        site.getVariableStaleStateMap().put(variable, false);
                    }
                }
            }
            action.getTransaction().setLive(false);
            cleanUpTransaction(action.getTransaction(), false);
            System.out.println("Transaction " + action.getTransaction().getTransactionId() + " commits.");
        } else {
            cleanUpTransaction(action.getTransaction(), true);
        }
    }

    private void cleanUpTransaction(Transaction transaction, boolean isAborted) {
        if (isAborted) {
            System.out.println("Transaction " + transaction.getTransactionId() + " is Aborted");
        }
        for (Site s : sites) {
            for (String variable : s.getLockMap().keySet()) {
                List<Lock> locksToRemove = new ArrayList<>();
                for (Lock lock : s.getLockMap().get(variable)) {
                    if (Objects.equals(lock.getTransaction().getTransactionId(), transaction.getTransactionId())) {
                        locksToRemove.add(lock);
                    }
                }
                s.getLockMap().get(variable).removeAll(locksToRemove);
            }
        }
        List<Action> actionsToRemove = new ArrayList<>();
        for (Action action : waitQueue) {
            if (action.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                actionsToRemove.add(action);
            }
        }
        waitQueue.removeAll(actionsToRemove);
        transactions.remove(transaction);
    }

    private void failAction(FailAction action) {
        Site site = action.getSite();
        site.setSiteStatus(false);
        site.setLockMap(new HashMap<>());
        site.setEndTime(tick);
        for (Transaction t : site.getVisitedTransactions()) {
            t.setLive(false);
        }
    }

    public void processAction(Action action, boolean firstAttempt) {
        Operations actionType = action.getOperation();
        switch (actionType) {
            case BEGIN:
                if (action instanceof BeginAction)
                    beginAction((BeginAction) action);
                break;
            case BEGINRO:
                if (action instanceof BeginRoAction)
                    beginRoAction((BeginRoAction) action);
                break;
            case DUMP:
                if (action instanceof DumpAction)
                    dumpAction();
                break;
            case END:
                if (action instanceof EndAction)
                    endAction((EndAction) action);
                break;
            case FAIL:
                if (action instanceof FailAction)
                    failAction((FailAction) action);
                break;
            case READ:
                if (action instanceof ReadAction) {
                    if (action.getTransaction().getTransactionType() == TransactionType.READONLY) {
                        readOnlyAction((ReadAction) action, firstAttempt);
                    } else if (action.getTransaction().getTransactionType() == TransactionType.BOTH) {
                        readAction((ReadAction) action, firstAttempt);
                    }
                }
                break;
            case RECOVER:
                if (action instanceof RecoverAction)
                    recoverAction((RecoverAction) action);
                break;
            case WRITE:
                if (action instanceof WriteAction)
                    writeAction((WriteAction) action, firstAttempt);
                break;
            default:
                // code block
        }

    }

    private boolean conflictWithWaitQueue(Queue<Action> queue, Action action, boolean firstAttempt) {
        return action.getOperation() == Operations.READ ? conflictWithWaitQueueRead(queue, (ReadAction) action, firstAttempt) : conflictWithWaitQueueWrite(queue, (WriteAction) action, firstAttempt);
    }

    private boolean conflictWithWaitQueueRead(Queue<Action> queue, ReadAction action, boolean firstAttempt) {
        for (Action actionToCheck : queue) {
            if (actionToCheck instanceof WriteAction) {
                WriteAction actionInQueue = (WriteAction) actionToCheck;
                if (!actionInQueue.getVariable().equals(action.getVariable())) {
                    continue;
                }
                for (Site site : variableSiteMap.getOrDefault(action.getVariable(), new HashSet<>())) {
                    for (Lock lock : site.getLockMap().get(action.getVariable())) {
                        if (lock.getTransaction().getTransactionId().equals(action.getTransaction().getTransactionId())) {
                            return false;
                        }
                    }
                }
                if (firstAttempt)
                    System.out.println("Transaction " + action.getTransaction().getTransactionId() + " is being added to the wait queue because of lock conflict");
                deadlock.addEdge(action.getTransaction().getTransactionId(), actionToCheck.getTransaction().getTransactionId());
                waitQueue.add(action);
                return true;
            }
        }
        return false;
    }

    private boolean conflictWithWaitQueueWrite(Queue<Action> queue, WriteAction action, boolean firstAttempt) {
        for (Action actionToCheck : queue) {
            if (actionToCheck instanceof WriteAction) {
                WriteAction actionInQueue = (WriteAction) actionToCheck;
                if (!actionInQueue.getVariable().equals(action.getVariable())) {
                    continue;
                }
                for (Site site : variableSiteMap.getOrDefault(action.getVariable(), new HashSet<>())) {
                    for (Lock lock : site.getLockMap().get(action.getVariable())) {
                        if (!lock.getTransaction().getTransactionId().equals(action.getTransaction().getTransactionId()) || lock.getLockType() != LockTypes.WRITE) {
                            if (firstAttempt)
                                System.out.println("Transaction " + action.getTransaction().getTransactionId() + " is being added to the wait queue because of lock conflict");
                            deadlock.addEdge(action.getTransaction().getTransactionId(), actionToCheck.getTransaction().getTransactionId());
                            waitQueue.add(action);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void resolveQueue() {
        Queue<Action> actionsToCheck;
        while (!waitQueue.isEmpty()) {
            int size = waitQueue.size();
            actionsToCheck = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Action action = waitQueue.peek();
                waitQueue.poll();
                if (!conflictWithWaitQueue(actionsToCheck, action, false)) {
                    actionsToCheck.add(action);
                    this.processAction(action, false);
                }
            }
            if (size == waitQueue.size()) {
                break;
            }
        }
    }

    public void simulate(String filename) {
        IOManager ioManager = new IOManager(filename);
        String line;
        while ((line = ioManager.readLine()) != null) {
            //Check deadlock and waitQ;
            if (deadlock.checkForDeadlock()) {
                Transaction victim = deadlock.resolveDeadlock(transactions);
                cleanUpTransaction(victim, true);
            }
            resolveQueue();

            tick++;
            Action action = null;
            if (line.startsWith("beginRO")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
                Transaction transaction = new Transaction(transactionId, TransactionType.READONLY, tick);
                action = new BeginRoAction(transaction);
            } else if (line.startsWith("begin")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
                Transaction transaction = new Transaction(transactionId, TransactionType.BOTH, tick);
                action = new BeginAction(transaction);
            } else if (line.startsWith("fail")) {
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim());
                Site failedSite = null;
                for (Site s : sites) {
                    if (s.getSiteId() == siteId) {
                        failedSite = s;
                        break;
                    }
                }
                if (failedSite != null) {
                    action = new FailAction(failedSite);
                } else {
                    System.out.println("Can't fail the site as it doesn't exist");
                }
            } else if (line.startsWith("recover")) {
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim());
                Site recoveredSite = null;
                for (Site s : sites) {
                    if (s.getSiteId() == siteId) {
                        recoveredSite = s;
                        break;
                    }
                }
                if (recoveredSite != null) {
                    action = new RecoverAction(recoveredSite);
                }
            } else if (line.startsWith("end")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
                Transaction endTransaction = null;
                for (Transaction t : transactions) {
                    if (Objects.equals(t.getTransactionId(), transactionId)) {
                        endTransaction = t;
                        break;
                    }
                }
                if (endTransaction != null) {
                    action = new EndAction(endTransaction);
                }
            } else if (line.startsWith("dump")) {
                action = new DumpAction();
            } else if (line.startsWith("R")) {
                String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
                String transactionId = fields.split(",")[0].trim();
                Transaction readTransaction = null;
                for (Transaction t : transactions) {
                    if (Objects.equals(t.getTransactionId(), transactionId)) {
                        readTransaction = t;
                        break;
                    }
                }
                String variable = fields.split(",")[1].trim();
                if (readTransaction != null) {
                    action = new ReadAction(readTransaction, variable);
                }
            } else if (line.startsWith("W")) {
                String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
                String transactionId = fields.split(",")[0].trim();
                Transaction writeTransaction = null;
                for (Transaction t : transactions) {
                    if (Objects.equals(t.getTransactionId(), transactionId)) {
                        writeTransaction = t;
                        break;
                    }
                }
                String variable = fields.split(",")[1].trim();
                int value = Integer.parseInt(fields.split(",")[2].trim());
                action = new WriteAction(writeTransaction, variable, value);
            }
            if (action != null) {
                if ((action.getOperation() == Operations.READ || action.getOperation() == Operations.WRITE) && conflictWithWaitQueue(waitQueue, action, true))
                    continue;
                this.processAction(action, true);
            }

        }
    }
}
