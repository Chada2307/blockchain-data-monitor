package ekipa.einsteina.monitor.reporting;

import ekipa.einsteina.monitor.logic.dto.BlockMetrics;
import ekipa.einsteina.monitor.logic.dto.TransactionMetrics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportingServiceTest {

    private final reportingService svc = new reportingService();
    private final Path txt = Path.of("reporting.txt");
    private final Path blocksCsv = Path.of("reporting_blocks.csv");
    private final Path json = Path.of("reporting.json");
    private final Path summary = Path.of("summary_report.txt");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(txt);
        Files.deleteIfExists(blocksCsv);
        Files.deleteIfExists(json);
        Files.deleteIfExists(summary);
    }

    @Test
    void reportBlockMetrics_createsFilesAndJson() throws IOException {
        var metrics = List.of(new BlockMetrics(BigInteger.valueOf(1), "h1", 0));

        svc.reportBlockMetrics(metrics);

        assertTrue(Files.exists(txt));
        assertTrue(Files.exists(blocksCsv));
        assertTrue(Files.exists(json));

        String content = Files.readString(blocksCsv);
        assertTrue(content.contains("Block Number"));
        assertTrue(content.contains("h1"));
    }

    @Test
    void reportTransactions_writesCsvAndJson() throws IOException {
        var tx = new TransactionMetrics(BigInteger.valueOf(1), "tx1", "from", "to", BigDecimal.ZERO, BigInteger.TEN);
        svc.reportTransactions(List.of(tx));

        assertTrue(Files.exists(txt));
        assertTrue(Files.exists(json));
        String s = Files.readString(json);
        assertTrue(s.contains("tx1"));
    }

    @Test
    void generateSummaryReport_writesSummary() {
        svc.generateSummaryReport();
        assertTrue(Files.exists(summary));
    }
}
