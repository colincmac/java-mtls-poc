package com.example.demo;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;

import java.security.KeyStore;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientWithMtls() throws Exception {
        // Initialize the Certificate Client
        CertificateClient certificateClient = new CertificateClientBuilder()
                .vaultUrl("https://<YourKeyVaultName>.vault.azure.net/")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        // Retrieve the certificate (replace 'your-cert-name' with actual name)
        byte[] certBytes = certificateClient.getCertificate("your-cert-name").getCer();

        // Load the certificate into a KeyStore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(certBytes), "your-cert-password".toCharArray());

        // Build SSLContext with the client certificate
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, "your-cert-password".toCharArray())
                .build();

        // Configure HttpClient with SSLContext
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec.sslContext(sslContext));

        // Build and return WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}