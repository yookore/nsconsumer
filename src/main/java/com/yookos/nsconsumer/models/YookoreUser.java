package com.yookos.nsconsumer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by jome on 2016/01/22.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class YookoreUser {
    private String type;
    private String userId;
    private String username;
    private String fullNames;
    private String firstName;
    private String lastName;
    private Preference preference;
    private List<String> registrationIds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullNames() {
        if(fullNames.trim().length() == 0){
            String fullname =firstName + " " + lastName;
            return fullname.trim();
        }else{
            return fullNames;
        }
    }

    public void setFullNames(String fullNames) {
        this.fullNames = fullNames;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public List<String> getRegistrationIds() {
        return registrationIds;
    }

    public void setRegistrationIds(List<String> registrationIds) {
        this.registrationIds = registrationIds;
    }

    @Override
    public String toString() {
        return "YookoreUser{" +
                "type='" + type + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullNames='" + fullNames + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", preference=" + preference +
                '}';
    }
}
