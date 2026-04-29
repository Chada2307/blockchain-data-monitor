package ekipa.einsteina.monitor.Models;


import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
public class TransEntity {

    @Id
    private String txHash;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAdrress;

    BigInteger valueWei;
    BigInteger gasUsed;

    @ManyToOne
    @JoinColumn(name = "block_hash")
    private BlockEntity block;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {this.txHash = txHash;}
    public String getFromAddress() {return fromAddress;}
    public void setFromAddress(String fromAddress) {this.fromAddress = fromAddress;}
    public String getToAdrress() {return toAdrress;}
    public void setToAdrress(String toAdrress) {this.toAdrress = toAdrress;}
    public BigInteger getValueWei() {return valueWei;}
    public void setValueWei(BigInteger valueWei) {this.valueWei = valueWei;}
    public BigInteger getGasUsed() {return gasUsed;}
    public void setGasUsed(BigInteger gasUsed) {this.gasUsed = gasUsed;}
    public BlockEntity getBlock() {return block;}
    public void setBlock(BlockEntity block) {this.block = block;}
}


