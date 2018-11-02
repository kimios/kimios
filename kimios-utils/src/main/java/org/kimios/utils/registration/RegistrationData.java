/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.utils.registration;

import java.util.HashMap;
import java.util.Map;

public class RegistrationData {

    private String firstname;
    private String lastname;
    private String email;
    private String company;
    private String city;
    private String state;
    private String zipCode;
    private String occupation;
    private String comment;
    private Map<String, String> additionalDatas = new HashMap<>();

    public RegistrationData() {
    }

    public RegistrationData(String firstname, String lastname, String email, String company, String city, String state, String zipCode, String occupation, String comment, Map<String, String> additionalDatas) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.company = company;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.occupation = occupation;
        this.comment = comment;
        this.additionalDatas = additionalDatas;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firtname) {
        this.firstname = firtname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, String> getAdditionalDatas() {
        return additionalDatas;
    }

    public void setAdditionalDatas(Map<String, String> additionalDatas) {
        this.additionalDatas = additionalDatas;
    }

    @Override
    public String toString() {
        return "RegistrationData{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", occupation='" + occupation + '\'' +
                ", comment='" + comment + '\'' +
                ", additionalDatas=" + additionalDatas +
                '}';
    }
}
