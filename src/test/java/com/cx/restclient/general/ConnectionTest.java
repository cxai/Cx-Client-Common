package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
public class ConnectionTest extends CommonClientTest {

    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void ssoConnectionTest() {
        CxScanConfig config = initConfig();
        try {
            CxClientDelegator client = new CxClientDelegator(config, log);
            client.init();
        } catch (IOException | CxClientException e) {
            e.printStackTrace();
            log.error("Error running  osa scan: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }
//TODO : Fix this test
    @Ignore("this test fails and needs to be fixed")
    @Test
    public void scaConnectionTest() {
        CxScanConfig config = new CxScanConfig();
        config.setCxOrigin("common");
        AstScaConfig scaConfig = getScaConfig(false);
        config.setAstScaConfig(scaConfig);
        config.addScannerType(ScannerType.AST_SCA);
        try {
            CxClientDelegator delegator = new CxClientDelegator(config, log);
            delegator.getScaClient().testScaConnection();
        } catch (CxClientException | MalformedURLException e) {
            failOnException(e);
        }
    }

    private CxScanConfig initConfig() {
        CxScanConfig config = new CxScanConfig();
        config.setSastEnabled(true);
        config.setUseSSOLogin(true);
        config.setUsername(prop("username"));
        config.setPassword(prop("password"));
        config.setUrl(prop("serverUrl"));
        config.setCxOrigin("common");
        config.setTeamPath("\\CxServer");
        config.setPresetName("Checkmarx Default");
        config.setProjectName("testSSO");
        return config;
    }
}
