package com.example.minesweeper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.minesweeper.ui.theme.MinesweeperTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MinesweeperGameScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MinesweeperGameScreen(modifier: Modifier,
    viewModel: MinesweeperViewModel = viewModel()
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "MineSweeper")

        MinesweeperBoard(
            board = viewModel.board
        )
    }

}

@Composable
fun MinesweeperBoard(
    board: Array<Array<Field>>
){

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1.0f)
            .pointerInput(key1 = Unit) {
                detectTapGestures {
                    offSet ->
                    Log.d("TAG_TAP",
                        "${offSet.x} - ${offSet.y}")

                val row = (offSet.y / (size.height / 5)).toInt()
                val col = (offSet.x / (size.width / 5)).toInt()
                    Log.d("TAG_TAP",
                        "${row},${col} ")
            }
        }
    ) {
        // Draw the Grid
        val gridSize = size.minDimension
        val fifthSize = gridSize / 5

        for (i in 0..5) {
            drawLine(
                color = Color.Black,
                strokeWidth = 3f,
                start = Offset(fifthSize * i, y = 0f),
                end = Offset(fifthSize * i, gridSize)
            )

            drawLine(
                color = Color.Black,
                strokeWidth = 3f,
                start = Offset(x = 0f, y = fifthSize * i),
                end = Offset(x = gridSize, y = fifthSize * i)
            )
        }

        //Draw whats inside
        for (row in 0..4) {
            for (col in 0..4) {
                val box = board[row][col]


            }
        }


    }
}

