package ekipa.einsteina.monitor.access;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class AccessConfig {

    @Value("${blockchain.node.url_https}")
    private String node_url_https;

    @Bean
    public Web3j web3j(){
        return Web3j.build(new HttpService(node_url_https));
    }
}
