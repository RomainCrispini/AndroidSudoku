package com.romain.app11sudoku;

import java.io.Serializable;

/**
 * This class represent the grid. The grid contains 81 cells (of type GameCell).
 */
public class GameBoard {

    /**
     * This class represent one cell and it's informations.
     */
    public static class GameCell {
        public int realValue;
        public int assumedValue;
        public boolean isInitial = false;
        public boolean [] marks = { false, false, false, false, false, false, false, false, false };

        public GameCell( int realValue ) {
            this.realValue = realValue;
        }

        public GameCell( int realValue, int isInitial ) {
            this.realValue = realValue;
            this.isInitial = isInitial == 1;
            if ( this.isInitial ) this.assumedValue = realValue;
        }
    }


    public GameLevel level;
    public boolean bigNumber = true;

    // Permet de savoir sur quelle cellule on a cliqué
    public int currentCellX = -1;
    public int currentCellY = -1;

    // Tableau qui copntient les data de chacune de cellules
    public GameCell [][] cells;

    /**
     * The class constructor
     * @param level     The associated level.
     * @param cells     The states for each cells of the grid.
     */
    private GameBoard( GameLevel level, GameCell [][] cells ) {
        this.level = level;
        this.cells = cells;
    }

    /**
     * Return the currently selected value. A cell must be selected, otherwise 0 is returned.
     */
    public int getSelectedValue() {
        // We need to know the current cell
        if ( this.currentCellX == -1 ) return 0;
        if ( this.currentCellY == -1 ) return 0;

        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];
        return currentCell.assumedValue;
    }

    /**
     * This method change the state of the selected cell for this grid.
     * If no cell is selected, the method do nothing.
     * We cannot change the state af an initial state.
     * @param value     The value to insert in the selected cell
     */
    public void pushValue( int value ) {
        // We need to know the current cell
        if ( this.currentCellX == -1 ) return;
        if ( this.currentCellY == -1 ) return;

        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];
        // We cannot update an initial cell
        if ( currentCell.isInitial ) return;

        if ( this.bigNumber ) {
            // Change the assumed value
            currentCell.assumedValue = value;
        } else {
            // Change the mark states
            currentCell.marks[value-1] = ! currentCell.marks[value-1];
        }
    }

    /**
     * Clear all informations (value and marks) for the selected cell.
     * We cannot change the state af an initial state.
     */
    public void clearCell() {
        // We need to know the current cell
        if ( this.currentCellX == -1 ) return;
        if ( this.currentCellY == -1 ) return;

        GameCell currentCell = this.cells[ this.currentCellY ][ this.currentCellX ];

        // We cannot update an initial cell
        if ( currentCell.isInitial ) return;

        currentCell.assumedValue = 0;
        currentCell.marks = new boolean[] { false, false, false, false, false, false, false, false, false };
    }

    /**
     * A factory method that produce an initial grid to solve.
     * @param level     Just the medium level is actually supported
     * @return          A new grid to solve.
     */
    public static GameBoard getGameBoard( GameLevel level ) {

        if ( level != GameLevel.MEDIUM ) throw new RuntimeException( "Not actually implemented" );

        // TODO add code for generate differents Grid for each level

        return new GameBoard( level, new GameCell[][] {
                { new GameCell(9,1), new GameCell(2,0), new GameCell(8,0),
                        new GameCell(7,1), new GameCell(5,0), new GameCell(4,0),
                            new GameCell(1,1), new GameCell(3,0), new GameCell(6,0) },
                { new GameCell(6,0), new GameCell(7,0), new GameCell(1,0),
                        new GameCell(8,1), new GameCell(2,0), new GameCell(3,1),
                            new GameCell(5,0), new GameCell(4,0), new GameCell(9,0) },
                { new GameCell(3,0), new GameCell(5,1), new GameCell(4,1),
                        new GameCell(9,0), new GameCell(1,1), new GameCell(6,0),
                            new GameCell(2,0), new GameCell(7,1), new GameCell(8,0) },

                { new GameCell(4,1), new GameCell(9,1), new GameCell(6,0),
                        new GameCell(2,0), new GameCell(3,0), new GameCell(7,0),
                            new GameCell(8,1), new GameCell(5,1), new GameCell(1,0) },
                { new GameCell(8,0), new GameCell(1,1), new GameCell(5,0),
                        new GameCell(4,1), new GameCell(6,0), new GameCell(9,1),
                            new GameCell(7,0), new GameCell(2,1), new GameCell(3,0) },
                { new GameCell(7,0), new GameCell(3,1), new GameCell(2,1),
                        new GameCell(5,0), new GameCell(8,0), new GameCell(1,0),
                            new GameCell(9,0), new GameCell(6,1), new GameCell(4,1) },

                { new GameCell(5,0), new GameCell(4,1), new GameCell(3,0),
                        new GameCell(1,0), new GameCell(9,1), new GameCell(2,0),
                            new GameCell(6,1), new GameCell(8,1), new GameCell(7,0) },
                { new GameCell(2,0), new GameCell(6,0), new GameCell(9,0),
                        new GameCell(3,1), new GameCell(7,0), new GameCell(8,1),
                        new GameCell(4,0), new GameCell(1,0), new GameCell(5,0) },
                { new GameCell(1,0), new GameCell(8,0), new GameCell(7,1),
                        new GameCell(6,0), new GameCell(4,0), new GameCell(5,1),
                            new GameCell(3,0), new GameCell(9,0), new GameCell(2,1) }
        });
    }

}