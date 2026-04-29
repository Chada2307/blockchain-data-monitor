package ekipa.einsteina.monitor.Models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class BlockEntity {
    @Id
    private String blockHash;
    private Long blockNumber;

    private int txCount;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TransEntity> transactions;

    public String getBlockHash() {return blockHash;}
    public void setBlockHash(String blockHash) {this.blockHash = blockHash;}
    public Long getBlockNumber() {return blockNumber;}
    public void setBlockNumber(Long blockNumber) {this.blockNumber = blockNumber;}
    public int getTxCount() {return txCount;}
    public void setTxCount(int txCount) {this.txCount = txCount;}
    public Set<TransEntity> getTransactions() {return transactions;}
    public void setTransactions(Set<TransEntity> transactions) {this.transactions = transactions;}
}
