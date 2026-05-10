package ekipa.einsteina.monitor.logic;

import ekipa.einsteina.monitor.Models.BlockEntity;
import ekipa.einsteina.monitor.interfaces.BlockRepository;
import ekipa.einsteina.monitor.interfaces.TransRepository;
import ekipa.einsteina.monitor.reporting.reportingService;
import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MonitorServiceTest {

    @Mock
    Web3j web3j;

    @Mock
    reportingService reporter;

    @Mock
    BlockRepository blockRepository;

    @Mock
    TransRepository transRepository;

    MonitorService monitorService;

  @BeforeEach
    void setUp() throws Exception{
       
        when(web3j.blockFlowable(anyBoolean())).thenReturn(Flowable.empty());

        Request<?, EthBlock> throwingReq = mock(Request.class);
        when(throwingReq.send()).thenThrow(new IOException("init-stop"));
        doReturn(throwingReq).when(web3j).ethGetBlockByNumber(any(DefaultBlockParameter.class), anyBoolean());

        var reqNum = mock(Request.class);
        var ethBlockNumber = mock(org.web3j.protocol.core.methods.response.EthBlockNumber.class);
        when(ethBlockNumber.getBlockNumber()).thenReturn(BigInteger.ZERO);
        when(reqNum.send()).thenReturn(ethBlockNumber);
        when(web3j.ethBlockNumber()).thenReturn(reqNum);

        monitorService = new MonitorService(web3j, reporter, blockRepository, transRepository);
    }

    @Test
    void processSingleBlock_savesBlockAndReports() throws Exception {
        EthBlock.Block block = mock(EthBlock.Block.class);
        when(block.getHash()).thenReturn("h1");
        when(block.getNumber()).thenReturn(BigInteger.ONE);
        when(block.getTransactions()).thenReturn(Collections.emptyList());

        EthBlock ethBlock = mock(EthBlock.class);
        when(ethBlock.getBlock()).thenReturn(block);

        Request<?, EthBlock> req = mock(Request.class);
        when(req.send()).thenReturn(ethBlock);
        doReturn(req).when(web3j).ethGetBlockByNumber(any(DefaultBlockParameter.class), anyBoolean());

        monitorService.processSingleBlock(BigInteger.ONE, false);

        verify(blockRepository, atLeastOnce()).save(any(BlockEntity.class));
        verify(reporter, atLeastOnce()).reportBlockMetrics(anyList());
    }

    @Test
    void processSingleBlock_handlesNullBlockGracefully() throws Exception {
        EthBlock ethBlock = mock(EthBlock.class);
        Request<?, EthBlock> req = mock(Request.class);

        when(req.send()).thenReturn(ethBlock);
        doReturn(req).when(web3j).ethGetBlockByNumber(any(DefaultBlockParameter.class), anyBoolean());
        when(ethBlock.getBlock()).thenReturn(null);

        // should not throw
        monitorService.processSingleBlock(BigInteger.ONE, false);

        verify(blockRepository, never()).save(any());
    }
}
