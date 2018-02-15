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

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProvider;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public abstract class UserResolver {

    private static final Log log = LogFactory.getLog(UserResolver.class);

    private OpenIDConnectProvider provider;

    public UserResolver(OpenIDConnectProvider provider) {
        this.provider = provider;
    }

    public OpenIDConnectProvider getProvider() {
        return provider;
    }

    protected abstract String findNuxeoUser(OpenIDUserInfo userInfo);

    protected  DocumentModel createNuxeoUser(String nuxeoLogin) {
        DocumentModel userDoc;

        try {
            UserManager userManager = Framework.getLocalService(UserManager.class);

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

    public String findOrCreateNuxeoUser(OpenIDUserInfo userInfo) {
        String user = findNuxeoUser(userInfo);
        if (user == null) {
            user = generateRandomUserId();
            DocumentModel userDoc = createNuxeoUser(user);
            updateUserInfo(userDoc, userInfo);
        }
        return user;
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
