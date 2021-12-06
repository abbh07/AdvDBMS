package TransactionManager;

import Action.*;
import IOManager.IOManager;
import Site.Site;
import Transaction.*;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private List<Transaction> transactions;
    private List<Site> sites;
    private int tick = 0;

    public TransactionManager(){
        this.transactions = new ArrayList<Transaction>();
        this.sites = new ArrayList<Site>();
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

    public void processAction(Action action){
        //Process the input
    }

    public void simulate(String filename){
        IOManager ioManager = new IOManager(filename);
        String line = "";
        while((line= ioManager.readLine()).length()!=0){
            Action action = null;
            if (line.startsWith("beginRO")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                Transaction transaction = new Transaction(transactionId, TransactionType.READONLY);
                transactions.add(transaction);
                action = new BeginRO(transaction);
            }
            else if (line.startsWith("begin")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                Transaction transaction = new Transaction(transactionId, TransactionType.BOTH);
                transactions.add(transaction);
                action = new BeginAction(transaction);
            }
            else if (line.startsWith("fail")) {
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
                Site failedSite = null;
                for(Site s : sites){
                    if(s.getSiteId()==siteId){
                        failedSite = s;
                        break;
                    }
                }
                if(failedSite!=null){
                    action = new FailAction(failedSite);
                }
            }
            else if (line.startsWith("recover")) {
                int siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
                Site recoveredSite = null;
                for(Site s : sites){
                    if(s.getSiteId()==siteId){
                        recoveredSite = s;
                        break;
                    }
                }
                if(recoveredSite!=null){
                    action = new RecoverAction(recoveredSite);
                }
            }
            else if (line.startsWith("end")) {
                String transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                Transaction endTransaction = null;
                for(Transaction t : transactions){
                    if(t.getTransactionId()==transactionId){
                        endTransaction = t;
                        break;
                    }
                }
                if(endTransaction!=null){
                    action = new EndAction(endTransaction);
                }
            }
            else if (line.startsWith("dump")) {
                action = new DumpAction();
            }
            else if (line.startsWith("R")) {
                String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                String transactionId = fields.split(",")[0];
                Transaction readTransaction = null;
                for(Transaction t : transactions){
                    if(t.getTransactionId()==transactionId){
                        readTransaction = t;
                        break;
                    }
                }
                String variable = fields.split(",")[1];
            }
            this.processAction(action);
        }
    }



}
