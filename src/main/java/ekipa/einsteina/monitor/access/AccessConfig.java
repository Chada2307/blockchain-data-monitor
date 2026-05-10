package ekipa.einsteina.monitor.access;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;

import java.io.IOException;

@Configuration
public class AccessConfig {

//    @Value("${blockchain.node.url_https}")
//    private String node_url_https;

    @Value("${blockchain.node.url_wss}")
    private String node_url_wss;

    @Bean
    public Web3j web3j()throws Exception {
        //return Web3j.build(new HttpService(node_url_https));
        WebSocketService wss = new WebSocketService(node_url_wss, true);
        Thread.sleep(3000);
        wss.connect();
        return Web3j.build(wss);
    }
}

