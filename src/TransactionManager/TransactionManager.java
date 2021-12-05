package TransactionManager;

import Site.Site;
import Transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private List<Transaction> transactions;
    private List<Site> sites;

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

    public void process(String line){
        //Process the input
    }



}
