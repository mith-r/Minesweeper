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

    fun toggleFlag(row: Int, col: Int) {
        val newBoard = board.map { it.copyOf() }.toTypedArray()
        val cell = newBoard[row][col]
        newBoard[row][col] = cell.copy(isFlagged = !cell.isFlagged)
        board = newBoard
    }

    fun reveal(row: Int, col: Int) {
        val newBoard = board.map { it.copyOf() }.toTypedArray()
        val cell = newBoard[row][col]
        if (!cell.isRevealed and !cell.isFlagged) {
            newBoard[row][col] = cell.copy(isRevealed = true)
            board = newBoard
        }

        calculateMines(row,col)
    }

    fun calculateMines(row:Int, col:Int){
        val newBoard = board.map {it.copyOf() }. toTypedArray()
        val cell = newBoard[row][col]
        var mineCount = 0

        for (rc in -1..1){
            for (cc in -1..1){

                val new_row = row + rc
                val new_col = col + cc

                if ((min(new_row, new_col) >= 0)
                    and (max(new_row,new_col) < 5)
                    and ((new_row != row) or (new_col != col))){

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



    fun resetGame(){

    }
}