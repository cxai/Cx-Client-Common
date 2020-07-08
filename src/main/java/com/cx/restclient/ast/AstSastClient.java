package com.cx.restclient.ast;

import com.cx.restclient.ast.dto.common.ASTConfig;
import com.cx.restclient.ast.dto.common.ASTResults;
import com.cx.restclient.ast.dto.common.ScanConfig;
import com.cx.restclient.ast.dto.common.ScanConfigValue;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.SastScanConfigValue;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AUTH;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class AstSastClient extends AstClient implements Scanner {
    private static final String ENGINE_TYPE_FOR_API = "sast";

    public AstSastClient(CxScanConfig config, Logger log) {
        super(config, log);

        AstSastConfig astConfig = this.config.getAstSastConfig();
        validate(astConfig);

        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
    }

    @Override
    public void init() {
        AstSastConfig astConfig = config.getAstSastConfig();
        httpClient.addCustomHeader(AUTH.WWW_AUTH_RESP, String.format("Bearer %s", astConfig.getAccessToken()));
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SAST.getDisplayName();
    }

    @Override
    public Results initiateScan() {
        ASTResults astResults = new ASTResults();
        AstSastConfig astConfig = config.getAstSastConfig();
        try {
            SourceLocationType locationType = astConfig.getSourceLocationType();
            HttpResponse response;
            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(astConfig, config.getProjectName());
            } else {
                throw new NotImplementedException("The upload flow is not yet supported.");
            }
            String scanId = extractScanIdFrom(response);
            log.info(String.format("Scan started successfully. Scan ID: %s", scanId));

            astResults.setScanId(scanId);
            return astResults;
        } catch (IOException e) {
            throw new CxClientException("Error creating CxAST-SAST scan.", e);
        }
    }

    @Override
    protected ScanConfig getScanConfig() {
        boolean isIncremental = Boolean.TRUE.equals(config.getIncremental());
        String presetName = StringUtils.defaultIfEmpty(config.getPresetName(), "");

        ScanConfigValue configValue = SastScanConfigValue.builder()
                .incremental(Boolean.toString(isIncremental))
                .presetName(presetName)
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();
    }

    @Override
    public ScanResults waitForScanResults() {
        return null;
    }

    @Override
    public ScanResults getLatestScanResults() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        Optional.ofNullable(httpClient).ifPresent(CxHttpClient::close);
    }

    private void validate(ASTConfig astSastConfig) {
        String error = null;
        if (astSastConfig == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isBlank(astSastConfig.getApiUrl())) {
            error = "%s API URL must be provided.";
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }
}
