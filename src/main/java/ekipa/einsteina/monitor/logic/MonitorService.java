package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.logic.model.BlockMetrics;
import ekipa.einsteina.monitor.logic.model.TransactionMetrics;
import ekipa.einsteina.monitor.reporting.reportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
            System.err.println("WSS error: " + error.getMessage());
        });
    }

    public void processSingleBlock(BigInteger number, boolean isdetailed) throws IOException{
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(number), true).send();
        EthBlock.Block block = ethBlock.getBlock();

        if(block != null){
            totalBlocks++;
            int txCount = block.getTransactions().size();
            totalTrans += txCount;

            BlockMetrics metrics = new BlockMetrics(number, block.getHash(), txCount);
            reporter.reportBlockMetrics(List.of(metrics));

            if (isdetailed){
                processTrans(block.getTransactions(), number);
            }
        }
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

    private void processTrans(List<EthBlock.TransactionResult> transactions, BigInteger blockNumber) throws IOException {
        List<TransactionMetrics> txMetricsList = new ArrayList<>();

        for (EthBlock.TransactionResult<?> txResult : transactions) {
            Object txObj = txResult.get();
            if (!(txObj instanceof EthBlock.TransactionObject tx)) {
                continue;
            }

            BigInteger actualGasUser = BigInteger.ZERO;
            try {
                EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(tx.getHash()).send();
                actualGasUser = receiptResponse.getTransactionReceipt()
                        .map(r -> r.getGasUsed())
                        .orElse(BigInteger.ZERO);
            } catch (IOException e) {
                System.err.println("Receipt Error: " + e.getMessage());
            }

            BigDecimal valueEth = tx.getValue() == null
                    ? BigDecimal.ZERO
                    : Convert.fromWei(new BigDecimal(tx.getValue()), Convert.Unit.ETHER);

            txMetricsList.add(new TransactionMetrics(
                    blockNumber,
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    valueEth,
                    actualGasUser
            ));
        }

        if (!txMetricsList.isEmpty()) {
            reporter.reportTransactions(txMetricsList);
        }
    }
}