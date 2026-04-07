package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.reporting.reportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MonitorService {
    private final Web3j web3j;
    private final reportingService reporter;
    private BigInteger lastBlockNumber = BigInteger.ZERO;

    @Autowired
    public MonitorService(Web3j web3j, reportingService reporter){
        this.web3j = web3j;
        this.reporter = reporter;
    }

    @Scheduled(fixedRate = 10000)
    public void monitor(){
        try{
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();

            if(!currentBlock.equals(lastBlockNumber)){
                processBlock(currentBlock);
                lastBlockNumber = currentBlock;
            }

        }catch(IOException | InterruptedException e){
            System.err.println("Monitor Error: " + e.getMessage());
        }
    }

    private void processBlock(BigInteger currentBlock) throws InterruptedException, IOException{
        List<BigInteger> blockNumbers = Stream.iterate(currentBlock, x -> x.subtract(BigInteger.ONE))
                .limit(100)
                .toList();

        for(BigInteger blockNumber : blockNumbers){
            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send();
            System.out.println("Block Number: " + blockNumber);
            System.out.println("Block: " + ethBlock.getBlock().getHash());
            System.out.println("Trans: " + ethBlock.getBlock().getTransactions().size());
            reporter.reportToFile(blockNumber, ethBlock.getBlock().getHash(), ethBlock.getBlock().getTransactions().size());
        }
    }

}
