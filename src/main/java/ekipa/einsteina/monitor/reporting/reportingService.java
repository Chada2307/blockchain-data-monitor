package ekipa.einsteina.monitor.reporting;

import ekipa.einsteina.monitor.logic.model.BlockMetrics;
import ekipa.einsteina.monitor.logic.model.TransactionMetrics;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class reportingService {

    public void reportBlockMetrics(List<BlockMetrics> metrics) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("./reporting.txt", true))) {
            for (BlockMetrics m : metrics) {
                out.printf(
                        "%s --- %s --- tx=%d%n",
                        m.blockNumber(),
                        m.blockHash(),
                        m.transactionCount()
                );
            }
        }
    }

    public void reportTransactions(List<TransactionMetrics> txs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileOutputStream("./reporting.txt", true))) {
            for (TransactionMetrics t : txs) {
                out.printf(
                        "TX --- block=%s --- hash=%s --- from=%s --- to=%s --- valueEth=%s --- gasUsed=%s%n",
                        t.blockNumber(),
                        t.txHash(),
                        t.from(),
                        t.to(),
                        t.valueEth(),
                        t.gasUsed()
                );
            }
        }
    }
}