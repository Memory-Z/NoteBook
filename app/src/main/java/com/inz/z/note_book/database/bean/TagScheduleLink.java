package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

/**
 * 标签 - 任务 计划  链接
 * <p>
 * ====================================================
 * Create by 11654 in 2021/10/24 14:10
 */
@Entity(nameInDb = "tag_schedule_link")
public class TagScheduleLink {
    /**
     * LinkId
     */
    @Id(autoincrement = true)
    private Long linkId = 0L;
    /**
     * 标签 ID
     *
     * @see TagInfo
     */
    @Index
    private Long tagId = 0L;

    /**
     * 任务计划 ID
     *
     * @see TaskSchedule
     */
    @Index
    private String scheduleId = "";

    @Property(nameInDb = "link_index")
    private int index = 0;

    private Date createDate;

    @Generated(hash = 1315251299)
    public TagScheduleLink(Long linkId, Long tagId, String scheduleId, int index,
            Date createDate) {
        this.linkId = linkId;
        this.tagId = tagId;
        this.scheduleId = scheduleId;
        this.index = index;
        this.createDate = createDate;
    }

    @Generated(hash = 175255853)
    public TagScheduleLink() {
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "TagScheduleLink{" +
                "linkId=" + linkId +
                ", tagId=" + tagId +
                ", scheduleId='" + scheduleId + '\'' +
                ", index=" + index +
                ", createDate=" + createDate +
                '}';
    }
}
