package com.eagb.blockchainexample.managers;

import android.content.Context;

import com.eagb.blockchainexample.adapters.BlockAdapter;
import com.eagb.blockchainexample.models.BlockModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlockChainManager {

    private final int difficulty;
    private final List<BlockModel> blocks;
    public final BlockAdapter adapter;

    public BlockChainManager(@NonNull Context context, int difficulty) {
        this.difficulty = difficulty;

        // Creating the 'Genesis block' (first block)
        blocks = new ArrayList<>();
        BlockModel block = new BlockModel(0, System.currentTimeMillis(), null, "Genesis Block");
        block.mineBlock(difficulty);
        blocks.add(block);

        // Setting list of blocks in the adapter
        adapter = new BlockAdapter(context, blocks);
    }

    private BlockModel latestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    // Broadcast block
    public BlockModel newBlock(String data) {
        BlockModel latestBlock = latestBlock();

        return new BlockModel(latestBlock.getIndex() + 1, System.currentTimeMillis(),
                latestBlock.getHash(), data);
    }

    // Requesting Proof-of-Work
    public void addBlock(BlockModel block) {
        if (block != null) {
            block.mineBlock(difficulty);
            blocks.add(block);
        }
    }

    // Validating first block
    private boolean isFirstBlockValid() {
        BlockModel firstBlock = blocks.get(0);

        if (firstBlock.getIndex() != 0) {
            return false;
        }

        if (firstBlock.getPreviousHash() != null) {
            return false;
        }

        return firstBlock.getHash() != null &&
                BlockModel.calculateHash(firstBlock).equals(firstBlock.getHash());
    }

    // Validate new block
    private boolean isValidNewBlock(@Nullable BlockModel newBlock, @Nullable BlockModel previousBlock) {
        if (newBlock != null  &&  previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }

            if (newBlock.getPreviousHash() == null  ||
                    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            return newBlock.getHash() != null &&
                    BlockModel.calculateHash(newBlock).equals(newBlock.getHash());
        }

        return false;
    }

    // Validating current block
    public boolean isBlockChainValid() {
        if (!isFirstBlockValid()) {
            return false;
        }

        for (int i = 1; i < blocks.size(); i++) {
            BlockModel currentBlock = blocks.get(i);
            BlockModel previousBlock = blocks.get(i - 1);

            if (!isValidNewBlock(currentBlock, previousBlock)) {
                return false;
            }
        }

        return true;
    }
}
