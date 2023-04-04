package com.example;

import com.example.model.Certificate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class KeyStoreUtils {
    private KeyStore keyStore;

    public KeyStoreUtils() throws KeyStoreException, NoSuchProviderException {
        this.keyStore = KeyStore.getInstance("JKS", "SUN");
    }

    private void loadKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException {
        keyStore.load(new FileInputStream("keystores/keystore.jks"), "IBTim22".toCharArray());
    }

    private void saveNewCertificate(X509Certificate generatedCertificate, Certificate certificate, PrivateKey privateKey) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        loadKeyStore();
        keyStore.setCertificateEntry(certificate.getSerialNumber() + "CRT", generatedCertificate);
        String pass = certificate.getSerialNumber() + "PASS";
        keyStore.setKeyEntry(certificate.getSerialNumber() + "KEY", privateKey, pass.toCharArray(), new Certificate[]{generatedCertificate});
    }
}
