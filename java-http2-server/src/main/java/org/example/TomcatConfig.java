//package org.example;
//
//import org.apache.coyote.http2.Http2Protocol;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//
//    @Bean
//    public TomcatServletWebServerFactory tomcatFactory() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void customizeConnector(org.apache.catalina.connector.Connector connector) {
//                super.customizeConnector(connector);
//                connector.addUpgradeProtocol(new Http2Protocol());
//            }
//        };
//    }
//}
