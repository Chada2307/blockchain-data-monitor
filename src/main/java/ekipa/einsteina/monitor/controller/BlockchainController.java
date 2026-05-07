package ekipa.einsteina.monitor.controller;

import ekipa.einsteina.monitor.interfaces.BlockRepository;
import ekipa.einsteina.monitor.interfaces.TransRepository;
import ekipa.einsteina.monitor.logic.dto.BlockMetrics;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BlockchainController {

    private BlockRepository blockRepository;
    private TransRepository transRepository;

    public BlockchainController(BlockRepository blockRepository, TransRepository transRepository) {
        this.blockRepository = blockRepository;
        this.transRepository = transRepository;
    }

    @GetMapping("/blocks")
    public List<BlockMetrics> getRecentBlocks() {
        return blockRepository.findAll().stream()
                .limit(10)
                .map(blockEntity -> new BlockMetrics(
                        BigInteger.valueOf(blockEntity.getBlockNumber()),
                        blockEntity.getBlockHash(),
                        blockEntity.getTxCount()
                ))
                .toList();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats(){
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBlocks", blockRepository.count());
        stats.put("avgGas", Math.round(transRepository.findAverageGasUsed() * 100.0) / 100.0);
        return stats;
    }
}
