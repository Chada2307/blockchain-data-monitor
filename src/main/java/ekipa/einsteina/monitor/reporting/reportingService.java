package ekipa.einsteina.monitor.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ekipa.einsteina.monitor.logic.model.BlockMetrics;
import ekipa.einsteina.monitor.logic.model.TransactionMetrics;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class reportingService {

    private static final String TXT_FILE = "./reporting.txt";
    private static final String BLOCKS_CSV_FILE = "./reporting_blocks.csv";
    private static final String TRANSACTIONS_CSV_FILE = "./reporting_transactions.csv";
    private static final String JSON_FILE = "./reporting.json";

    private static final String BLOCKS_CSV_HEADER = "Block Number,Block Hash,Transaction Count";
    private static final String TRANSACTIONS_CSV_HEADER = "Block Number,TX Hash,From,To,Value ETH,Gas Used";

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void reportBlockMetrics(List<BlockMetrics> metrics) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(TXT_FILE, true))) {
            for (BlockMetrics m : metrics) {
                out.printf("%s --- %s --- tx=%d%n", m.blockNumber(), m.blockHash(), m.transactionCount());
            }
        }

        boolean csvExists = new File(BLOCKS_CSV_FILE).exists();
        try (PrintWriter out = new PrintWriter(new FileOutputStream(BLOCKS_CSV_FILE, true))) {
            if (!csvExists) {
                out.println(BLOCKS_CSV_HEADER);
            }
            for (BlockMetrics m : metrics) {
                out.printf("%s,%s,%d%n", m.blockNumber(), m.blockHash(), m.transactionCount());
            }
        }

        appendToJson("blocks", metrics.stream().map(this::blockToNode).toList());
    }

    public void reportTransactions(List<TransactionMetrics> txs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(TXT_FILE, true))) {
            for (TransactionMetrics t : txs) {
                out.printf(
                        "TX --- block=%s --- hash=%s --- from=%s --- to=%s --- valueEth=%s --- gasUsed=%s%n",
                        t.blockNumber(), t.txHash(), t.from(), t.to(), t.valueEth(), t.gasUsed()
                );
            }
        }

        boolean csvExists = new File(TRANSACTIONS_CSV_FILE).exists();
        try (PrintWriter out = new PrintWriter(new FileOutputStream(TRANSACTIONS_CSV_FILE, true))) {
            if (!csvExists) {
                out.println(TRANSACTIONS_CSV_HEADER);
            }
            for (TransactionMetrics t : txs) {
                out.printf("%s,%s,%s,%s,%s,%s%n",
                        t.blockNumber(), t.txHash(), t.from(),
                        t.to() != null ? t.to() : "",
                        t.valueEth(), t.gasUsed());
            }
        }

        appendToJson("transactions", txs.stream().map(this::txToNode).toList());
    }

    private synchronized void appendToJson(String key, List<ObjectNode> newNodes) throws IOException {
        File jsonFile = new File(JSON_FILE);
        ObjectNode root;
        if (jsonFile.exists()) {
            root = (ObjectNode) objectMapper.readTree(jsonFile);
        } else {
            root = objectMapper.createObjectNode();
            root.putArray("blocks");
            root.putArray("transactions");
        }
        ArrayNode array = (ArrayNode) root.get(key);
        newNodes.forEach(array::add);
        objectMapper.writeValue(jsonFile, root);
    }

    private ObjectNode blockToNode(BlockMetrics m) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("blockNumber", m.blockNumber().toString());
        node.put("blockHash", m.blockHash());
        node.put("transactionCount", m.transactionCount());
        return node;
    }

    private ObjectNode txToNode(TransactionMetrics t) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("blockNumber", t.blockNumber().toString());
        node.put("txHash", t.txHash());
        node.put("from", t.from());
        node.put("to", t.to());
        node.put("valueEth", t.valueEth().toString());
        node.put("gasUsed", t.gasUsed().toString());
        return node;
    }
}
