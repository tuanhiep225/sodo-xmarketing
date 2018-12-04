package com.sodo.xmarketing.utils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.sodo.xmarketing.exception.SodException;

public class ClientHelperUtils {


  private ClientHelperUtils() {
  }

  public static Client createClient() throws SodException {
    TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType)
        // Do nothing here
          throws CertificateException {
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType)
        // Do nothing here
          throws CertificateException {
      }
    }};

    SSLContext ctx = null;
    try {
      ctx = SSLContext.getInstance("TLS");
      ctx.init(null, certs, new SecureRandom());
    } catch (java.security.GeneralSecurityException e) {
      throw new SodException(e.getMessage(), "SECURITY_EXCEPTION");
    }

    HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

    ClientBuilder clientBuilder = ClientBuilder.newBuilder();
    try {
      clientBuilder.sslContext(ctx);
      clientBuilder.hostnameVerifier(new HostnameVerifier() {

        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });
    } catch (

        Exception e) {
      throw new SodException(e.getMessage(), "SECURITY_EXCEPTION");
    }

    return clientBuilder.withConfig(new ClientConfig()).register(JacksonFeature.class).build();
  }
}
