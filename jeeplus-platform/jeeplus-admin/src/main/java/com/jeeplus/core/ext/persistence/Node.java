package com.jeeplus.core.ext.persistence;

import java.io.Serializable;

public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String sortCode;

    private String text;

    private String parent;

    private String grade;

    private State state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public State getState() {
        if ("#".equals(parent) && state == null) {
            State e = new State();
            e.setOpen(true);
            setState(e);
        }
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static class State implements Serializable {

        private static final long serialVersionUID = 1L;

        private boolean open;

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }
    }
}
