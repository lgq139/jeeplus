package com.jeeplus.modules.sys.entity;

import java.util.Date;

import com.jeeplus.core.ext.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 日历Entity
 */
public class Calendar extends DataEntity<Calendar> {

    private static final long serialVersionUID = 1L;
    private String title;        // 事件标题
    private Date start;        // 事件开始时间
    private Date end;        // 事件结束时间
    private String adllDay;        // 是否为全天时间
    private String color;        // 时间的背景色
    private User user;        // 所属用户

    public Calendar() {
        super();
    }

    public Calendar(String id) {
        super(id);
    }

    @Length(min = 0, max = 64, message = "事件标题长度必须介于 0 和 64 之间")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Length(min = 0, max = 64, message = "事件开始时间长度必须介于 0 和 64 之间")
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Length(min = 0, max = 64, message = "事件结束时间长度必须介于 0 和 64 之间")
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @JsonIgnore
    @Length(min = 0, max = 64, message = "是否为全天时间长度必须介于 0 和 64 之间")
    public String getAdllDay() {
        return adllDay;
    }

    public void setAdllDay(String adllDay) {
        this.adllDay = adllDay;
    }

    @Length(min = 0, max = 64, message = "时间的背景色长度必须介于 0 和 64 之间")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
