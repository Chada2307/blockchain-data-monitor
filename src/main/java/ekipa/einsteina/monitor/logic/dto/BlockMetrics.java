package ekipa.einsteina.monitor.logic.dto;

import java.math.BigInteger;

public record BlockMetrics(
        BigInteger blockNumber,
        String blockHash,
        int transactionCount
) {}