package com.example.utils;

import com.example.model.Certificate;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
public class KeyStoreUtils {
    private final KeyStore keyStore;

    public KeyStoreUtils() throws KeyStoreException, NoSuchProviderException {
        this.keyStore = KeyStore.getInstance("JKS", "SUN");
    }

    private void loadKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException {
        keyStore.load(new FileInputStream("keystores/certificates.jks"), "IBTim22".toCharArray());
    }

    public void saveNewCertificate(java.security.cert.Certificate generatedCertificate, Certificate certificate, PrivateKey privateKey) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        loadKeyStore();
        keyStore.setCertificateEntry(certificate.getSerialNumber() + "CRT", generatedCertificate);
        String pass = certificate.getSerialNumber() + "PASS";
        keyStore.setKeyEntry(certificate.getSerialNumber() + "KEY", privateKey, pass.toCharArray(), new java.security.cert.Certificate[]{generatedCertificate});
    }
}
