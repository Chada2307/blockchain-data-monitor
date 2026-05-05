package ekipa.einsteina.monitor.logic.model;

import java.math.BigInteger;

public record BlockMetrics(
        BigInteger blockNumber,
        String blockHash,
        int transactionCount
) {}