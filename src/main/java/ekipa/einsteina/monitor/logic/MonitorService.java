package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.Models.BlockEntity;
import ekipa.einsteina.monitor.Models.TransEntity;
import ekipa.einsteina.monitor.interfaces.BlockRepository;
import ekipa.einsteina.monitor.interfaces.TransRepository;
import ekipa.einsteina.monitor.reporting.reportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Service
public class MonitorService {
    private final Web3j web3j;
    private final reportingService reporter;
    private int totalTrans = 0;
    private int totalBlocks = 0;

    private final BlockRepository blockRepository;
    private final TransRepository transRepository;

    public void saveBlockData(BlockEntity blockEntity) {
        blockRepository.save(blockEntity);
    }

    @Autowired
    public MonitorService(Web3j web3j, reportingService reporter , BlockRepository blockRepository, TransRepository transRepository) {
        this.web3j = web3j;
        this.reporter = reporter;
        initHistoricalBlocks();
        startMonitorWSS();
        this.blockRepository = blockRepository;
        this.transRepository = transRepository;
    }

    private void startMonitorWSS(){
        web3j.blockFlowable(false).subscribe(ethBlock -> {
            BigInteger number = ethBlock.getBlock().getNumber();
            processSingleBlock(number, true);
        }, error -> {
            System.out.println("error: " + error.getMessage());
        });
    }

    public void processSingleBlock(BigInteger number, boolean isdetailed) throws IOException{
        EthBlock block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(number), true).send();

        if(block.getBlock() != null){

            BlockEntity blockEntity = new BlockEntity();
            blockEntity.setBlockHash(block.getBlock().getHash());
            blockEntity.setBlockNumber(block.getBlock().getNumber().longValue());
            blockEntity.setTxCount(block.getBlock().getTransactions().size());

            totalBlocks++;
            int txCount = block.getBlock().getTransactions().size();
            totalTrans += txCount;
            reporter.reportBlock(number, block.getBlock().getHash(), txCount);
            if (isdetailed){
                processTrans(block.getBlock().getTransactions(), blockEntity);
            }
            blockRepository.save(blockEntity);
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

    private void processTrans(List<EthBlock.TransactionResult> transactions, BlockEntity blockEntity){
        transactions.forEach(txResult -> {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult.get();



            BigInteger actualGasUser = BigInteger.ZERO;
            try{
                var receipt = web3j.ethGetTransactionReceipt(tx.getHash()).send();
                actualGasUser = receipt.getTransactionReceipt().map(r -> r.getGasUsed()).orElse(BigInteger.ZERO);
            }catch (IOException e){
                e.printStackTrace();
            }

            TransEntity trans = new TransEntity();
            trans.setTxHash(tx.getHash());
            trans.setFromAddress(tx.getFrom());
            trans.setToAdrress(tx.getTo());
            trans.setValueWei(tx.getValue());
            trans.setBlock(blockEntity);
            trans.setGasUsed(actualGasUser);
            transRepository.save(trans);
//            -------------------------------- No row with the given identifier exists for entity
//


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
