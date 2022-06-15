package org.jenkinsci.plugins.qywechat.dto.wechat;

public class SourceInfo {

    private String desc;

    //来源文字的颜色，目前支持：0(默认) 灰色，1 黑色，2 红色，3 绿色
    private String desc_color;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc_color() {
        return desc_color;
    }

    public void setDesc_color(String desc_color) {
        this.desc_color = desc_color;
    }
}
