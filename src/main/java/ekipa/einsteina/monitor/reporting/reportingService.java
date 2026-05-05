package ekipa.einsteina.monitor.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ekipa.einsteina.monitor.logic.model.BlockMetrics;
import ekipa.einsteina.monitor.logic.model.TransactionMetrics;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class reportingService {

    private static final Logger log = LoggerFactory.getLogger(reportingService.class);

    private static final String TXT_FILE = "./reporting.txt";
    private static final String BLOCKS_CSV_FILE = "./reporting_blocks.csv";
    private static final String TRANSACTIONS_CSV_FILE = "./reporting_transactions.csv";
    private static final String JSON_FILE = "./reporting.json";
    private static final String SUMMARY_FILE = "./summary_report.txt";

    private static final String BLOCKS_CSV_HEADER = "Block Number,Block Hash,Transaction Count";
    private static final String TRANSACTIONS_CSV_HEADER = "Block Number,TX Hash,From,To,Value ETH,Gas Used";

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final AtomicInteger totalBlocksProcessed = new AtomicInteger(0);
    private final AtomicInteger totalTransactionsProcessed = new AtomicInteger(0);

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

        totalBlocksProcessed.addAndGet(metrics.size());

        for (BlockMetrics m : metrics) {
            log.info("Przetworzono blok: {} | Hash: {} | TX: {}",
                    m.blockNumber(), m.blockHash(), m.transactionCount());
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

        totalTransactionsProcessed.addAndGet(txs.size());

        for (TransactionMetrics t : txs) {
            log.info("Transakcja: {} | Blok: {} | Od: {} | Do: {} | Wartość: {} ETH | Gas: {}",
                    t.txHash(), t.blockNumber(), t.from(),
                    t.to() != null ? t.to() : "kontrakt",
                    t.valueEth(), t.gasUsed());
        }

        appendToJson("transactions", txs.stream().map(this::txToNode).toList());
    }

    @PreDestroy
    public void generateSummaryReport() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String summary = String.format(
                "===== RAPORT PODSUMOWUJĄCY =====%n" +
                "Data wygenerowania : %s%n" +
                "--------------------------------%n" +
                "Przetworzone bloki : %d%n" +
                "Przetworzone TX    : %d%n" +
                "================================%n",
                timestamp,
                totalBlocksProcessed.get(),
                totalTransactionsProcessed.get()
        );

        try (PrintWriter out = new PrintWriter(new FileOutputStream(SUMMARY_FILE, false))) {
            out.print(summary);
        } catch (IOException e) {
            log.error("Nie udało się zapisać raportu podsumowującego: {}", e.getMessage());
        }

        log.info("Raport podsumowujący zapisany do {}", SUMMARY_FILE);
        log.info(summary);
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
