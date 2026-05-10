package ekipa.einsteina.monitor.interfaces;

import ekipa.einsteina.monitor.Models.TransEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TransRepositoryTest {

    @Autowired
    TransRepository transRepository;

    @Test
    void findByFromAddress_returnsSavedEntity() {
        TransEntity e = new TransEntity();
        e.setTxHash("tx1");
        e.setFromAddress("addr1");
        e.setToAddress("addr2");
        e.setValueEth(BigDecimal.ZERO);
        e.setGasUsed(BigInteger.TEN);

        transRepository.save(e);

        List<TransEntity> found = transRepository.findByFromAddress("addr1");
        assertFalse(found.isEmpty());
        assertEquals("tx1", found.get(0).getTxHash());
    }

    @Test
    void findAverageGasUsed_computesAverage() {
        TransEntity a = new TransEntity();
        a.setTxHash("a"); a.setGasUsed(BigInteger.valueOf(10));
        a.setValueEth(BigDecimal.ZERO);
        TransEntity b = new TransEntity();
        b.setTxHash("b"); b.setGasUsed(BigInteger.valueOf(20));
        b.setValueEth(BigDecimal.ZERO);

        transRepository.save(a);
        transRepository.save(b);

        Double avg = transRepository.findAverageGasUsed();
        assertNotNull(avg);
        assertEquals(15.0, avg, 0.001);
    }
}