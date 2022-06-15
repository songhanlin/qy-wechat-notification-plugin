package org.jenkinsci.plugins.qywechat.dto.wechat;

public class MessageInfo {

    private String msgtype = "template_card";

    private TemplateCardInfo template_card;

    public String getMsgtype() {
        return msgtype;
    }

    public TemplateCardInfo getTemplate_card() {
        return template_card;
    }

    public void setTemplate_card(TemplateCardInfo template_card) {
        this.template_card = template_card;
    }
}
