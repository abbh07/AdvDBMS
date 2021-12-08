/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/27/2021
 */
package Action;

import Site.Site;

/**
 * FailAction subclass with a constructor and getters and setters.
 */
public class FailAction extends Action {
    private Site site;

    public FailAction(Site site) {
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
