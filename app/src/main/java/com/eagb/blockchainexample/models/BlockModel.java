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

    /**
     * Block model constructor.
     *
     * @param index is the index of the block.
     * @param timestamp is the timestamp of the block.
     * @param previousHash is the hash of the previous block.
     * @param data is the data of the block.
     */
    public BlockModel(int index, long timestamp, @Nullable String previousHash, @Nullable String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;

        nonce = 0;
        hash = BlockModel.calculateHash(this);
    }

    /**
     * Getter of the index.
     *
     * @return the index of the block.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter of the nonce.
     *
     * @return the nonce of the block.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter of the hash.
     *
     * @return the hash of the block.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Getter of the previous hash.
     *
     * @return the previous hash of the block.
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Getter of the data.
     *
     * @return the data of the block.
     */
    public String getData() {
        return data;
    }

    /**
     * Getter of the nonce.
     *
     * @return the nonce of the block.
     */
    private String str() {
        return index + timestamp + previousHash + data + nonce;
    }

    /**
     * Calculate the hash of the block. This is the process to find a hash with the exact
     * number of leading zeros set by the user.
     *
     * @param block is the block to calculate the hash.
     * @return the hash of the block.
     */
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

    /**
     * Mine the block. Proof-of-Work (mining blocks). Adding the number of zeros set by the user.
     * The more zeros at the beginning of the hash, the more difficult it will be to find a hash
     * with that request. The calculations will be done by the CPU. This process is called 'mining'.
     *
     * @param difficulty is the number of zeros at the beginning of the hash.
     */
    public void mineBlock(int difficulty) {
        nonce = 0;

        while (!getHash().substring(0,  difficulty).equals(addZeros(difficulty))) {
            nonce++;
            hash = BlockModel.calculateHash(this);
        }
    }

    /**
     * Add zeros in a String to the hash to set more difficulty.
     *
     * @param length is the number of zeros to add.
     * @return the hash with the added zeros.
     */
    private String addZeros(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append('0');
        }

        return builder.toString();
    }
}
