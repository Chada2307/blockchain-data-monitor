package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.logic.model.BlockMetrics;
import ekipa.einsteina.monitor.logic.model.TransactionMetrics;
import ekipa.einsteina.monitor.reporting.reportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private static final BigInteger BOOTSTRAP_WINDOW = BigInteger.valueOf(100);
    private static final int TRANSACTION_DETAIL_BLOCK_WINDOW = 10;

    private final Web3j web3j;
    private final reportingService reporter;

    private BigInteger lastBlockNumber = null;

    @Autowired
    public MonitorService(
            Web3j web3j,
            reportingService reporter
    ) {
        this.web3j = web3j;
        this.reporter = reporter;
    }

    @Scheduled(fixedRate = 10000)
    public void monitor() {
        try {
            BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();

            BigInteger fromBlock;
            if (lastBlockNumber == null) {
                fromBlock = currentBlock.subtract(BOOTSTRAP_WINDOW.subtract(BigInteger.ONE));
                if (fromBlock.compareTo(BigInteger.ZERO) < 0) {
                    fromBlock = BigInteger.ZERO;
                }
            } else {
                fromBlock = lastBlockNumber.add(BigInteger.ONE);
            }

            if (fromBlock.compareTo(currentBlock) > 0) {
                return;
            }

            List<BlockMetrics> metrics = processRange(fromBlock, currentBlock);
            reporter.reportBlockMetrics(metrics);

            List<TransactionMetrics> txMetrics = processTransactionsForLatestBlocks(
                    fromBlock,
                    currentBlock,
                    TRANSACTION_DETAIL_BLOCK_WINDOW
            );
            reporter.reportTransactions(txMetrics);
            System.out.println("Transaction metrics saved: " + txMetrics.size());

            lastBlockNumber = currentBlock;
        } catch (Exception e) {
            System.err.println("Monitor Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<BlockMetrics> processRange(BigInteger fromBlock, BigInteger toBlock) throws IOException {
        List<BlockMetrics> result = new ArrayList<>();

        for (BigInteger blockNumber = fromBlock;
             blockNumber.compareTo(toBlock) <= 0;
             blockNumber = blockNumber.add(BigInteger.ONE)) {

            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send();
            EthBlock.Block block = ethBlock.getBlock();
            if (block == null) {
                continue;
            }

            int txCount = block.getTransactions().size();

            result.add(new BlockMetrics(
                    blockNumber,
                    block.getHash(),
                    txCount
            ));
        }

        return result;
    }

    private List<TransactionMetrics> processTransactionsForLatestBlocks(
            BigInteger fromBlock,
            BigInteger toBlock,
            int blockWindow
    ) throws IOException {
        List<TransactionMetrics> txMetrics = new ArrayList<>();
        BigInteger windowSize = BigInteger.valueOf(Math.max(1, blockWindow));
        BigInteger txFrom = toBlock.subtract(windowSize.subtract(BigInteger.ONE));
        if (txFrom.compareTo(fromBlock) < 0) {
            txFrom = fromBlock;
        }

        for (BigInteger blockNumber = txFrom;
             blockNumber.compareTo(toBlock) <= 0;
             blockNumber = blockNumber.add(BigInteger.ONE)) {

            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send();
            EthBlock.Block block = ethBlock.getBlock();
            if (block == null) {
                continue;
            }

            for (EthBlock.TransactionResult<?> txResult : block.getTransactions()) {
                try {
                    Object txObj = txResult.get();
                    if (!(txObj instanceof EthBlock.TransactionObject tx)) {
                        continue;
                    }

                    EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(tx.getHash()).send();
                    BigInteger gasUsed = receiptResponse.getTransactionReceipt()
                            .map(r -> r.getGasUsed())
                            .orElse(BigInteger.ZERO);

                    BigDecimal valueEth = tx.getValue() == null
                            ? BigDecimal.ZERO
                            : Convert.fromWei(new BigDecimal(tx.getValue()), Convert.Unit.ETHER);

                    txMetrics.add(new TransactionMetrics(
                            blockNumber,
                            tx.getHash(),
                            tx.getFrom(),
                            tx.getTo(),
                            valueEth,
                            gasUsed
                    ));
                } catch (Exception ex) {
                    
                    System.err.println("Transaction metric skipped: " + ex.getMessage());
                }
            }
        }

        return txMetrics;
    }
}