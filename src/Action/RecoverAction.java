/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/28/2021
 */
package Action;

import Site.Site;

/**
 * RecoverAction subclass with a constructor and getters/setters.
 */
public class RecoverAction extends Action {
    private Site site;

    public RecoverAction(Site site) {
        this.operation = Operations.RECOVER;
        this.site = site;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
