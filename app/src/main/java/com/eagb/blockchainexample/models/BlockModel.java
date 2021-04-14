package com.eagb.blockchainexample.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.Nullable;

public class BlockModel {

    private final int index;
    private int nonce;
    private final long timestamp;
    private String hash;
    private final String previousHash;
    private final String data;

    public BlockModel(int index, long timestamp, @Nullable String previousHash, @Nullable String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;

        nonce = 0;
        hash = BlockModel.calculateHash(this);
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    private String str() {
        return index + timestamp + previousHash + data + nonce;
    }

    // This is the process to find a hash with the exact
    // number of leading zeros set by the user
    public static String calculateHash(@Nullable BlockModel block) {
        if (block != null) {
            MessageDigest messageDigest;

            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            String txt = block.str();
            final byte[] bytes = messageDigest.digest(txt.getBytes());
            final StringBuilder builder = new StringBuilder();

            for (final byte b : bytes) {
                final String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    builder.append('0');
                }

                builder.append(hex);
            }

            return builder.toString();
        }

        return null;
    }

    // Proof-of-Work (mining blocks).
    // Adding the number of zeros set by the user.
    // The more zeros at the beginning of the hash, the more difficult
    // it will be to find a hash with that request.
    // The calculations will be done by the CPU. This process is called 'mining'
    public void mineBlock(int difficulty) {
        nonce = 0;

        while (!getHash().substring(0,  difficulty).equals(addZeros(difficulty))) {
            nonce++;
            hash = BlockModel.calculateHash(this);
        }
    }

    // Adding zeros in a String to set more difficulty
    private String addZeros(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append('0');
        }

        return builder.toString();
    }
}
