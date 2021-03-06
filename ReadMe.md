## About nuxeo-platform-login-openid 9.2

This module contribute a new Login Plugin that can use OpenId to authenticate the user.

OpenId providers links will added to the login screen.

## Sample configuration for Google OpenID

You must first declare your Nuxeo Web Application to Google so that you can get the clientId and ClientSecret.

For that, go to https://code.google.com/apis/console > API Access > Create > Web Application

Once you have the clientId/clientSecret, and the accepted redirect url (like http://demo.nuxeo.com/nuxeo/nxstartup.faces?provider=GoogleOpenIDConnect&forceAnonymousLogin=true) create `nxserver/config/openid-config.xml`

    <?xml version="1.0"?>
    <component name="org.nuxeo.ecm.platform.oauth2.openid.google.testing" version="1.0">
      <require>org.nuxeo.ecm.platform.oauth2.openid.google</require>
      <extension target="org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProviderRegistry" point="providers">
       <provider>
        <name>GoogleOpenIDConnect</name>
        <clientId><!--enter your clientId here --></clientId>
        <clientSecret><!--enter your clientSecret key here --></clientSecret>
       </provider>
      </extension>
    </component>

## Sample configuration for Keycloak (3.3.0):

In actual plugin:

Change definition resources/OSGI-INF/openid-keycloak-contrib.xml

	<?xml version="1.0"?>
	<component name="org.nuxeo.ecm.platform.oauth2.openid.keycloak" version="1.0">
	    <requires>org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProviderRegistry</requires>
	    <extension target="org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProviderRegistry" point="providers">
		<provider>
		    <name>keycloak</name>
		    <label>keycloak</label>
		    <description>Login using your keycloak account</description>
		    <authorizationServerURL>http://localhost:9080/auth/realms/jhipster/protocol/openid-connect/auth</authorizationServerURL>
		    <tokenServerURL>http://localhost:9080/auth/realms/jhipster/protocol/openid-connect/token</tokenServerURL>
		    <userInfoURL>http://localhost:9080/auth/realms/jhipster/protocol/openid-connect/userinfo</userInfoURL>
		    <scope>openid</scope>
		    <userInfoClass>org.nuxeo.ecm.platform.oauth2.openid.auth.keycloak.KeycloakUserInfo</userInfoClass>
		</provider>
	    </extension>
	</component>

In nuxeo server:

Declare in "templates/openid/nuxeo.defaults"

    nuxeo.openid.keycloak.client.id=
    nuxeo.openid.keycloak.secret.id=


You must first declare your Nuxeo Web Application to Keycloak so that you can get the clientId and ClientSecret.

templates/openid/config/openid-login-config.xml.nxftl


    <#if "${nuxeo.openid.keycloak.client.id}" != "">
      <require>org.nuxeo.ecm.platform.oauth2.openid.keycloak</require>
      <extension target="org.nuxeo.ecm.platform.oauth2.openid.OpenIDConnectProviderRegistry" point="providers">
        <provider>
          <name>keycloak</name>
          <clientId>${nuxeo.openid.keycloak.client.id}</clientId>
          <clientSecret>${nuxeo.openid.keycloak.client.secret}</clientSecret>
        </provider>
      </extension>
    </#if>

In "nuxeo.conf":

    nuxeo.openid.keycloak.client.id=${nuxeo-client}
    nuxeo.openid.keycloak.secret.id=${nuxeo-secret}
    nuxeo.oauth.auth.create.user=true

Replace nuxeo-platform-login-openid-9.2.jar in:

- nxserver/bundles

- packages/store/openid-authentication-1.2.1/install/bundles

- packages/backup/nxserver/bundles
