/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.repositories.pgp;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

public class PGPEncryptionUtil {

    private static final String BC_PROVIDER_NAME = "BC";

    // pick some sensible encryption buffer size
    private static final int BUFFER_SIZE = 4096;

    // encrypt the payload data using AES-256,
    // remember that PGP uses a symmetric key to encrypt
    // data and uses the public key to encrypt the symmetric
    // key used on the payload.
    private static final int PAYLOAD_ENCRYPTION_ALG = PGPEncryptedData.AES_256;

    // various streams we're taking care of
    private final ArmoredOutputStream armoredOutputStream;
    private final OutputStream encryptedOut;
    private final OutputStream compressedOut;
    private final OutputStream literalOut;

    public PGPEncryptionUtil(PGPPublicKey key, String payloadFilename, OutputStream out) throws PGPException, NoSuchProviderException, IOException {

        // write data out using "ascii-armor" encoding.  This is the
        // normal PGP text output.
        this.armoredOutputStream = new ArmoredOutputStream(out);

        // create an encrypted payload and set the public key on the data generator
        BcPGPDataEncryptorBuilder builder = new BcPGPDataEncryptorBuilder(PAYLOAD_ENCRYPTION_ALG);
        builder.setSecureRandom(new SecureRandom());
        PGPEncryptedDataGenerator encryptGen = new PGPEncryptedDataGenerator(builder);

        BcPublicKeyKeyEncryptionMethodGenerator encKey = new BcPublicKeyKeyEncryptionMethodGenerator(key);
        encryptGen.addMethod(encKey);


        byte[] buffer = new byte[2048];

        // open an output stream connected to the encrypted data generator
        // and have the generator write its data out to the ascii-encoding stream
        this.encryptedOut = encryptGen.open(armoredOutputStream, buffer);

        // compress data.  we are building layers of output streams.  we want to compress here
        // because this is "before" encryption, and you get far better compression on
        // unencrypted data.
        PGPCompressedDataGenerator compressor = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
        this.compressedOut = compressor.open(encryptedOut);

        // now we have a stream connected to a data compressor, which is connected to
        // a data encryptor, which is connected to an ascii-encoder.
        // into that we want to write a PGP "literal" object, which is just a named
        // piece of data (as opposed to a specially-formatted key, signature, etc)
        PGPLiteralDataGenerator literalGen = new PGPLiteralDataGenerator();
        this.literalOut = literalGen.open(compressedOut, PGPLiteralDataGenerator.BINARY,
                payloadFilename, new Date(), new byte[BUFFER_SIZE]);
    }

    /**
     * Get an output stream connected to the encrypted file payload.
     */
    public OutputStream getPayloadOutputStream() {
        return this.literalOut;
    }

    /**
     * Close the encrypted output writers.
     */
    public void close() throws IOException {
        // close the literal output
        literalOut.close();

        // close the compressor
        compressedOut.close();

        // close the encrypted output
        encryptedOut.close();

        // close the armored output
        armoredOutputStream.close();
    }



    /**
     * Decode a PGP public key block and return the keyring it represents.
     */
    public static PGPPublicKeyRing getKeyring(InputStream keyBlockStream) throws IOException {
        // PGPUtil.getDecoderStream() will detect ASCII-armor automatically and decode it,
        // the PGPObject factory then knows how to read all the data in the encoded stream
        PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(keyBlockStream), new BcKeyFingerprintCalculator());

        // these files should really just have one object in them,
        // and that object should be a PGPPublicKeyRing.
        Object o = factory.nextObject();
        if (o instanceof PGPPublicKeyRing) {
            return (PGPPublicKeyRing)o;
        }
        throw new IllegalArgumentException("Input text does not contain a PGP Public Key");
    }

    /**
     * Get the first encyption key off the given keyring.
     */
    public static PGPPublicKey getEncryptionKey(PGPPublicKeyRing keyRing) {
        if (keyRing == null)
            return null;

        // iterate over the keys on the ring, look for one
        // which is suitable for encryption.
        Iterator keys = keyRing.getPublicKeys();
        PGPPublicKey key = null;
        while (keys.hasNext()) {
            key = (PGPPublicKey)keys.next();
            if (key.isEncryptionKey()) {
                return key;
            }
        }
        return null;
    }


    public  static PGPPrivateKey findPrivateKey(InputStream keyIn, long keyID, char[] pass)
            throws IOException, PGPException, NoSuchProviderException
    {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());
        return findPrivateKey(pgpSec.getSecretKey(keyID), pass);

    }

    /**
     * Load a secret key and find the private key in it
     * @param pgpSecKey The secret key
     * @param pass passphrase to decrypt secret key with
     * @return
     * @throws PGPException
     */
    public static PGPPrivateKey findPrivateKey(PGPSecretKey pgpSecKey, char[] pass)
            throws PGPException
    {
        if (pgpSecKey == null) return null;

        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
        return pgpSecKey.extractPrivateKey(decryptor);
    }


    public static void decryptFileFromKey(InputStream in, OutputStream out, PGPPrivateKey skey, char[] passwd)
            throws Exception {

        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpF = new PGPObjectFactory(in, new BcKeyFingerprintCalculator());
        PGPEncryptedDataList enc;
        Object o = pgpF.nextObject();
        if (o instanceof  PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }
        Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData pbe = it.next();

        InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(skey));
        PGPObjectFactory plainFact = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());

        Object message = plainFact.nextObject();

        if (message instanceof  PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) message;
            PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), new BcKeyFingerprintCalculator());

            message = pgpFact.nextObject();
        }

        if (message instanceof  PGPLiteralData) {
            PGPLiteralData ld = (PGPLiteralData) message;

            InputStream unc = ld.getInputStream();
            int ch;

            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
        } else if (message instanceof  PGPOnePassSignatureList) {
            throw new PGPException("Encrypted message contains a signed message - not literal data.");
        } else {
            throw new PGPException("Message is not a simple encrypted file - type unknown.");
        }

        if (pbe.isIntegrityProtected()) {
            if (!pbe.verify()) {
                throw new PGPException("Message failed integrity check");
            }
        }
    }



    @SuppressWarnings("unchecked")
    public static void decryptFile(InputStream in, OutputStream out, InputStream keyIn, char[] passwd)
            throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpF = new PGPObjectFactory(in, new BcKeyFingerprintCalculator());
        PGPEncryptedDataList enc;

        Object o = pgpF.nextObject();
        //
        // the first object might be a PGP marker packet.
        //
        if (o instanceof  PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }

        //
        // find the secret key
        //
        Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
        PGPPrivateKey sKey = null;
        PGPPublicKeyEncryptedData pbe = null;

        while (sKey == null && it.hasNext()) {
            pbe = it.next();

            sKey = findPrivateKey(keyIn, pbe.getKeyID(), passwd);
        }

        if (sKey == null) {
            throw new IllegalArgumentException("Secret key for message not found.");
        }

        InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

        PGPObjectFactory plainFact = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());

        Object message = plainFact.nextObject();

        if (message instanceof  PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) message;
            PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), new BcKeyFingerprintCalculator());

            message = pgpFact.nextObject();
        }

        if (message instanceof  PGPLiteralData) {
            PGPLiteralData ld = (PGPLiteralData) message;

            InputStream unc = ld.getInputStream();
            int ch;

            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
        } else if (message instanceof  PGPOnePassSignatureList) {
            throw new PGPException("Encrypted message contains a signed message - not literal data.");
        } else {
            throw new PGPException("Message is not a simple encrypted file - type unknown.");
        }

        if (pbe.isIntegrityProtected()) {
            if (!pbe.verify()) {
                throw new PGPException("Message failed integrity check");
            }
        }
    }


}
