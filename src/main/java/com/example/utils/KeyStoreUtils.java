package com.example.utils;

import com.example.repository.CertificateRepository;
import com.example.repository.UserRepository;
import com.example.service.CertificateService;
import com.example.service.interfaces.ICertificateService;
import com.example.service.interfaces.IUserService;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Component
public class KeyStoreUtils {

    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEYSTORE_PROVIDER = "SUN";
    private static final String KEYSTORE_FILE_EXTENSION = ".jks";
    private static final String PASSWORD = "ibtim22";
    private static final String KEYSTORE_PATH = "C://Users//Svetozar//Desktop//DRUGI SEMESTAR//Informaciona bezbednost//Projekat//BackendTim22IB//src//main//java//com//example//keystores/mykeystore";

    public KeyStore loadKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE, KEYSTORE_PROVIDER);
            File keystoreFile = new File(KEYSTORE_PATH + KEYSTORE_FILE_EXTENSION);
            FileInputStream inputStream = new FileInputStream(keystoreFile);
            keyStore.load(inputStream, PASSWORD.toCharArray());
            inputStream.close();

//            Enumeration<String> aliases = keyStore.aliases();
//            while (aliases.hasMoreElements()) {
//                String alias = aliases.nextElement();
//
//                keyStore.deleteEntry(alias);
//            }

            FileOutputStream outputStream = new FileOutputStream(keystoreFile);
            keyStore.store(outputStream, PASSWORD.toCharArray());
            outputStream.close();

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

