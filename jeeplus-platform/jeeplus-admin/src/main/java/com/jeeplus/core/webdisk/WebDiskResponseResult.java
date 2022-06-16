package com.jeeplus.core.webdisk;

public class WebDiskResponseResult<T> {
    /** 文件编号 */
    private String userFileId;
    /** 文件名称 */
    private String fileName;
    /** 文件后最 */
    private String extendName;

    public String getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(String userFileId) {
        this.userFileId = userFileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }
}
