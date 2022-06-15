package org.jenkinsci.plugins.qywechat.dto;

import hudson.EnvVars;
import org.jenkinsci.plugins.qywechat.NotificationUtil;
import org.jenkinsci.plugins.qywechat.dto.wechat.*;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 开始构建的通知信息
 * @author jiaju
 */
public class BuildBeginInfo {

    private List<String> hiddenParams = Arrays.asList("jobId", "buildType", "projectId", "buildScope");

    /**
     * 请求参数
     */
    private Map params = new HashMap<String, Object>();

    /**
     * 预计时间，毫秒
     */
    private Long durationTime = 0L;

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
     * 更多自定消息
     */
    private String moreInfo = "";

    public BuildBeginInfo(String projectName, AbstractBuild<?, ?> build, NotificationConfig config, EnvVars envVars){
        //获取请求参数
        List<ParametersAction> parameterList = build.getActions(ParametersAction.class);
        if(parameterList!=null && parameterList.size()>0){
            for(ParametersAction p : parameterList){
                for(ParameterValue pv : p.getParameters()){
                    this.params.put(pv.getName(), pv.getValue());
                }
            }
        }
        //预计时间
        if(build.getProject().getEstimatedDuration()>0){
            this.durationTime = build.getProject().getEstimatedDuration();
        }
        //控制台地址
        StringBuilder urlBuilder = new StringBuilder();
        String jenkinsUrl = NotificationUtil.getJenkinsUrl();
        if(StringUtils.isNotEmpty(jenkinsUrl)){
            String buildUrl = build.getUrl();
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
        if (StringUtils.isNotEmpty(config.moreInfo)){
            moreInfo = config.moreInfo;
        }
    }

    public String toJSONString(){
        //参数组装
        StringBuffer paramBuffer = new StringBuffer();
        params.forEach((key, val)->{
            if(!hiddenParams.contains(key)) {
                paramBuffer.append(key);
                paramBuffer.append("=");
                paramBuffer.append(val);
                paramBuffer.append(", ");
            }
        });
        if(paramBuffer.length()==0){
            paramBuffer.append("无");
        }else{
            paramBuffer.deleteCharAt(paramBuffer.length()-2);
        }

        //耗时预计
        String durationTimeStr = "无";
        if(durationTime>0){
            Long l = durationTime / (1000 * 60);
            durationTimeStr = l + "分钟";
        }

        //组装内容
        MessageInfo messageInfo = new MessageInfo();
        TemplateCardInfo templateCardInfo = new TemplateCardInfo();
        // 设置标题
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setDesc("开始构建");
        sourceInfo.setDesc_color("3");
        templateCardInfo.setSource(sourceInfo);
        // 设置名称
        MainTitle mainTitle = new MainTitle();
        mainTitle.setTitle("【" + this.projectName + "】");
        templateCardInfo.setMain_title(mainTitle);
        // 设置二维码
        CardImage cardImage = new CardImage();
        cardImage.setUrl("https://appdownload.kstore.shop/" + envVars.get("projectId") + "/qrcode.png");
        templateCardInfo.setCard_image(cardImage);
        // 设置点击跳转
        CardAction cardAction = new CardAction();
        cardAction.setUrl("http://app.dev.wanmi.com/"+this.projectName+"/"+
                (envVars.get("buildType").equals("微信小程序")?"1":"0"));
        templateCardInfo.setCard_action(cardAction);

        // 设置构建明细
        List<VerticalContentInfo> verticalContentInfos = new ArrayList<>();
        if(Objects.nonNull(envVars) && StringUtils.isNotEmpty(envVars.get("buildType"))) {
            VerticalContentInfo buildType = new VerticalContentInfo();
            buildType.setTitle("构建类型");
            buildType.setDesc(envVars.get("buildType"));
            verticalContentInfos.add(buildType);
        }
        VerticalContentInfo buildParams = new VerticalContentInfo();
        buildParams.setTitle("构建参数");
        buildParams.setDesc(paramBuffer.toString());
        verticalContentInfos.add(buildParams);

        VerticalContentInfo buildTime = new VerticalContentInfo();
        buildTime.setTitle("预计用时");
        buildTime.setDesc(durationTimeStr);
        verticalContentInfos.add(buildTime);

        templateCardInfo.setVertical_content_list(verticalContentInfos);
        messageInfo.setTemplate_card(templateCardInfo);

        String req = JSONObject.fromObject(messageInfo).toString();
        return req;
    }



}
