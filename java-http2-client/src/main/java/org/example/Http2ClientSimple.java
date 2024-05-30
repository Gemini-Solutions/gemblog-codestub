package org.example;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Http2ClientSimple {

    private static final String BASE_URL = "https://10.50.16.16:8444/api/employees";

    private static HttpClient createHttpClient() throws Exception {
        // Trust all certificates for simplicity (not recommended for production)
        TrustManagerFactory trustAllCerts = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustAllCerts.init((KeyStore) null);
        X509TrustManager trustManager = (X509TrustManager) trustAllCerts.getTrustManagers()[0];
        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}
            @Override
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[]{trustManager}, new java.security.SecureRandom());

// Automatically upgrades the connection to http2 if supported
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = createHttpClient();
        Thread t = new Thread(
                () -> {
                    HttpRequest request = null;
                    try {
                        request = HttpRequest.newBuilder()
                                .uri(new URI(BASE_URL + "/sortedById"))
                                .GET()
                                .build();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        System.out.println("Inside thread");
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("inside thread after response" + "/sortedById" + ": " + response.body());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
        );

        t.start();
        Thread.sleep(1000);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/sortedBySalary"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("inside main Response from " + "/sortedBySalary" + ": " + response.body());
        t.join();

    }
}
