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
    public static final String FieldName_CategoryFixed = "category.fixed";
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
    private String categoryFixed = "";
    private String description = "title.category";
    private String index = "title.category.latitude.longitude";
    private String uri = "http://localhost";
    private String username = "xchangecore";
    private String password = "xchangecore";
    private String redirectUrl = "http://www.google.com";

    public String getCategory() {

        return category;
    }

    public String getCategoryFixed() {

        return categoryFixed;
    }

    public String getDescription() {

        return description;
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

        return filter;
    }

    public String getFilterText() {

        return filterText;
    }

    public String getId() {

        return id;
    }

    public String getIndex() {

        return index;
    }

    public String getLatitude() {

        return latitude;
    }

    public String getLongitude() {

        return longitude;
    }

    public String getPassword() {

        return password;
    }

    public String getRedirectUrl() {

        return redirectUrl;
    }

    public String getTitle() {

        return title;
    }

    public String getTitlePrefix() {

        return titlePrefix;
    }

    public String getUri() {

        return uri;
    }

    public String getUsername() {

        return username;
    }

    public String getValue(String key) {

        if (key.equals(FieldName_Category))
            return getCategory();
        else if (key.equalsIgnoreCase(FieldName_Description))
            return getDescription();
        else if (key.equalsIgnoreCase(FieldName_FilterName))
            return getFilter();
        else if (key.equalsIgnoreCase(FieldName_Latitude))
            return getLatitude();
        else if (key.equalsIgnoreCase(FieldName_Longitude))
            return getLongitude();
        else if (key.equalsIgnoreCase(FieldName_Title))
            return getTitle();
        else if (key.equalsIgnoreCase(FieldName_Category))
            return getCategoryFixed();
        else
            return null;
    }

    public boolean isValid() {

        return getTitle() == null || getTitle().length() == 0 ? false : true;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public void setCategoryFixed(String categoryFixed) {

        this.categoryFixed = categoryFixed;
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

        if (keyAndValue[0].equalsIgnoreCase(FieldName_Category))
            setCategory(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Title))
            setTitle(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_TitlePrefix))
            setTitlePrefix(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Latitude))
            setLatitude(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Longitude))
            setLongitude(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_FilterName))
            setFilter(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_FilterText))
            setFilterText(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Index))
            setIndex(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Description))
            setDescription(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_URLHost))
            setUri(keyAndValue[1] + urlPostfix);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Username))
            setUsername(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_Password))
            setPassword(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_RedirectUrl))
            setRedirectUrl(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FieldName_CategoryFixed))
            setCategoryFixed(keyAndValue[1]);
        else
            logger.warn("Invalid Key/Value: [" + keyAndValue[0] + "/" + keyAndValue[1] + "]");
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

        if (getCategory().indexOf(".") == -1)
            map.put(getCategory(), FieldName_Category);
        if (getTitle().indexOf(".") == -1)
            map.put(getTitle(), FieldName_Title);
        if (getFilter().indexOf(".") == -1)
            map.put(getFilter(), FieldName_FilterName);
        map.put(getLatitude(), FieldName_Latitude);
        map.put(getLongitude(), FieldName_Longitude);

        return map;
    }
}
