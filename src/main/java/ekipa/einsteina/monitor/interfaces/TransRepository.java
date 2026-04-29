package ekipa.einsteina.monitor.interfaces;

import ekipa.einsteina.monitor.Models.TransEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransRepository extends JpaRepository<TransEntity, String> {
    List<TransEntity> findByFromAddress(String fromAddress);
    List<TransEntity> findByToAdrress(String toAddress);


}
