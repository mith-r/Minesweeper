package com.example.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.minesweeper.ui.theme.MinesweeperTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minesweeper.viewmodel.Field
import com.example.minesweeper.viewmodel.MinesweeperViewModel

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
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(0.8f).background(Color(0xFF4A752C)).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.flags_left, 3 - viewModel.board.sumOf { r -> r.count { it.isFlagged } }), color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.time, viewModel.seconds), color = Color.White, style = MaterialTheme.typography.titleLarge)
        }

        MinesweeperBoard(
            board = viewModel.board,
            onCellLongPress = {row, col -> viewModel.toggleFlag(row, col)},
            onCellTap = {row, col -> viewModel.reveal(row,col) }
        )

        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.hint), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { viewModel.resetGame() }) { Text(stringResource(R.string.new_game)) }
    }
    if (viewModel.showGameOverDialog){
        val won = viewModel.board.sumOf { r -> r.count { it.isRevealed && !it.isMine } } == 22 ||
                viewModel.board.sumOf { r -> r.count { it.isFlagged && it.isMine } } == 3
        AlertDialog(
            onDismissRequest = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissScreen()
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            icon = {Text(stringResource(if (won) R.string.trophy else R.string.bomb), fontSize = 48.sp)},
            title = {Text(stringResource(if (won) R.string.you_win else R.string.game_over))},
            text = {Text(when {
                won -> stringResource(R.string.win_message, viewModel.seconds)
                viewModel.board.any { r -> r.any { it.isFlagged && !it.isMine } } -> stringResource(R.string.wrong_flag_message)
                else -> stringResource(R.string.hit_mine_message)
            }, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)},
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetGame()
                    }
                ) {
                    Text(stringResource(R.string.new_game))
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
    val flagEmoji = stringResource(R.string.flag)
    val bombEmoji = stringResource(R.string.bomb)
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
                        val flag = textMeasurer.measure(flagEmoji, TextStyle(fontSize = (fifthSize * 0.6f).toSp()))
                        drawText(
                            textLayoutResult = flag,
                            topLeft = Offset(centerX - flag.size.width / 2, centerY - flag.size.height / 2)
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
                        val bomb = textMeasurer.measure(bombEmoji, TextStyle(fontSize = (fifthSize * 0.6f).toSp()))
                        drawText(
                            textLayoutResult = bomb,
                            topLeft = Offset(centerX - bomb.size.width / 2, centerY - bomb.size.height / 2)
                        )
                    }

                }
            }
        }


    }
}

