package com.example.minesweeper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class BoardCell(val row: Int, val col: Int)
data class Field (var isMine: Boolean, var isFlagged: Boolean, var isRevealed: Boolean, var minesAround: Int)

class MinesweeperViewModel: ViewModel() {

    var board by mutableStateOf(
        Array(5){
            Array(5){
                Field(isMine = false, isFlagged = false, isRevealed = false, minesAround = 0)
            }
        }
    )
}