package com.whodesire.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class Encoder {

	private static final String ALGORITHM = "PBEWITHSHA-256AND256BITAES-CBC-BC";

	private static final BouncyCastleProvider bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();

	public Encoder() {

		if(!OneMethod.isBouncyCastleAdded())
			Security.addProvider(bouncyCastleProvider);
//		Security.insertProviderAt(bouncyCastleProvider, 1);
		
	}

	@SuppressWarnings("unused")
	private char[] byteToCharArray(byte[] bytes){
		String value = new String(bytes);
		return value.toCharArray();
	}

	@SuppressWarnings("unused")
	private byte[] charToByteArray(char[] chars){
		return String.valueOf(chars).getBytes();
	}

	private StandardPBEStringEncryptor getStandardPBEStringEncryptor(char[] secretKey){

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setProvider(bouncyCastleProvider);
		encryptor.setPasswordCharArray(secretKey);

		return encryptor;
	}
	
	public final char[] encryptSentence(char[] secretKey, char[] message)  {

		StandardPBEStringEncryptor enc = getStandardPBEStringEncryptor(secretKey);

		return enc.encrypt(String.valueOf(message)).toCharArray();

	}

	public final char[] decryptSentence(char[] secretKey, char[] message) {

		StandardPBEStringEncryptor enc = getStandardPBEStringEncryptor(secretKey);
		return enc.decrypt(String.valueOf(message)).toCharArray();

	}

	public final List<char[]> encryptList(char[] secretKey, List<char[]> message) {

		List<char[]> enc_list = new ArrayList<>();
		StandardPBEStringEncryptor enc = getStandardPBEStringEncryptor(secretKey);

		for(char[] ch : message)
			enc_list.add(enc.encrypt(String.valueOf(ch)).toCharArray());

		return enc_list;
	}

	public final List<char[]> decryptList(char[] secretKey, List<char[]> message) {

		List<char[]> dec_list = new ArrayList<>();
		StandardPBEStringEncryptor enc = getStandardPBEStringEncryptor(secretKey);

		for(char[] ch : message)
			dec_list.add(enc.decrypt(String.valueOf(ch)).toCharArray());

		return dec_list;
	}

}