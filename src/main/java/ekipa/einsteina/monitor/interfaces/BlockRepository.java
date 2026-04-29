package ekipa.einsteina.monitor.interfaces;

import ekipa.einsteina.monitor.Models.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, String> {
    Optional<BlockEntity> findByBlockHash(Long blockNumber);
    long count();
}
