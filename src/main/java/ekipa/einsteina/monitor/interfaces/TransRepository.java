package ekipa.einsteina.monitor.interfaces;

import ekipa.einsteina.monitor.Models.TransEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransRepository extends JpaRepository<TransEntity, String> {
    List<TransEntity> findByFromAddress(String fromAddress);
    List<TransEntity> findByToAddress(String toAddress);

    @Query("SELECT AVG(t.gasUsed) FROM TransEntity t")
    Double findAverageGasUsed();
}
