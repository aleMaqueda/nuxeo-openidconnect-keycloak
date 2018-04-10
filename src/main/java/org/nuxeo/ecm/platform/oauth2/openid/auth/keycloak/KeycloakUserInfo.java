package org.nuxeo.ecm.platform.oauth2.openid.auth.keycloak;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;
import org.nuxeo.ecm.platform.oauth2.openid.auth.OpenIDUserInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KeycloakUserInfo extends GenericJson implements OpenIDUserInfo {

    protected static String membersname = "members";

    @Key("user-client-role")
    protected List<String> roles;

    @Key("group")
    protected List<String> groups;

    @Key("sub")
    protected String subject;

    @Key("name")
    protected String name;

    @Key("given_name")
    protected String givenName;

    @Key("family_name")
    protected String familyName;

    @Key("middle_name")
    protected String middleName;

    @Key("nickname")
    protected String nickname;

    @Key("preferred_username")
    protected String preferredUsername;

    @Key("profile")
    protected String profile;

    @Key("picture")
    protected String picture;

    @Key("website")
    protected String website;

    @Key("email")
    protected String email;

    @Key("email_verified")
    protected boolean emailVerified;

    @Key("gender")
    protected String gender;

    @Key("birthdate")
    protected Date birthdate;

    @Key("zoneinfo")
    protected String zoneInfo;

    @Key("locale")
    protected String locale;

    @Key("phone_number")
    protected String phoneNumber;

    @Key("address")
    protected String address;

    @Key("updated_time")
    protected String updatedTime;

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getPreferredUsername() {
        return preferredUsername;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public String getPicture() {
        return picture;
    }

    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public Date getBirthdate() {
        return birthdate;
    }

    @Override
    public String getZoneInfo() {
        return zoneInfo;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public Date getUpdatedTime() {
        Date date;
        try {
            DateTime dateTime = DateTime.parseRfc3339(updatedTime);
            date = new Date(dateTime.getValue());
        } catch (NumberFormatException e) {
            return null;
        }
        return date;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getGroups() {
        boolean members = false;
        if(groups != null){
            if(groups.size() > 0){
                for (int i = 0; i < groups.size(); i++){
                    groups.set(i,groups.get(i).replaceAll("/", ""));
                    if(groups.get(i).equals(membersname)){
                        members = true;
                    }
                }
            }
        }else{
            groups = new ArrayList<>();
        }
        if(!members){
            groups.add(membersname);
        }
        return groups;
    }

    @Override
    public String toString() {
        return "KeycloakUserInfo{" +
                "#roles=" + roles.size() +
                ",#groups=" + groups.size() +
                ", subject='" + subject + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", preferredUsername='" + preferredUsername + '\'' +
                ", profile='" + profile + '\'' +
                ", picture='" + picture + '\'' +
                ", website='" + website + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", gender='" + gender + '\'' +
                ", birthdate=" + birthdate +
                ", zoneInfo='" + zoneInfo + '\'' +
                ", locale='" + locale + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", updatedTime='" + updatedTime + '\'' +
                '}';
    }
}
