package com.eagb.blockchainexample.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.Nullable;

public class Block {
    private int index, nonce;
    private long timestamp;
    private String hash, previousHash, data;

    Block(int index, long timestamp, @Nullable String previousHash, @Nullable String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        hash = Block.calculateHash(this);
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

    static String calculateHash(Block block) {
        if (block != null) {
            MessageDigest messageDigest;

            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            String txt = block.str();
            final byte bytes[] = messageDigest.digest(txt.getBytes());
            final StringBuilder builder = new StringBuilder();

            for (final byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    builder.append('0');
                }

                builder.append(hex);
            }

            return builder.toString();
        }

        return null;
    }

    // Proof-of-Work (mining blocks)
    void mineBlock(int difficulty) {
        nonce = 0;

        while (!getHash().substring(0,  difficulty).equals(addZeros(difficulty))) {
            nonce++;
            hash = Block.calculateHash(this);
        }
    }

    private String addZeros(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append('0');
        }

        return builder.toString();
    }
}
