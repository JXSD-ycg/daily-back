package com.ycg.daily.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import lombok.Data;

/**
 * 用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户id和图片byte 的md5值
     */
    private String hash;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片url
     */
    private String url;

    /**
     * 是否是头像 1:是  0:不是
     */
    private Integer isAvatar;

    /**
     * 图片创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Objects.equals(id, picture.id) && Objects.equals(userId, picture.userId) && Objects.equals(hash, picture.hash) && Objects.equals(name, picture.name) && Objects.equals(url, picture.url) && Objects.equals(isAvatar, picture.isAvatar) && Objects.equals(createTime, picture.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, hash, name, url, isAvatar, createTime);
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", userId=" + userId +
                ", hash='" + hash + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", isAvatar=" + isAvatar +
                ", createTime=" + createTime +
                '}';
    }
}