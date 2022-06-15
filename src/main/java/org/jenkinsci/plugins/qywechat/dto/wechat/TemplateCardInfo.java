package org.jenkinsci.plugins.qywechat.dto.wechat;

import java.util.List;

public class TemplateCardInfo {

    private String card_type = "news_notice";

    private SourceInfo source;

    private MainTitle main_title;

    private CardImage card_image;

    private CardAction card_action;

    private List<VerticalContentInfo> vertical_content_list;

    public String getCard_type() {
        return card_type;
    }

    public SourceInfo getSource() {
        return source;
    }

    public void setSource(SourceInfo source) {
        this.source = source;
    }

    public MainTitle getMain_title() {
        return main_title;
    }

    public void setMain_title(MainTitle main_title) {
        this.main_title = main_title;
    }

    public CardImage getCard_image() {
        return card_image;
    }

    public void setCard_image(CardImage card_image) {
        this.card_image = card_image;
    }

    public CardAction getCard_action() {
        return card_action;
    }

    public void setCard_action(CardAction card_action) {
        this.card_action = card_action;
    }

    public List<VerticalContentInfo> getVertical_content_list() {
        return vertical_content_list;
    }

    public void setVertical_content_list(List<VerticalContentInfo> vertical_content_list) {
        this.vertical_content_list = vertical_content_list;
    }
}
