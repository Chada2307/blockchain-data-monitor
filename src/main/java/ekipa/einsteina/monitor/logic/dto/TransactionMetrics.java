package ekipa.einsteina.monitor.logic.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record TransactionMetrics(
        BigInteger blockNumber,
        String txHash,
        String from,
        String to,
        BigDecimal valueEth,
        BigInteger gasUsed
) {}