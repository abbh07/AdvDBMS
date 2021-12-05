package Action;

import Site.Site;
import Transaction.Transaction;

public class FailAction extends Action{
    private Site site;
    public FailAction(Site site){
        this.operation = Operations.FAIL;
        this.site = site;
    }
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
