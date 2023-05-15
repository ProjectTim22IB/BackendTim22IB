package com.example.utils;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Component
public class KeyStoreUtils {

    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEYSTORE_PROVIDER = "SUN";
    private static final String KEYSTORE_FILE_EXTENSION = ".jks";
    private static final String PASSWORD = "ibtim22";
    private static final String KEYSTORE_PATH = "C://Users//Svetozar//Desktop//DRUGI SEMESTAR//Informaciona bezbednost//Projekat//BackendTim22IB//src//main//java//com//example//keystores/mykeystore";

    public KeyStore loadKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
        FileInputStream inputStream = new FileInputStream(KEYSTORE_PATH + ".jks");
        keyStore.load(inputStream, PASSWORD.toCharArray());
        return keyStore;
    }

    public KeyStore createKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
        keyStore.load(null, PASSWORD.toCharArray());
        return keyStore;
    }

    public void saveCertificateToKeyStore(KeyStore keyStore, String alias, Certificate certificate, PrivateKey privateKey) throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, PASSWORD.toCharArray(), new Certificate[]{certificate});
    }

    public void saveKeyStoreToFile(KeyStore keyStore) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        FileOutputStream outputStream = new FileOutputStream(Paths.get(KEYSTORE_PATH + KEYSTORE_FILE_EXTENSION).toString());
        keyStore.store(outputStream, PASSWORD.toCharArray());
        outputStream.close();
    }
}

