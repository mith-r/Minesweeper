# Minesweeper

AIT Mobile Software Development, Assignment 1.
A 5×5 Minesweeper game for Android, built with Kotlin and Jetpack Compose. The board is drawn on a `Canvas`.

## How to play

- **Tap** a square to reveal it. The number shows how many of the 8 neighbouring squares hold a mine.
- **Long-press** a square to place a 🚩 flag on a mine.
- Win by flagging all 3 mines **or** revealing all 22 safe squares.
- You lose if you reveal a mine **or** flag a square that isn't a mine.

The board has 3 mines.

## Required features

| Requirement | How it's implemented |
|---|---|
| 5×5 game area with 3 mines | 5×5 `Array<Array<Field>>` in the ViewModel, 3 mines placed at random |
| Choosing between "try a field" and "place a flag" | Gestures instead of a checkbox: tap tries a field, long-press places a flag |
| Hitting a mine ends the game | Revealing a mine shows the game-over dialog |
| Safe field shows the nearby mine count | Each square stores its neighbouring mine count, drawn as a number with `drawText` |
| Flagging a non-mine ends the game | Long-pressing a safe square ends the game |
| Flagging every mine wins | Flagging all 3 mines ends the game with a win |
| Messages to the user | Status bar (flags and time), on-screen hint, and a result dialog |
| Single Activity, Compose UI, Canvas drawing | `MainActivity` with Compose. The board is a single `Canvas` |

## Extra features

- **Safe first tap.** Mines are placed only after your first tap, so you cannot lose on the first tap. 
- **Flood fill.** Revealing a square with no neighbouring mines automatically opens the area around it.
- **Timer.** Starts on the first tap and stops when the game ends. I
- **Flags remaining counter.** Shown in the status bar and calculated directly from the board.
- **Result dialog.**
  - 🏆 "You Win!" with your completion time.
  - 💣 "Game Over!" that says whether you hit a mine or flagged a safe square.
- **All mines revealed** when the game ends.
- **Input locked after the game ends.** Taps and long-presses are ignored until a new game is started.
- **New game** button to restart at any time.
- **Styled board.**
  - Checkerboard grass (hidden) and dirt (revealed) tiles.
  - Colour-coded numbers: 1 is blue, 2 is green, 3 is red.
  - 🚩 and 💣 emoji.
- **Gesture hint** under the board ("Tap to reveal · Long-press to flag").

## Running it

1. Open the project in Android Studio.
2. Pick an emulator or a connected device (Android 7.0 / API 24 or newer).
3. Press **Run**.

## Code layout

- `MinesweeperViewModel.kt`: game state and rules: mine placement, mine counts, revealing, flags, timer, win and lose.
- `MainActivity.kt`: the UI: status bar, the `Canvas` board with tap and long-press handling, and the result dialog.
- `res/values/strings.xml`: every piece of user-visible text, including the emoji, so the app can be translated without code changes. Text containing numbers (flags left, time) uses `%1$d` placeholders.
