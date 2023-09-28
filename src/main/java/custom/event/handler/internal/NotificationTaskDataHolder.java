package custom.event.handler.internal;

import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.user.core.service.RealmService;

public class NotificationTaskDataHolder {

    private static volatile NotificationTaskDataHolder accountServiceDataHolder = new NotificationTaskDataHolder();
    private IdentityEventService identityEventService;
    private RealmService realmService;

    public static NotificationTaskDataHolder getInstance() {
        return accountServiceDataHolder;
    }
    public IdentityEventService getIdentityEventService() {
        return identityEventService;
    }

    public void setIdentityEventService(IdentityEventService identityEventService) {
        this.identityEventService = identityEventService;
    }

    public RealmService getIdentityRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }
}
