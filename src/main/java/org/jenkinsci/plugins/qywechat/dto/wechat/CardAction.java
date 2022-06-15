package org.jenkinsci.plugins.qywechat.dto.wechat;

public class CardAction {

    private Integer type = 1;

    private String url;

    public Integer getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
