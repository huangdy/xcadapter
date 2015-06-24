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
    public static final String FN_Latitude = "latitude";
    public static final String FN_Longitude = "longitude";
    public static final String FN_Title = "title";
    public static final String FN_TitlePrefix = "title.prefix";
    public static final String FN_Category = "category";
    public static final String FN_CategoryPrefix = "category.prefix";
    public static final String FN_CategoryFixed = "category.fixed";
    public static final String FN_FilterName = "filter";
    public static final String FN_FilterText = "filter.text";
    public static final String FN_Distance = "distance";
    public static final String FN_DistanceFilterText = "distance.filter.text";
    public static final String FN_Index = "index";
    public static final String FN_Description = "description";
    public static final String FN_URLHost = "url.host";
    public static final String FN_Username = "url.username";
    public static final String FN_Password = "url.password";
    public static final String FN_RedirectUrl = "url.redirectUrl";
    public static final String urlPostfix = "/core/ws/services";

    public static final String[] DefinedColumnNames = new String[] {
        FN_Title,
        FN_Category,
        FN_Latitude,
        FN_Longitude,
        FN_FilterName,
        FN_Description,
    };

    @Id
    @Column
    private String id;
    private String title;
    private String titlePrefix;
    private String category = "";
    private String filter;
    private String filterText;
    private String distance = "";
    private String distanceFilterText = "";
    private String latitude;
    private String longitude;
    private String categoryPrefix = "";

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

    public String getCategoryPrefix() {

        return categoryPrefix;
    }

    public String getDescription() {

        return description;
    }

    public String getDistance() {

        return distance;
    }

    public String getDistanceFilterText() {

        return distanceFilterText;
    }

    public String getFieldValue(String columnName) {

        try {
            return (String) this.getClass().getDeclaredField(columnName).get(this);
        } catch (final Throwable e) {
            if (e instanceof NoSuchFieldException)
                return "";
            else {
                logger.error("getFieldValue: " + columnName + ": " + e.getMessage());
                return null;
            }
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

        if (key.equals(FN_Category))
            return getCategory();
        else if (key.equalsIgnoreCase(FN_Description))
            return getDescription();
        else if (key.equalsIgnoreCase(FN_FilterName))
            return getFilter();
        else if (key.equalsIgnoreCase(FN_Latitude))
            return getLatitude();
        else if (key.equalsIgnoreCase(FN_Longitude))
            return getLongitude();
        else if (key.equalsIgnoreCase(FN_Title))
            return getTitle();
        else if (key.equalsIgnoreCase(FN_Category))
            return getCategory();
        else if (key.equalsIgnoreCase(FN_Distance))
            return getDistance();
        else if (key.equalsIgnoreCase(FN_DistanceFilterText))
            return getDistanceFilterText();
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

    public void setCategoryPrefix(String categoryPrefix) {

        this.categoryPrefix = categoryPrefix;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setDistance(String distance) {

        this.distance = distance;
    }

    public void setDistanceFilterText(String distanceFilterText) {

        this.distanceFilterText = distanceFilterText;
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

        if (keyAndValue[0].equalsIgnoreCase(FN_Category))
            setCategory(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Title))
            setTitle(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_TitlePrefix))
            setTitlePrefix(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Latitude))
            setLatitude(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Longitude))
            setLongitude(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_FilterName))
            setFilter(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_FilterText))
            setFilterText(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Index))
            setIndex(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Description))
            setDescription(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_URLHost))
            setUri(keyAndValue[1] + urlPostfix);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Username))
            setUsername(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Password))
            setPassword(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_RedirectUrl))
            setRedirectUrl(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_CategoryPrefix))
            setCategoryPrefix(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_CategoryFixed))
            setCategoryFixed(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_Distance))
            setDistance(keyAndValue[1]);
        else if (keyAndValue[0].equalsIgnoreCase(FN_DistanceFilterText))
            setDistanceFilterText(keyAndValue[1]);
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
            map.put(getCategory(), FN_Category);
        if (getTitle().indexOf(".") == -1)
            map.put(getTitle(), FN_Title);
        if (getFilter().indexOf(".") == -1)
            map.put(getFilter(), FN_FilterName);
        map.put(getLatitude(), FN_Latitude);
        map.put(getLongitude(), FN_Longitude);

        return map;
    }
}
