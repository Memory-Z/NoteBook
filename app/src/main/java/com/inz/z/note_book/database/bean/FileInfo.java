package com.inz.z.note_book.database.bean;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/22 14:35.
 */
@Entity(nameInDb = "file_info")
public class FileInfo {

    @Index
    @Id(autoincrement = true)
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName = "";

    /**
     * 文件URI 地址
     */
    private String uri = "";

    /**
     * 文件路径
     */
    private String filePath = "";
    /**
     * 文件原路径
     */
    private String oldFilePath = "";
    /**
     * 文件类型
     */
    private String fileType = "";
    /**
     * 图片字符串
     */
    private String imageStr = "";

    private Date createDate;
    private Date updateDate;

    @Generated(hash = 1038290468)
    public FileInfo(Long fileId, String fileName, String uri, String filePath,
            String oldFilePath, String fileType, String imageStr, Date createDate,
            Date updateDate) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.uri = uri;
        this.filePath = filePath;
        this.oldFilePath = oldFilePath;
        this.fileType = fileType;
        this.imageStr = imageStr;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Generated(hash = 1367591352)
    public FileInfo() {
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOldFilePath() {
        return this.oldFilePath;
    }

    public void setOldFilePath(String oldFilePath) {
        this.oldFilePath = oldFilePath;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageStr() {
        return this.imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public String toString() {
        return "FileInfo{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", uri='" + uri + '\'' +
                ", filePath='" + filePath + '\'' +
                ", oldFilePath='" + oldFilePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", imageStr='" + imageStr + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
