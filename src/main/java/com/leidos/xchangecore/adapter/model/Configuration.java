package com.leidos.xchangecore.adapter.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration
implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static final String N_Configuration_Start = "configuration:start";
    public static final String N_Configuration_End = "configuration:end";

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
    public static final String FN_AutoClose = "auto.close";
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
        FN_Index,
        FN_Description,
    };

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
    private boolean autoClose = false;
    private String uri = "http://localhost";
    private String username = "xchangecore";
    private String password = "xchangecore";
    private String redirectUrl = "http://www.google.com";

    public String getCategory() {

        return this.category;
    }

    public String getCategoryFixed() {

        return this.categoryFixed;
    }

    public String getCategoryPrefix() {

        return this.categoryPrefix;
    }

    public String getDescription() {

        return this.description;
    }

    public String getDistance() {

        return this.distance;
    }

    public String getDistanceFilterText() {

        return this.distanceFilterText;
    }

    public String getFieldValue(String columnName) {

        try {
            return (String) this.getClass().getDeclaredField(columnName).get(this);
        } catch (final Throwable e) {
            if (e instanceof NoSuchFieldException) {
                return "";
            } else {
                logger.error("getFieldValue: " + columnName + ": " + e.getMessage());
                return null;
            }
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

        if (key.equals(FN_Category)) {
            return this.getCategory();
        } else if (key.equalsIgnoreCase(FN_Description)) {
            return this.getDescription();
        } else if (key.equalsIgnoreCase(FN_FilterName)) {
            return this.getFilter();
        } else if (key.equalsIgnoreCase(FN_Latitude)) {
            return this.getLatitude();
        } else if (key.equalsIgnoreCase(FN_Longitude)) {
            return this.getLongitude();
        } else if (key.equalsIgnoreCase(FN_Title)) {
            return this.getTitle();
        } else if (key.equalsIgnoreCase(FN_Category)) {
            return this.getCategory();
        } else if (key.equalsIgnoreCase(FN_Distance)) {
            return this.getDistance();
        } else if (key.equalsIgnoreCase(FN_DistanceFilterText)) {
            return this.getDistanceFilterText();
        } else if (key.equalsIgnoreCase(FN_Index)) {
            return this.getIndex();
        } else {
            return null;
        }
    }

    public boolean isAutoClose() {

        return autoClose;
    }

    public boolean isValid() {

        return this.title != null && this.index != null && this.category != null &&
            this.description != null && this.filter != null && this.latitude != null &&
            this.longitude != null ? true : false;
    }

    private void setAutoClose(String ac) {

        this.autoClose = ac.equalsIgnoreCase("true") == true || ac.equals("1") == true ? true : false;
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

        if (keyAndValue[0].equalsIgnoreCase(FN_Category)) {
            this.setCategory(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Title)) {
            this.setTitle(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_TitlePrefix)) {
            this.setTitlePrefix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Latitude)) {
            this.setLatitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Longitude)) {
            this.setLongitude(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_FilterName)) {
            this.setFilter(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_FilterText)) {
            this.setFilterText(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Index)) {
            this.setIndex(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Description)) {
            this.setDescription(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_URLHost)) {
            this.setUri(keyAndValue[1] + urlPostfix);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Username)) {
            this.setUsername(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Password)) {
            this.setPassword(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_RedirectUrl)) {
            this.setRedirectUrl(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_CategoryPrefix)) {
            this.setCategoryPrefix(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_CategoryFixed)) {
            this.setCategoryFixed(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_Distance)) {
            this.setDistance(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_DistanceFilterText)) {
            this.setDistanceFilterText(keyAndValue[1]);
        } else if (keyAndValue[0].equalsIgnoreCase(FN_AutoClose)) {
            this.setAutoClose(keyAndValue[1]);
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
            map.put(this.getCategory(), FN_Category);
        }
        if (this.getTitle().indexOf(".") == -1) {
            map.put(this.getTitle(), FN_Title);
        }
        if (this.getFilter().indexOf(".") == -1) {
            map.put(this.getFilter(), FN_FilterName);
        }
        map.put(this.getLatitude(), FN_Latitude);
        map.put(this.getLongitude(), FN_Longitude);

        return map;
    }
}
