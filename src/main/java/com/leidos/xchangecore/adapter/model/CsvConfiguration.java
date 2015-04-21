package com.leidos.xchangecore.adapter.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class CsvConfiguration
    implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CsvConfiguration.class);

    public static final String FieldName_Latitude = "latitude";
    public static final String FieldName_Longitude = "longitude";
    public static final String FieldName_Title = "title";
    public static final String FieldName_TitlePrefix = "title.prefix";
    public static final String FieldName_Category = "category";
    public static final String FieldName_FilterName = "filter";
    public static final String FieldName_FilterText = "filter.text";
    public static final String FieldName_Index = "index";
    public static final String FieldName_Description = "description";
    public static final String FieldName_URLHost = "url.host";
    public static final String FieldName_Username = "url.username";
    public static final String FieldName_Password = "url.password";
    public static final String FieldName_RedirectUrl = "url.redirectUrl";
    private static final String urlPostfix = "/core/ws/services";

    public static final String[] DefinedColumnNames = new String[] {
        FieldName_Title,
        FieldName_Category,
        FieldName_Latitude,
        FieldName_Longitude,
        FieldName_FilterName,
        FieldName_Description,
    };

    @Id
    @Column
    private String id;

    private String title;
    private String titlePrefix;
    private String category;
    private String filter;

    private String filterText;

    private String latitude;

    private String longitude;
    private String description = "title.category";
    private String index = "title.category.latitude.longitude";
    private String uri = "http://localhost";
    private String username = "xchangecore";
    private String password = "xchangecore";
    private String redirectUrl = "http://www.google.com";

    public String getCategory() {

        return this.category;
    }

    public String getDescription() {

        return this.description;
    }

    public String getFieldValue(String columnName) {

        try {
            return (String) this.getClass().getDeclaredField(columnName).get(this);
        } catch (final Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFilter() {

        return this.filter;
    }

    public String getFilterText() {

        return this.filterText;
    }

    public String getId() {

        return this.id;
    }

    public String getIndex() {

        return this.index;
    }

    public String getLatitude() {

        return this.latitude;
    }

    public String getLongitude() {

        return this.longitude;
    }

    public String getPassword() {

        return this.password;
    }

    public String getRedirectUrl() {

        return this.redirectUrl;
    }

    public String getTitle() {

        return this.title;
    }

    public String getTitlePrefix() {

        return this.titlePrefix;
    }

    public String getUri() {

        return this.uri;
    }

    public String getUsername() {

        return this.username;
    }

    public String getValue(String key) {

        if (key.equals(FieldName_Category)) {
            return this.getCategory();
        } else if (key.equalsIgnoreCase(FieldName_Description)) {
            return this.getDescription();
        } else if (key.equalsIgnoreCase(FieldName_FilterName)) {
            return this.getFilter();
        } else if (key.equalsIgnoreCase(FieldName_Latitude)) {
            return this.getLatitude();
        } else if (key.equalsIgnoreCase(FieldName_Longitude)) {
            return this.getLongitude();
        } else if (key.equalsIgnoreCase(FieldName_Title)) {
            return this.getTitle();
        } else {
            return null;
        }
    }

    public boolean isValid() {

        return ((this.getTitle() == null) || (this.getTitle().length() == 0)) ? false : true;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setFilter(String filter) {

        this.filter = filter;
    }

    public void setFilterText(String filterText) {

        this.filterText = filterText;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setIndex(String index) {

        this.index = index;
    }

    public void setKeyValue(final String[] keyAndValue) {

        logger.debug("key/value: [" + keyAndValue[0] + "/" + keyAndValue[1] + "]");

        if (keyAndValue[0].equalsIgnoreCase(FieldName_Category)) {
            this.setCategory(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Title)) {
            this.setTitle(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_TitlePrefix)) {
            this.setTitlePrefix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Latitude)) {
            this.setLatitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Longitude)) {
            this.setLongitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_FilterName)) {
            this.setFilter(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_FilterText)) {
            this.setFilterText(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Index)) {
            this.setIndex(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Description)) {
            this.setDescription(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_URLHost)) {
            this.setUri(keyAndValue[1] + urlPostfix);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Username)) {
            this.setUsername(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_Password)) {
            this.setPassword(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FieldName_RedirectUrl)) {
            this.setRedirectUrl(keyAndValue[1]);
        } else {
            logger.warn("Invalid Key/Value: [" + keyAndValue[0] + "/" + keyAndValue[1] + "]");
        }
    }

    public void setLatitude(String latitude) {

        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {

        this.longitude = longitude;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public void setRedirectUrl(String redirectUrl) {

        this.redirectUrl = redirectUrl;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setTitlePrefix(String titlePrefix) {

        this.titlePrefix = titlePrefix;
    }

    public void setUri(String uri) {

        this.uri = uri;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public Map<String, String> toMap() {

        final HashMap<String, String> map = new HashMap<String, String>();

        if (this.getCategory().indexOf(".") == -1) {
            map.put(this.getCategory(), FieldName_Category);
        }
        if (this.getTitle().indexOf(".") == -1) {
            map.put(this.getTitle(), FieldName_Title);
        }
        if (this.getFilter().indexOf(".") == -1) {
            map.put(this.getFilter(), FieldName_FilterName);
        }
        map.put(this.getLatitude(), FieldName_Latitude);
        map.put(this.getLongitude(), FieldName_Longitude);

        return map;
    }
}
