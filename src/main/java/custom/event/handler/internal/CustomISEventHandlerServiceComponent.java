package custom.event.handler.internal;

import custom.event.handler.CustomISEventHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * Custom Password Grant Service Component
 */
@Component(
        name = "customEmailEventHandler",
        immediate = true
)
public class CustomISEventHandlerServiceComponent {
    private static Log log = LogFactory.getLog(CustomISEventHandlerServiceComponent.class);

    protected void activate(ComponentContext componentContext) {
        log.info("Activeting CustomISEventHandler !!");
        CustomISEventHandler customGrant = new CustomISEventHandler();
        componentContext.getBundleContext().registerService(AbstractEventHandler.class.getName(), customGrant, null);
        log.info("Custom grant activated successfully.");
    }

    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Custom grant is deactivated.");
        }
    }

    protected void unsetIdentityEventService(IdentityEventService eventService) {

        NotificationTaskDataHolder.getInstance().setIdentityEventService(null);
    }

    @Reference(
            name = "EventMgtService",
            service = org.wso2.carbon.identity.event.services.IdentityEventService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityEventService")
    protected void setIdentityEventService(IdentityEventService eventService) {

        NotificationTaskDataHolder.getInstance().setIdentityEventService(eventService);
    }

    @Reference(
            name = "user.realmservice.default",
            service = org.wso2.carbon.user.core.service.RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        NotificationTaskDataHolder.getInstance().setRealmService(realmService);
        if (log.isDebugEnabled()) {
            log.debug("RealmService is set in the User Store Count bundle");
        }
    }

    protected void unsetRealmService(RealmService realmService) {

        NotificationTaskDataHolder.getInstance().setRealmService(null);
        if (log.isDebugEnabled()) {
            log.debug("RealmService is unset in the Application Authentication Framework bundle");
        }
    }
}
