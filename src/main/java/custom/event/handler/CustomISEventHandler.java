package custom.event.handler;

import custom.event.handler.internal.NotificationTaskDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;

import java.util.HashMap;
import java.util.Map;

public class CustomISEventHandler extends AbstractEventHandler {

    private static Log log = LogFactory.getLog(CustomISEventHandler.class);

    @Override
    public void handleEvent(Event event) throws IdentityEventException {
        log.info("Custom event handler received events successfully.");
        String eventName = "POST_AUTHENTICATION";
        if (eventName.equals(event.getEventName())) {
            Map<String, Object> eventProperties = event.getEventProperties();
            String userName = (String) eventProperties.get(IdentityEventConstants.EventProperty.USER_NAME);
            String username = (String) eventProperties.get("user-name");
            try {
                UserRealm primaryUserRealm = NotificationTaskDataHolder.getInstance().getIdentityRealmService().
                        getTenantUserRealm((Integer) eventProperties.get("tenantId"));
                UserStoreManager primaryUserStoreManager = primaryUserRealm.getUserStoreManager();
                try {
                    // Perform the search in the primary user store
                    boolean userExistsInPrimaryStore = primaryUserStoreManager.isExistingUser(username);
                    boolean userExistsInSecondaryStore = false;
                    Map<String, String> claimValues = null;
                    HashMap<String, Object> properties = new HashMap<>();
                    // Replace with the claim URIs you want to retrieve
                    String[] claimURIs = {"http://wso2.org/claims/emailaddress", "http://wso2.org/claims/lastname"};
                    if (userExistsInPrimaryStore) {
                        claimValues = primaryUserStoreManager.getUserClaimValues(username, claimURIs, null);
                    } else {
                        // Get a secondary user store manager
                        UserStoreManager secondaryUserStoreManager =
                                ((AbstractUserStoreManager) primaryUserStoreManager).getSecondaryUserStoreManager();
                        userExistsInSecondaryStore = secondaryUserStoreManager.isExistingUser(username);
                        if (userExistsInSecondaryStore) {
                            claimValues = secondaryUserStoreManager.getUserClaimValues(username, claimURIs, null);
                        }
                    }
                    if (!userExistsInPrimaryStore && !userExistsInSecondaryStore) {
                        log.error("User not found in any user store.");
                    } else {
                        if (claimValues != null) {
                            properties.put("send-to", claimValues.get("http://wso2.org/claims/emailaddress"));
                            properties.put("user-name", username);
                            properties.put("tenant-domain", eventProperties.get("tenant-domain"));
                            properties.put("TEMPLATE_TYPE", "CustomLoginSuccessful");
                            Event identityMgtEvent = new Event(IdentityEventConstants.Event.TRIGGER_NOTIFICATION, properties);
                            NotificationTaskDataHolder.getInstance().getIdentityEventService().handleEvent(identityMgtEvent);
                        }
                    }
                } catch (org.wso2.carbon.user.api.UserStoreException e) {
                    // Handle exceptions
                    log.error("User store exception occurred when searching the user.", e);
                }
            } catch (UserStoreException e) {
                log.error("Error occurred while sending email to: " + eventProperties.get("user-name"), e);
            }
        }
    }

    @Override
    public String getName() {
        return "customEmailEventHandler";
    }

}
