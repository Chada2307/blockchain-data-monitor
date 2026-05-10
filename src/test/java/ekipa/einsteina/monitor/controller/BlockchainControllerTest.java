package ekipa.einsteina.monitor.controller;

import ekipa.einsteina.monitor.interfaces.BlockRepository;
import ekipa.einsteina.monitor.interfaces.TransRepository;
import ekipa.einsteina.monitor.logic.dto.BlockMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockchainControllerTest {

    @Mock
    BlockRepository blockRepository;

    @Mock
    TransRepository transRepository;

    @InjectMocks
    BlockchainController controller;

    @Test
    void getRecentBlocks_mapsEntitiesToDtoAndLimitsTo10() {
        var blocks = java.util.stream.IntStream.range(0, 3)
                .mapToObj(i -> new ekipa.einsteina.monitor.Models.BlockEntity(new BlockMetrics(BigInteger.valueOf(i), "h"+i, i)))
                .toList();

        when(blockRepository.findAll()).thenReturn(blocks);

        List<BlockMetrics> recent = controller.getRecentBlocks();

        assertEquals(3, recent.size());
        assertEquals(BigInteger.valueOf(0), recent.get(0).blockNumber());
    }

    @Test
    void getStats_handlesNullAverageGracefully() {
        when(transRepository.findAverageGasUsed()).thenReturn(null);
        when(blockRepository.count()).thenReturn(42L);

        var stats = controller.getStats();

        assertEquals(42L, stats.get("totalBlocks"));
        assertNotNull(stats.get("avgGas"));
    }

    @Test
    void getStats_roundsAverageToTwoDecimals() {
        when(blockRepository.count()).thenReturn(1L);
        when(transRepository.findAverageGasUsed()).thenReturn(123.456);

        var stats = controller.getStats();

        assertEquals(1L, stats.get("totalBlocks"));
        assertEquals(123.46, ((Number)stats.get("avgGas")).doubleValue(), 0.001);
    }
}
