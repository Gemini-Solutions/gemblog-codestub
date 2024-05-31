package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Http2ClientTest {

    private static final int THREAD_COUNT = 100;
    private static final String BASE_URL = "http://localhost:8080/api/employees";

    private static final Map<Integer, String> apiMap = Map.of(0,"/sortedByName", 1,"/sortedById",
            2,"/sortedBySalary", 3,"/sortedByDesignation",
            4,"/orderByName",5,"/orderBySalary");

    public static void main(String[] args) throws Exception {
        Random random = new Random();
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        HttpClient client = createHttpClient();
        int count = 0;
        for (int i = 0; i < 10000; i++) {
            int x = i%6;
            executorService.submit(() -> {
                try {
                    String api = apiMap.get(x);
                    // String api = apiMap.get(random.nextInt(6));
                    System.out.println("API for thread " + Thread.currentThread().getName() + " is " + api);
                        sendRequest(client, api);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Start time: " + startTime + " ms");
        System.out.println("End time: " + endTime + " ms");
        System.out.println("Execution time: " + executionTime + " ms");

    }

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

    private static void sendRequest(HttpClient client, String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        System.out.println("Response from " + endpoint + ": " + response.body());
    }
}
