package com.example.minesweeper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.max
import kotlin.math.min

data class BoardCell(val row: Int, val col: Int)
data class Field (var isMine: Boolean, var isFlagged: Boolean, var isRevealed: Boolean, var minesAround: Int)


class MinesweeperViewModel: ViewModel() {

    var board by mutableStateOf(
        Array(5) {
            Array(5) {
                Field(isMine = true, isFlagged = true, isRevealed = false, minesAround = 0)
            }
        }
    )

    var gameOver by mutableStateOf(false)

    init{
        setupBoard()
    }

    fun toggleFlag(row: Int, col: Int) {
        val newBoard = board.map { it.copyOf() }.toTypedArray()
        val cell = newBoard[row][col]
        if (!cell.isMine){
            gameOver = true
        }
        newBoard[row][col] = cell.copy(isFlagged = !cell.isFlagged)
        board = newBoard
    }

    fun reveal(row: Int, col: Int) {
        val newBoard = board.map { it.copyOf() }.toTypedArray()
        val cell = newBoard[row][col]

        if (cell.isMine){
            gameOver = true
        }
        revealNeighbors(newBoard, row, col)
        board = newBoard
    }

    private fun revealNeighbors(newBoard: Array<Array<Field>>, row:Int, col: Int) {
        if (row !in 0..4 || col !in 0..4) {
            return
        }

        val cell = newBoard[row][col]

        if (cell.isRevealed) {
            return
        }

        newBoard[row][col] = cell.copy(isRevealed = true)

        if (cell.isMine || cell.minesAround != 0) {
            return
        }

        for (rc in -1..1) {
            for (cc in -1..1) {
                revealNeighbors(newBoard, row + rc, col + cc)
            }
        }
    }

    private fun calculateMines(row:Int, col:Int){
        val newBoard = board.map {it.copyOf() }. toTypedArray()
        val cell = newBoard[row][col]
        var mineCount = 0

        for (rc in -1..1){
            for (cc in -1..1){

                val new_row = row + rc
                val new_col = col + cc

                if ((min(new_row, new_col) >= 0)
                    && (max(new_row,new_col) < 5)
                    && ((new_row != row) || (new_col != col))){

                    val newCell = board[new_row][new_col]
                    if (newCell.isMine){
                        mineCount ++
                    }

                }
            }
        }

        newBoard[row][col] = cell.copy(minesAround = mineCount)
        board = newBoard
    }

    private fun setupBoard() {
        gameOver = false
        board = Array(5){Array(5){
                Field(false,false,false,0)
            }
        }
        //Randomly placing Mines
        (0 until 25).shuffled().take(3).forEach{
            n -> board[n/5][n%5].isMine = true
        }

        for (row in 0..4){
            for (col in 0..4) {
                calculateMines(row,col)
            }
        }

    }



    fun resetGame(){
        setupBoard()
    }
}