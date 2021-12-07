package TransactionManager;

import Action.*;
import Deadlock.Deadlock;
import IOManager.IOManager;
import Lock.Lock;
import Lock.LockTypes;
import Site.Site;
import Transaction.Transaction;
import Transaction.TransactionType;

import java.util.*;

public class TransactionManager {

    private List<Transaction> transactions;
    private List<Site> sites;
    private Queue<Action> waitQueue;
    private Deadlock deadlock;
    private HashMap<String, HashMap<String, Map.Entry<Integer, Integer>>> cache;
    private int tick = 0;
    private Map<String, HashSet<Site>> variableSiteMap;

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.sites = new ArrayList<>();
        this.deadlock = new Deadlock();
        this.waitQueue = new LinkedList<>();
        this.variableSiteMap = new HashMap<>();
        this.cache = new HashMap<>();
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
                }
            } else {
                Site site = sites.get((i % 10));
                site.addDataMap("x" + i, 0, 10 * i);
                HashSet<Site> set = variableSiteMap.getOrDefault("x" + i, new HashSet<>());
                set.add(site);
                variableSiteMap.put("x" + i, set);
                site.addStartEndTimeMap(0, Integer.MAX_VALUE);
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

    private void readOnlyAction(ReadAction action) {
        HashSet<Site> sites = variableSiteMap.get(action.getVariable());
        boolean isRead = false;
        for (Site site : sites) {
            if (site.getSiteStatus() && site.canAccessReadOnly(action.getVariable(), action.getTransaction())) {
                isRead = true;
                site.addTransaction(action.getTransaction());
                System.out.println(action.getVariable() + ": " + site.getLatestValue(action.getVariable()));
                break;
            }
        }
        if (!isRead)
            waitQueue.add(action);
    }

    private void readAction(ReadAction action) {
        boolean isAvailable = false;

        for (Site s : sites) {
            if (s.getSiteStatus()) {
                Map<String, TreeMap<Integer, Integer>> map = s.getDataMap();
                if (map.containsKey(action.getVariable()) && s.canAcquireLock(action.getVariable(), action.getTransaction(), LockTypes.READ)) {
                    s.acquireLock(action.getVariable(), action.getTransaction(), LockTypes.READ);
                    s.addTransaction(action.getTransaction());
                    System.out.println(action.getVariable() + ": " + s.getLatestValue(action.getVariable()));
                    isAvailable = true;
                    break;
                }
            }
        }

        if (!isAvailable) {
            waitQueue.add(action);
        }
    }

    private void writeAction(WriteAction action) {
        boolean isAllAvailable = true;

        for (Site s : sites) {
            if (s.getSiteStatus() && s.getDataMap().containsKey(action.getVariable())) {
                if (!s.canAcquireLock(action.getVariable(), action.getTransaction(), LockTypes.WRITE)) {
                    List<Lock> locks = s.getLockMap().get(action.getVariable());
                    for (Lock l : locks) {
                        if (l.getLockType().equals(LockTypes.WRITE)) {
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
//                    s.writeValue(action.getVariable(), action.getValue(), tick);
                    HashMap<String, Map.Entry<Integer, Integer>> variables = cache.get(action.getTransaction().getTransactionId());
                    if (variables == null) {
                        variables = new HashMap<>();
                    }
                    variables.put(action.getVariable(), Map.entry(action.getValue(), tick));
                    cache.put(action.getTransaction().getTransactionId(), variables);
                }
            }
        } else {
            waitQueue.add(action);
        }
    }

    private void beginAction(BeginAction action) {
        this.addTransaction(action.getTransaction());
    }

    private void beginRoAction(BeginRoAction action) {
        this.addTransaction(action.getTransaction());
    }

    private void dumpAction(DumpAction action) {
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
    }

    private void endAction(EndAction action) {
        //To check what to do with Q?
        if (action.getTransaction().getLive()) {
            //Bhatta - cache to write or not?
            for (Map.Entry<String, Map.Entry<Integer, Integer>> entry : cache.getOrDefault(action.getTransaction().getTransactionId(), new HashMap<>()).entrySet()) {
                String key = entry.getKey();
                Map.Entry<Integer, Integer> val = entry.getValue();
                for (Site s : sites) {
                    if (s.getSiteStatus()) {
                        Map<String, TreeMap<Integer, Integer>> dataMap = s.getDataMap();
                        if (dataMap.containsKey(key)) {
                            TreeMap<Integer, Integer> pair = dataMap.get(key);
                            pair.put(val.getValue(), val.getKey());
                            dataMap.put(key, pair);
                        }
                    }
                }
            }
            action.getTransaction().setLive(false);
            cleanUpTransaction(action.getTransaction());
            //Print end
            System.out.println(action.getTransaction().getTransactionId() + " : ended");
        } else {
            //Print already ended
            cleanUpTransaction(action.getTransaction());
            System.out.println(action.getTransaction().getTransactionId() + " : already ended");
        }
    }

    private void cleanUpTransaction(Transaction transaction) {
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
        List<Action> actionsToRemove = new ArrayList<Action>();
        for (Action action : waitQueue) {
            if (action.getTransaction().getTransactionId() == transaction.getTransactionId()) {
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

    public void processAction(Action action) {
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
                    dumpAction((DumpAction) action);
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
                        readOnlyAction((ReadAction) action);
                    } else if (action.getTransaction().getTransactionType() == TransactionType.BOTH) {
                        readAction((ReadAction) action);
                    }
                }
                break;
            case RECOVER:
                if (action instanceof RecoverAction)
                    recoverAction((RecoverAction) action);
                break;
            case WRITE:
                if (action instanceof WriteAction)
                    writeAction((WriteAction) action);
                break;
            default:
                // code block
        }

    }

    private void resolveQueue() {
        while (!waitQueue.isEmpty()) {
            int size = waitQueue.size();
            for (int i = 0; i < size; i++) {
                Action action = waitQueue.peek();
                waitQueue.poll();
                this.processAction(action);
                //toggle boolean
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
                cleanUpTransaction(victim);
            }
            resolveQueue();

            tick++;
            Action action = null;
            if (line.startsWith("beginRO")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                Transaction transaction = new Transaction(transactionId, TransactionType.READONLY, tick);
                action = new BeginRoAction(transaction);
            } else if (line.startsWith("begin")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                Transaction transaction = new Transaction(transactionId, TransactionType.BOTH, tick);
                action = new BeginAction(transaction);
            } else if (line.startsWith("fail")) {
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
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
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
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
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
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
                String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                String transactionId = fields.split(",")[0];
                Transaction readTransaction = null;
                for (Transaction t : transactions) {
                    if (Objects.equals(t.getTransactionId(), transactionId)) {
                        readTransaction = t;
                        break;
                    }
                }
                String variable = fields.split(",")[1];
                if (readTransaction != null) {
                    action = new ReadAction(readTransaction, variable);
                }
            } else if (line.startsWith("W")) {
                String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                String transactionId = fields.split(",")[0];
                Transaction writeTransaction = null;
                for (Transaction t : transactions) {
                    if (Objects.equals(t.getTransactionId(), transactionId)) {
                        writeTransaction = t;
                        break;
                    }
                }
                String variable = fields.split(",")[1];
                int value = Integer.parseInt(fields.split(",")[2]);
                action = new WriteAction(writeTransaction, variable, value);
            }
            if (action != null)
                this.processAction(action);
        }
    }
}
