package ekipa.einsteina.monitor.reporting;

import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class reportingService {
    private final String FILE_PATH = "raport_blockchain.txt";

    public void reportBlock(BigInteger number, String hash, int trans){
        String line = String.format("current block: %s | Hash: %s | Liczba TX: %d", number, hash, trans);
        System.out.println(line);
        reportToFile(line);
    }

    public void reportTrans(String txHash, String from, String to, BigInteger valueWei, BigInteger gasUsed){

        BigDecimal valueEth = Convert.fromWei(valueWei.toString(), Convert.Unit.ETHER);

        String line = String.format("  -> TX Hash: %s\n     Od: %s | Do: %s\n     Wartość: %f ETH |  Gas: %s",
                txHash, from, to, valueEth, gasUsed);

        System.out.println(line);
        reportToFile(line);
    }
    public void reportToFile(String Line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            out.println(Line);
        } catch (IOException e) {
            System.err.println("blad zapisu do pliku: " + e.getMessage());
        }
    }
}
