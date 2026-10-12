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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinesweeperGameScreen(modifier: Modifier,
    viewModel: MinesweeperViewModel = viewModel()
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "MineSweeper Game")
        Text(text = "Flags Remaining: ${3 - viewModel.board.sumOf { r -> r.count { it.isFlagged } }}")
        Text(text = "Time: ${viewModel.seconds}")

        Button(
            onClick = {
                viewModel.resetGame()
            }
        ) {
            Text("New Game")
        }

        MinesweeperBoard(
            board = viewModel.board,
            onCellLongPress = {row, col -> viewModel.toggleFlag(row, col)},
            onCellTap = {row, col -> viewModel.reveal(row,col) }
        )
    }
    if (viewModel.showGameOverDialog){
        AlertDialog(
            onDismissRequest = {},
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.dismissScreen()
                    }
                ) {
                    Text("Close")
                }
            },
            title = {Text(if (viewModel.board.sumOf { r -> r.count { it.isRevealed && !it.isMine } } == 22) "You Win!" else "Game Over!")},
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetGame()
                    }
                ) {
                    Text("New Game")
                }
            }

        )
    }

}

@Composable
fun MinesweeperBoard(
    board: Array<Array<Field>>,
    onCellTap: (Int, Int) -> Unit,
    onCellLongPress: (Int, Int) -> Unit
){
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1.0f)
            .pointerInput(key1 = Unit) {
                fun cellAt(offSet: Offset): Pair<Int, Int> {
                    val row = (offSet.y / (size.height / 5)).toInt()
                    val col = (offSet.x / (size.width / 5)).toInt()
                    return Pair(row, col)
                }

                detectTapGestures(
                    onTap = { offSet ->
                        val (row, col) = cellAt(offSet)
                        onCellTap(row, col)
                    },
                    onLongPress = { offSet ->
                        val (row, col) = cellAt(offSet)
                        onCellLongPress(row, col)
                    }
                )
            }
    ) {
        // Draw the Grid
        val gridSize = size.minDimension
        val fifthSize = gridSize / 5

        //Draw whats inside

        for (row in 0..4) {
            for (col in 0..4) {
                val box = board[row][col]
                val centerX = col * fifthSize + fifthSize / 2
                val centerY = row * fifthSize + fifthSize / 2
                val light = (row + col) % 2 == 0
                val grass = if (light) Color(0xFFAAD751) else Color(0xFFA2D149)
                val dirt = if (light) Color(0xFFE5C29F) else Color(0xFFD7B899)

                val textLayoutResult: TextLayoutResult =
                    textMeasurer.measure(
                        text = box.minesAround.toString(),
                        style = TextStyle(fontSize = (fifthSize * 0.6f).toSp() , fontWeight = FontWeight.Bold,
                            color = listOf(Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFD32F2F)).getOrElse(box.minesAround - 1) { Color.Black })
                    )
                val textSize = textLayoutResult.size

                if (!box.isRevealed){

                    // Green Background
                    drawRect(
                        color = grass,
                        topLeft = Offset(col * fifthSize, row * fifthSize),
                        size = Size(fifthSize, fifthSize)
                    )

                    //Placing the Flag

                    if (box.isFlagged){

                        val poleX = centerX - fifthSize * 0.15f
                        val poleTop = centerY - fifthSize * 0.3f
                        val poleBottom = centerY + fifthSize * 0.3f

                        //Flag Pole
                        drawLine(
                            color = Color.Black,
                            strokeWidth = fifthSize * 0.05f,
                            start = Offset(poleX, poleBottom),
                            end = Offset(poleX, poleTop)
                        )

                        // Flag Rectangle, attached to the right of the pole top
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(poleX, poleTop),
                            size = Size(fifthSize * 0.35f, fifthSize * 0.25f)
                        )

                    }

                } else{
                    drawRect(
                        color = dirt,
                        topLeft = Offset(col * fifthSize, row * fifthSize),
                        size = Size(fifthSize, fifthSize)
                    )

                    if (box.minesAround != 0)
                    {

                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                x  = centerX - textSize.width/2,
                                y = centerY - textSize.height / 2
                            ),
                        )
                    }

                    if (box.isMine){
                        drawCircle(
                            color = Color.Black,
                            radius = fifthSize/2 * 0.9f,
                            center = Offset(centerX, centerY)
                        )
                    }






                }


            }
        }


    }
}

