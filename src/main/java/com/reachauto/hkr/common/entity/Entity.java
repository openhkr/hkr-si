package com.reachauto.hkr.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reachauto.hkr.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haojr on 17/06/05.
 */
@ApiModel
public class Entity implements Serializable {

    private static final long serialVersionUID = -9073095998693952756L;

    @ApiModelProperty(position = 0, value = "唯一索引标识")
    private Long id;
    @ApiModelProperty(position = 31, value = "备注")
    private String remarks = "";
    @JsonIgnore
    private Integer deleted = Constants.DEFAULT_DELETED;
    @ApiModelProperty(position = 32, value = "创建人标识")
    private String createdBy = "";
    @ApiModelProperty(position = 33, value = "创建时间")
    private Date createdAt = new Date();
    @ApiModelProperty(position = 34, value = "最后更新人标识")
    private String updatedBy = "";
    @ApiModelProperty(position = 35, value = "最后更新时间")
    private Date updatedAt = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public boolean deleted() {

        if (this.deleted == null) {
            return true;
        } else {
            return Constants.DELETED_YES == this.deleted.intValue();
        }

    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", deleted=" + deleted +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
