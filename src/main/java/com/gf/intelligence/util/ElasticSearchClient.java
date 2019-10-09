package com.gf.intelligence.util;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author wushubiao
 * @Title: ElasticSearchClient
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/9
 */
@Component
public class ElasticSearchClient implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class);

    public TransportClient client;

    @Value("${elasticsearch.cluster.name}")
    private String clusterName;

    @Value("{elasticsearch.cluster.nodes}")
    private String clusterNodes;

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            Settings settings = Settings.builder().put("cluster.name",clusterName).put("client.transport.sniff",true).build();
            client = new PreBuiltTransportClient(settings);
            if(StringUtils.isNotBlank(clusterNodes)){
                String[] clusterNode = StringUtils.split(clusterNodes, ",");
                for(String node:clusterNode){
                    String[] split = StringUtils.split(node, ":");
                    client.addTransportAddress( new TransportAddress(InetAddress.getByName(split[0]), Integer.valueOf(split[1])));
                }
            }
        }catch (Exception e){
            logger.error("创建ES.client出错{}",e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        client.close();
    }
}
