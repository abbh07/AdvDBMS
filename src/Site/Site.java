package Site;

import Lock.Lock;
import Lock.LOCKTYPES;

import java.util.Map;

public class Site {
    private static final int totalVariables = 20;
    private int siteId;
    private boolean siteStatus;
    private Map<Integer, Integer> siteData;
    private Lock lock;

    public Site(int siteId) {
        this.siteId = siteId;
        siteStatus = true;
        lock = new Lock();
    }

    public void initData() {
        for (int i = 1; i <= totalVariables; i++) {
            if (i % 2 == 0) {
                siteData.put(i, 10 * i);
            } else if ((i % 10) + 1 == siteId) {
                siteData.put(i, 10 * i);
            }
        }
    }

    public void failSite() {
        siteStatus = false;
    }

    public void recoverSite() {
        siteStatus = true;
    }

    public boolean getSiteStatus() {
        return siteStatus;
    }

    public int getValue(int key) {
        return siteData.get(key);
    }

    public void setValue(int key, int value) {
        siteData.put(key, value);
    }

    public LOCKTYPES getLockStatus(int key) {
        return lock.getLock(key);
    }

    public boolean acquireLock(int variable, String transactionId, LOCKTYPES type) {
        return lock.acquireLock(variable, transactionId, type);
    }

    public void releaseLock(int variable) {
        lock.releaseLock(variable);
    }

    public boolean isKeyPresent(int key) {
        return siteData.containsKey(key);
    }

    public void dump() {
        System.out.println("site " + siteId + " -");
        for (Map.Entry<Integer, Integer> e : siteData.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }
}
