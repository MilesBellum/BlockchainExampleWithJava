package com.eagb.blockchainexample.utils;

import android.content.Context;

import com.eagb.blockchainexample.adapters.BlockAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class Blockchain {

    private int difficulty;
    private List<Block> blocks;
    public final BlockAdapter adapter;

    public Blockchain(@NonNull Context context, int difficulty) {
        this.difficulty = difficulty;

        // Creating the 'Genesis block' (first block)
        blocks = new ArrayList<>();
        Block block = new Block(0, System.currentTimeMillis(), null, "Genesis Block");
        block.mineBlock(difficulty);
        blocks.add(block);

        // Setting list of blocks in the adapter
        adapter = new BlockAdapter(context, blocks);
    }

    private Block latestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    // Broadcast block
    public Block newBlock(String data) {
        Block latestBlock = latestBlock();
        return new Block(latestBlock.getIndex() + 1, System.currentTimeMillis(),
                latestBlock.getHash(), data);
    }

    // Requesting Proof-of-Work
    public void addBlock(Block block) {
        if (block != null) {
            block.mineBlock(difficulty);
            blocks.add(block);
        }
    }

    // Validating first block
    private boolean isFirstBlockValid() {
        Block firstBlock = blocks.get(0);

        if (firstBlock.getIndex() != 0) {
            return false;
        }

        if (firstBlock.getPreviousHash() != null) {
            return false;
        }

        return firstBlock.getHash() != null &&
                Block.calculateHash(firstBlock).equals(firstBlock.getHash());
    }

    // Validate new block
    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (newBlock != null  &&  previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }

            if (newBlock.getPreviousHash() == null  ||
                    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            return newBlock.getHash() != null &&
                    Block.calculateHash(newBlock).equals(newBlock.getHash());
        }

        return false;
    }

    // Validating current block
    public boolean isBlockChainValid() {
        if (!isFirstBlockValid()) {
            return false;
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            if (!isValidNewBlock(currentBlock, previousBlock)) {
                return false;
            }
        }

        return true;
    }
}
