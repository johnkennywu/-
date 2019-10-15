package com.gf.intelligence.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gf.intelligence.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wushubiao
 * @Title: WebSocket
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/14
 */
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocket {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    /**
     * 会话
     */
    private Session session;
    /**
     * 用户名称
     */
    private String username;

    private ChatService chatService;

    private static ApplicationContext applicationContext;


    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

        /**
         * 建立连接
         *
         * @param session
         */
    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session)
    {
        onlineNumber++;
        logger.info("现在来连接的客户id："+session.getId()+"用户名："+username);
        this.username = username;
        this.session = session;
        logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
            //先给所有人发送通知，说我上线了
//            Map<String,Object> map1 = new HashMap<String,Object>();
//            map1.put("messageType",1);
//            map1.put("username",username);
//            sendMessageAll(JSON.toJSONString(map1),username);

            //把自己的信息加入到map当中去
            clients.put(username, this);
            //给自己发一条消息：告诉自己现在都有谁在线
//            Map<String,Object> map2 = new HashMap<String,Object>();
//            map2.put("messageType",3);
//            //移除掉自己
//            Set<String> set = clients.keySet();
//            map2.put("onlineUsers",set);
//            sendMessageTo(JSON.toJSONString(map2),username);
        }
        catch (Exception e){
            logger.info(username+"上线的时候通知所有人发生了错误");
        }



    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());
        //error.printStackTrace();
    }
    /**
     * 连接关闭
     */
    @OnClose
    public void onClose()
    {
        onlineNumber--;
        //webSockets.remove(this);
        clients.remove(username);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
//            Map<String,Object> map1 =new HashMap<String,Object>();
//            map1.put("messageType",2);
//            map1.put("onlineUsers",clients.keySet());
//            map1.put("username",username);
//            sendMessageAll(JSON.toJSONString(map1),username);
        }
        catch (Exception e){
            logger.info(username+"下线的时候通知所有人发生了错误");
        }
        logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message,@PathParam("username") String username, Session session)
    {
        try {
            logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());
            chatService = (ChatService) applicationContext.getBean("chatService");
            String rtnMsg = chatService.input(message);
            sendMessageTo(rtnMsg, username);
        }
        catch (Exception e){
            logger.info("发生错误");
        }

    }


    public void sendMessageTo(String message, String ToUserName) throws Exception {
        for (WebSocket item : clients.values()) {
            if (item.username.equals(ToUserName) ) {
                item.session.getAsyncRemote().sendText(message);
                break;
            }
        }
    }

    public void sendMessageAll(String message,String FromUserName) throws Exception {
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }
}
