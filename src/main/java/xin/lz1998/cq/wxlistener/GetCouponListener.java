package xin.lz1998.cq.wxlistener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.xuxiaoxiao.chatapi.wechat.WeChatClient;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXContact;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLocation;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXVerify;
import xin.lz1998.cq.plugin.forward.ForwardPlugin;
import xin.lz1998.cq.plugin.forward.HttpUtil;
import xin.lz1998.cq.plugin.forward.enums.DsErrorEnum;

@Component
public class GetCouponListener implements CommandLineRunner {

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	
	private static Logger logger = LoggerFactory.getLogger(ForwardPlugin.class);
	
	/**
     * 新建一个微信监听器
     */
    public static final WeChatClient.WeChatListener LISTENER = new WeChatClient.WeChatListener() {
        @Override
        public void onQRCode(@Nonnull WeChatClient client, @Nonnull String qrCode) {
        	logger.info("微信二维码登录链接:" + qrCode);
        }
        
        @Override
        public void onLogin(@Nonnull WeChatClient client) {
        	logger.info(String.format("登录成功：您有%d名好友、活跃微信群%d个", client.userFriends().size(), client.userGroups().size()));
        }
        
        @Override
        public void onMessage(@Nonnull WeChatClient client, @Nonnull WXMessage message) {
        	logger.info("收到新消息：" + GSON.toJson(message));
            
            if (message instanceof WXVerify) {
                //是好友请求消息，自动同意好友申请
                client.passVerify((WXVerify) message);
            } else if (message instanceof WXLocation && message.fromUser != null && !message.fromUser.id.equals(client.userMe().id)) {
                // 如果对方告诉我他的位置，发送消息的不是自己，则我也告诉他我的位置
                if (message.fromGroup != null) {
                    // 群消息
                    // client.sendLocation(message.fromGroup, "120.14556", "30.23856", "我在这里", "西湖");
                } else {
                    // 用户消息
                    client.sendLocation(message.fromUser, "120.14556", "30.23856", "我在这里", "西湖");
                }
            } else if (message instanceof WXText && message.fromUser != null && !message.fromUser.id.equals(client.userMe().id)) {
                //是文字消息，并且发送消息的人不是自己，发送相同内容的消息
                if (message.fromGroup != null) {
                    // 群消息
                    // client.sendText(message.fromGroup, message.content);
                } else {
                    // 用户消息
                    client.sendText(message.fromUser, getTaoCouponInfo(message.content));
                }
            }
        }
        
        @Override
        public void onContact(@Nonnull WeChatClient client, @Nullable WXContact oldContact, @Nullable WXContact newContact) {
        	logger.info(String.format("检测到联系人变更:旧联系人名称：%s:新联系人名称：%s", (oldContact == null ? "null" : oldContact.name), (newContact == null ? "null" : newContact.name)));
        }
        
        private String getTaoCouponInfo(String content) {
        	String url = "http://api.web.21ds.cn/taoke/doItemHighCommissionPromotionLinkByAll?apkey=%s&tbname=%s&pid=%s&content=%s&tpwd=1&extsearch=1";
        	String apKey = "7918202b-ef4a-f251-291b-eb880302814c";
        	String pid = "mm_109302870_1080150328_109752250051";
        	String tbname = "tb6746204";
        	JSONObject jsonResult;
    		try {
    			jsonResult = HttpUtil.sendGet(String.format(url, apKey,tbname,pid,content));
    			logger.info("喵有券接口转换结果：" + jsonResult.toJSONString());
    	    	if(jsonResult.getInteger("code") == 200) {
    	    		StringBuffer result = new StringBuffer("-------优惠券信息-------\n");
    	    		result.append("预计优惠金额:" + jsonResult.getJSONObject("data").getString("youhuiquan") + "\n");
    	    		result.append("复制淘口令后打开淘宝:" + jsonResult.getJSONObject("data").getString("tpwd") + "\n");
    	    		result.append("或点击此链接购买:" + jsonResult.getJSONObject("data").getString("item_url") + "\n");
    	    		return result.toString();
    	    	}
    	    	logger.error("获取喵有券高佣转淘口令api接口错误，请立即排查处理，否则影响引导收入，失败原因:[{}]",DsErrorEnum.getByCode(jsonResult.getString("code")));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	return "查询失败";
        }
    };
    
	@Override
	public void run(String... args) throws Exception {
		//新建一个模拟微信客户端
        WeChatClient wechatClient = new WeChatClient();
        //为模拟微信客户端设置监听器
        wechatClient.setListener(LISTENER);
        //启动模拟微信客户端
        wechatClient.startup();
	}
    
}
