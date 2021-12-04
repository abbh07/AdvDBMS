package Lock;

import java.util.AbstractMap;
import java.util.Map;

public class Lock {
    private static final int totalVariables = 20;
    private Map<Integer, AbstractMap.SimpleEntry<String, LOCKTYPES>> lockTable;

    private void initTable() {
        for (int i = 1; i <= totalVariables; i++) {
            lockTable.put(i, new AbstractMap.SimpleEntry("T0", LOCKTYPES.NA));
        }
    }

    public Lock() {
        initTable();
    }

    public boolean acquireLock(int variable, String transactionId, LOCKTYPES type) {
        if (lockTable.get(variable).getValue() == LOCKTYPES.NA) {
            return false;
        } else {
            lockTable.put(variable, new AbstractMap.SimpleEntry<>(transactionId, type));
            return true;
        }
    }

    public void releaseLock(int variable) {
        lockTable.put(variable, new AbstractMap.SimpleEntry("T0", LOCKTYPES.NA));
    }

    public LOCKTYPES getLock(int variable) {
        return lockTable.get(variable).getValue();
    }
}
