package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.reporting.reportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MonitorService {
    private final Web3j web3j;
    private final reportingService reporter;
    private int totalTrans = 0;
    private int totalBlocks = 0;

    @Autowired
    public MonitorService(Web3j web3j, reportingService reporter){
        this.web3j = web3j;
        this.reporter = reporter;
        initHistoricalBlocks();
        startMonitorWSS();
    }

    private void startMonitorWSS(){
        web3j.blockFlowable(false).subscribe(ethBlock -> {
            BigInteger number = ethBlock.getBlock().getNumber();
            processSingleBlock(number, false);
        }, error -> {
            System.out.println("error: " + error.getMessage());
        });
    }

    public void processSingleBlock(BigInteger number, boolean isdetailed) throws IOException{
        EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(number), true).send();

        if(block.getBlock() != null){
            totalBlocks++;
            int txCount = block.getBlock().getTransactions().size();
            totalTrans += txCount;
            reporter.reportBlock(number, block.getBlock().getHash(), txCount);
            if (isdetailed){
                processTrans(block.getBlock().getTransactions());
            }
        }
//
//        int txCount = block.getBlock().getTransactions().size();
//        System.out.println("-----------------------------");
//        System.out.println("txCount: " + txCount);
//        System.out.println("block: " + block.getBlock().getHash());
//        System.out.println("-----------------------------");
//        totalBlocks++;
    }


   public void initHistoricalBlocks(){
        new Thread(() -> {
            try{
                BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();

                for (int i = 0; i < 100; i++){
                    BigInteger blockNumber = currentBlock.subtract(BigInteger.valueOf(i));
                    processSingleBlock(blockNumber, i < 10);
                    Thread.sleep(300);
                }

            }catch(Exception e){
                System.err.println("Monitor Error: " + e.getMessage());
            }
        }).start();
    }

    private void processTrans(List<EthBlock.TransactionResult> transactions){
        transactions.forEach(txResult -> {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult.get();


            BigInteger actualGasUser = BigInteger.ZERO;
            try{
                var receipt = web3j.ethGetTransactionReceipt(tx.getHash()).send();
                actualGasUser = receipt.getTransactionReceipt().map(r -> r.getGasUsed()).orElse(BigInteger.ZERO);
            }catch (IOException e){
                e.printStackTrace();
            }

//            System.out.println("-----------------------------");
//            System.out.println("Hash: " + tx.getHash());
//            System.out.println("From: " + tx.getFrom());
//            System.out.println("To: " + tx.getTo());
//            System.out.println("Value: " + tx.getValue());
//            System.out.println("Gas: " + actualGasUser);
//            System.out.println("-----------------------------");

            reporter.reportTrans(
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    tx.getValue(),
                    actualGasUser
            );
        });
    }


}
