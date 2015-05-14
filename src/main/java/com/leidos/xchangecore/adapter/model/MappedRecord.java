package com.leidos.xchangecore.adapter.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MappedRecord
    implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String title;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String category = "N/A";

    @Column(columnDefinition = "VARCHAR(65536)")
    private String content = "N/A";

    @Column(columnDefinition = "VARCHAR(65536)")
    private String description = "N/A";

    @Column(columnDefinition = "VARCHAR(4096)")
    private String index;

    private String creator;
    private String filter;
    private String igID = null;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String workProductID = null;
    private String latitude;
    private String longitude;
    private String distance;
    private String distanceFilterText;

    private Date lastUpdated;

    public String getCategory() {

        return this.category;
    }

    public String getContent() {

        return this.content;
    }

    public String getCreator() {

        return this.creator;
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

    public String getFilter() {

        return this.filter;
    }

    public Integer getId() {

        return this.id;
    }

    public String getIgID() {

        return this.igID;
    }

    public String getIndex() {

        return this.index;
    }

    public Date getLastUpdated() {

        return this.lastUpdated;
    }

    public String getLatitude() {

        return this.latitude;
    }

    public String getLongitude() {

        return this.longitude;
    }

    public String getTitle() {

        return this.title;
    }

    public String getWorkProductID() {

        return this.workProductID;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void setCreator(String creator) {

        this.creator = creator;
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

    public void setId(Integer id) {

        this.id = id;
    }

    public void setIgID(String igID) {

        this.igID = igID;
    }

    public void setIndex(String index) {

        this.index = index;
    }

    public void setLastUpdated(Date lastUpdated) {

        this.lastUpdated = lastUpdated;
    }

    public void setLatitude(String latitude) {

        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {

        this.longitude = longitude;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setWorkProductID(String workProductID) {

        this.workProductID = workProductID;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer();
        if (this.id != null) {
            sb.append("\n\tID: " + this.id);
        }
        sb.append("\n\tTitle: " + this.title);
        sb.append("\n\tCategory: " + this.category);
        sb.append("\n\tLat/Lon: " + this.latitude + "/" + this.longitude);
        sb.append("\n\tIndex Key: " + this.index);
        sb.append("\n\tFilter: " + this.filter);
        sb.append("\n\tDescription: " + this.description);
        sb.append("\n\tContent:  " + this.content);
        if (this.distance != null) {
            sb.append("\n\tDistance: " + this.distance);
        }
        if (this.distanceFilterText != null) {
            sb.append("\n\tDistance.Filter.Text: " + this.distanceFilterText);
        }
        if (this.workProductID != null) {
            sb.append("\n\tProductID: " + this.workProductID);
        }
        if (this.igID != null) {
            sb.append("\n\tIGID: " + this.igID);
        }
        sb.append("\n");
        return sb.toString();
    }
}
