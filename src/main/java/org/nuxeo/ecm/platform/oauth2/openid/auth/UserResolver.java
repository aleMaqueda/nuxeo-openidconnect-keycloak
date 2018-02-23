/*
 * (C) Copyright 2006-2013 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nelson Silva <nelson.silva@inevo.pt> - initial API and implementation
 *     Nuxeo
 */
package org.nuxeo.ecm.platform.oauth2.openid.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProvider;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public abstract class UserResolver {

    private static final Log log = LogFactory.getLog(UserResolver.class);

    private OpenIDConnectProvider provider;

    protected static String userSchemaName = "user";

    protected static String groupSchemaName = "group";

    public UserResolver(OpenIDConnectProvider provider) {
        this.provider = provider;
    }

    public OpenIDConnectProvider getProvider() {
        return provider;
    }

    protected abstract String findNuxeoUser(OpenIDUserInfo userInfo);

    protected  DocumentModel createNuxeoUser(String nuxeoLogin, UserManager userManager) {
        DocumentModel userDoc;

        try {
            //UserManager userManager = Framework.getLocalService(UserManager.class);

            userDoc = userManager.getBareUserModel();
            userDoc.setPropertyValue(userManager.getUserIdField(), nuxeoLogin);

            userDoc = userManager.createUser(userDoc);

        } catch (NuxeoException e) {
            log.error("Error while creating user " + nuxeoLogin + "in UserManager", e);
            return null;
        }

        return userDoc;
    }

    protected abstract DocumentModel updateUserInfo(DocumentModel user, OpenIDUserInfo userInfo);
    private DocumentModel updateUser(DocumentModel userDoc, OpenIDUserInfo userInfo, UserManager userManager) {
        log.info(provider.getName());
        try {
            log.debug(userInfo.getName());
            log.info(userInfo.getGivenName());
            log.info(userInfo.getGroups().toArray().length);
            log.info(userInfo.getRoles().toArray().length);
            //UserManager userManager = Framework.getLocalService(UserManager.class);
            userDoc.setProperty(userSchemaName, "firstName", userInfo.getGivenName());
            userDoc.setProperty(userSchemaName, "lastName", userInfo.getFamilyName());
            userDoc.setProperty(userSchemaName, "groups", userInfo.getGroups());
            userManager.updateUser(userDoc); // create workspace with firstname lastname

        } catch (NuxeoException e) {
            log.error("Error while update user " + "in UserManager", e);
            return null;
        }

        return userDoc;
    }
    public String findOrCreateNuxeoUser(OpenIDUserInfo userInfo) {
        String user = findNuxeoUser(userInfo);

        UserManager userManager = Framework.getLocalService(UserManager.class);
        if (user == null) {

            if(userInfo.getEmail() == null || userInfo.getEmail().trim().isEmpty()){
                user = generateRandomUserId();
            }else{
                user = userInfo.getEmail();
            }
            if(provider.getName().equals("keycloak")){
                for (String group : userInfo.getGroups()) {
                    log.info(group);
                    findOrCreateGroup(group,userInfo.getName(),userManager);
                }
            }
            DocumentModel userDoc = createNuxeoUser(user, userManager);
            updateUserInfo(userDoc, userInfo);
            if(provider.getName().equals("keycloak")){
                updateUser(userDoc,userInfo, userManager);
            }
        }
        return user;
    }
    private DocumentModel findOrCreateGroup(String group, String userName, UserManager userManager) {
        DocumentModel groupDoc = findGroup(group, userManager);

        if (groupDoc == null) {
            groupDoc = userManager.getBareGroupModel();
            groupDoc.setPropertyValue(userManager.getGroupIdField(), group);
            groupDoc.setProperty(groupSchemaName, "groupname", group);
            groupDoc.setProperty(groupSchemaName, "grouplabel", group);
            groupDoc.setProperty(groupSchemaName, "description",
                    "Group automatically created by Keycloak based on user group [" + group + "]");
            groupDoc = userManager.createGroup(groupDoc);
        }
        List<String> users = userManager.getUsersInGroupAndSubGroups(group);
        if (!users.contains(userName)) {
            log.info("insert user to group");
            users.add(userName);
            groupDoc.setProperty(groupSchemaName, userManager.getGroupMembersField(), users);
            userManager.updateGroup(groupDoc);
        }
        log.info(groupDoc.getName());
        log.info(groupDoc.getId());
        log.info(groupDoc.toString());
        return groupDoc;
    }
    private DocumentModel findGroup(String group, UserManager userManager) {
        Map<String, Serializable> query = new HashMap<>();
        log.info(group);
        query.put(userManager.getGroupIdField(), group);
        DocumentModelList groups = userManager.searchGroups(query, null);
        log.info(groups.isEmpty());
        if (groups.isEmpty()) {
            return null;
        }
        log.info(groups.get(0).getId());
        log.info(groups.size());
        log.info(groups.get(0).getType());
        return groups.get(0);
    }
    protected String generateRandomUserId() {
        String userId = null;

        try {
            UserManager userManager = Framework.getLocalService(UserManager.class);
            List<String> userIds = userManager.getUserIds();

            while (userId == null || userIds.contains(userId)) {
                userId = "user_" + RandomStringUtils.randomNumeric(4);
            }
        } catch (NuxeoException e) {
            log.error("Error while generating random user id", e);
            return null;
        }
        return userId;
    }
}
