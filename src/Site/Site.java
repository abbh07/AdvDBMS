package Site;

import Lock.Lock;
import Lock.LOCKTYPES;
import Transaction.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Site {
    private static final int totalVariables = 20;
    private int siteId;
    private HashMap<String, Variable> dataMap;
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

    public void writeValue(String key, int value){
        Variable varObject =  this.dataMap.get(key);
        varObject.setValue(value);
        this.dataMap.put(key, varObject);
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
