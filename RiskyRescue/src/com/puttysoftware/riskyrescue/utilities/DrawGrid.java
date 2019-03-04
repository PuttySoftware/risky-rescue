package com.puttysoftware.riskyrescue.utilities;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.storage.ObjectStorage;

public class DrawGrid extends ObjectStorage {
    public DrawGrid(int numSquares) {
        super(numSquares, numSquares);
    }

    public BufferedImageIcon getImageCell(int row, int col) {
        return (BufferedImageIcon) this.getCell(row, col);
    }

    public void setImageCell(BufferedImageIcon bii, int row, int col) {
        this.setCell(bii, row, col);
    }
}
