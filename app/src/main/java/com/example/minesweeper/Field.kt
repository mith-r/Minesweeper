package com.example.minesweeper

data class BoardCell(val row: Int, val col: Int)
class Field (var isMine: Boolean, var isFlagged: Boolean, var isRevealed: Boolean, var minesAround: Int)

class MineSwe