package ekipa.einsteina.monitor.Models;


import ekipa.einsteina.monitor.logic.dto.TransactionMetrics;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
public class TransEntity {

    @Id
    private String txHash;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    BigDecimal valueEth;
    BigInteger gasUsed;

    @ManyToOne
    @JoinColumn(name = "block_hash")
    private BlockEntity block;

    public TransEntity() {}

    public TransEntity(TransactionMetrics metrics, BlockEntity block) {
        this.txHash = metrics.txHash();
        this.fromAddress = metrics.from();
        this.toAddress = metrics.to();
        this.gasUsed = metrics.gasUsed();
        this.block = block;
        this.valueEth = metrics.valueEth();
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {this.txHash = txHash;}
    public String getFromAddress() {return fromAddress;}
    public void setFromAddress(String fromAddress) {this.fromAddress = fromAddress;}
    public String getToAddress() {return toAddress;}
    public void setToAddress(String toAdrress) {this.toAddress = toAdrress;}
    public BigDecimal getValueEth() {return valueEth;}
    public void setValueEth(BigDecimal valueEth) {this.valueEth = valueEth;}
    public BigInteger getGasUsed() {return gasUsed;}
    public void setGasUsed(BigInteger gasUsed) {this.gasUsed = gasUsed;}
    public BlockEntity getBlock() {return block;}
    public void setBlock(BlockEntity block) {this.block = block;}
}


