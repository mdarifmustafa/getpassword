package com.whodesire.data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Scope("prototype")
public class Resource {

    private String resourceName;
    private String description;
    private Date createdOn;
    private Integer expireInDays;
    private List<char[]> secret;
    private Map<String, char[]> excessSpare;
    private boolean expired;
    private final static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getExpireInDays() {
        return expireInDays;
    }

    public void setExpireInDays(Integer expireInDays) {
        this.expireInDays = expireInDays;
    }

    public List<char[]> getSecret() {
        return secret;
    }

    public void setSecret(List<char[]> secret) {
        this.secret = secret;
    }

    public Map<String, char[]> getExcessSpare() {
        return excessSpare;
    }

    public void setExcessSpare(Map<String, char[]> excessSpare) {
        this.excessSpare = excessSpare;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public static DateFormat getDefaultDateFormat(){
//        System.out.println(dateFormat.toString());
        return dateFormat;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceName='" + resourceName + '\'' +
                ", description='" + description + '\'' +
                ", createdOn=" + createdOn +
                ", expireInDays=" + expireInDays +
                ", secret=" + secret +
                ", excessSpare=" + excessSpare +
                ", expired=" + expired +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;

        Resource resource = (Resource) o;

        if (expired != resource.expired) return false;
        if (!resourceName.equals(resource.resourceName)) return false;
        if (!description.equals(resource.description)) return false;
        if (!createdOn.equals(resource.createdOn)) return false;
        if (!expireInDays.equals(resource.expireInDays)) return false;
        if (!secret.equals(resource.secret)) return false;
        return excessSpare.equals(resource.excessSpare);
    }

    @Override
    public int hashCode() {
        int result = resourceName.hashCode();
        result = 31 * result + createdOn.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + expireInDays.hashCode();
        result = 31 * result + secret.hashCode();
        result = 31 * result + excessSpare.hashCode();
        result = 31 * result + (expired ? 1 : 0);
        return result;
    }

    public final boolean findByResourceName(String resourceName){
        if(this.resourceName.equals(resourceName))
            return true;
        else
            return false;
    }

}
