package ekipa.einsteina.monitor.reporting;

import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

@Service
public class reportingService {
    public void reportToFile(BigInteger blockNumber, String ethBlockHash, int trans) throws FileNotFoundException {
        try{
            PrintWriter zapis = new PrintWriter(new FileOutputStream("./reporting.txt", true));
            zapis.println(blockNumber + " --- " + ethBlockHash + " --- " + trans);
            zapis.close();
        }catch (IOException e){
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
