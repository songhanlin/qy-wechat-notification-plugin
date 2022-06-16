package org.jenkinsci.plugins.qywechat.dto;

import hudson.EnvVars;
import org.jenkinsci.plugins.qywechat.NotificationUtil;
import org.jenkinsci.plugins.qywechat.dto.wechat.*;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import hudson.model.Result;
import hudson.model.Run;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 结束构建的通知信息
 * @author jiaju
 */
public class BuildOverInfo {

    /**
     * 使用时间，毫秒
     */
    private String useTimeString = "";

    /**
     * 本次构建控制台地址
     */
    private String consoleUrl;

    /**
     * 工程名称
     */
    private String projectName;

    /**
     * 环境变量
     */
    private EnvVars envVars;

    /**
     * 环境名称
     */
    private String topicName = "";

    /**
     * 执行结果
     */
    private Result result;

    public BuildOverInfo(String projectName, Run<?, ?> run, NotificationConfig config, EnvVars envVars){
        //使用时间
        this.useTimeString = run.getTimestampString();
        //控制台地址
        StringBuilder urlBuilder = new StringBuilder();
        String jenkinsUrl = NotificationUtil.getJenkinsUrl();
        if(StringUtils.isNotEmpty(jenkinsUrl)){
            String buildUrl = run.getUrl();
            urlBuilder.append(jenkinsUrl);
            if(!jenkinsUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append(buildUrl);
            if(!buildUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append("console");
        }
        this.consoleUrl = urlBuilder.toString();
        //工程名称
        this.projectName = projectName;
        // 环境变量
        this.envVars = envVars;
        //环境名称
        if(config.topicName!=null){
            topicName = config.topicName;
        }
        //结果
        result = run.getResult();
    }

    public String toJSONString(){
        //组装内容
        MessageInfo messageInfo = new MessageInfo();
        TemplateCardInfo templateCardInfo = new TemplateCardInfo();

        // 设置标题
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setDesc("构建"+getStatus());
        sourceInfo.setDesc_color(getStatusColor());
        templateCardInfo.setSource(sourceInfo);
        // 设置名称
        MainTitle mainTitle = new MainTitle();
        mainTitle.setTitle("【" + this.projectName + "】");
        templateCardInfo.setMain_title(mainTitle);
        boolean wechatFlag = envVars.get("buildType").equals("微信小程序");
        // 设置二维码
        CardImage cardImage = new CardImage();
        cardImage.setUrl("https://appdownload.kstore.shop/" + envVars.get("projectId") +
                (wechatFlag?"/wechatCode.png":"/appCode.png"));
        templateCardInfo.setCard_image(cardImage);
        // 设置点击跳转
        CardAction cardAction = new CardAction();
        cardAction.setUrl("http://app.dev.wanmi.com/"+this.projectName+"/"+ (wechatFlag?"1":"0"));
        templateCardInfo.setCard_action(cardAction);

        // 设置构建明细
        List<VerticalContentInfo> verticalContentInfos = new ArrayList<>();
        if(Objects.nonNull(envVars) && StringUtils.isNotEmpty(envVars.get("buildType"))) {
            VerticalContentInfo buildType = new VerticalContentInfo();
            buildType.setTitle("构建类型");
            buildType.setDesc(envVars.get("buildType"));
            verticalContentInfos.add(buildType);
        }
        VerticalContentInfo buildTime = new VerticalContentInfo();
        buildTime.setTitle("构建用时");
        buildTime.setDesc(useTimeString);
        verticalContentInfos.add(buildTime);

        templateCardInfo.setVertical_content_list(verticalContentInfos);
        messageInfo.setTemplate_card(templateCardInfo);

        String req = JSONObject.fromObject(messageInfo).toString();
        return req;
    }

    private String getStatus(){
        if(null != result && result.equals(Result.FAILURE)){
            return "失败!!!\uD83D\uDE2D";
        }else if(null != result && result.equals(Result.ABORTED)){
            return "中断!!\uD83D\uDE28";
        }else if(null != result && result.equals(Result.UNSTABLE)){
            return "异常!!\uD83D\uDE41";
        }else if(null != result && result.equals(Result.SUCCESS)){
            int max=successFaces.length-1,min=0;
            int ran = (int) (Math.random()*(max-min)+min);
            return "成功~" + successFaces[ran];
        }
        return "情况未知";
    }

    private String getStatusColor(){
        if(null != result && result.equals(Result.FAILURE)){
            return "2";
        }else if(null != result && result.equals(Result.ABORTED)){
            return "0";
        }else if(null != result && result.equals(Result.UNSTABLE)){
            return "2";
        }else if(null != result && result.equals(Result.SUCCESS)){
            return "3";
        }
        return "1";
    }

    String []successFaces = {
            "\uD83D\uDE0A", "\uD83D\uDE04", "\uD83D\uDE0E", "\uD83D\uDC4C", "\uD83D\uDC4D", "(o´ω`o)و", "(๑•̀ㅂ•́)و✧"
    };


}
