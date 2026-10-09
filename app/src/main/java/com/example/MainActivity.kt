package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.game.grid.GridGameScreen
import com.example.game.sound.SoundManager
import com.example.game.ui.GameScreen
import com.example.game.ui.ModeSelectScreen
import com.example.ui.theme.MyApplicationTheme

enum class ScreenState {
    MODE_SELECT,
    NORMAL_GRID_GAME,
    MAZE_GAME
}

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundManager = SoundManager(this)

        val prefs = getSharedPreferences("nani_teer_wali_prefs", Context.MODE_PRIVATE)

        setContent {
            MyApplicationTheme {
                // Starts on Mode Selection screen as requested: "jab shuru ho to pocha jae normal mode ya maze mode"
                var currentScreen by remember { mutableStateOf(ScreenState.MODE_SELECT) }
                // Starts at Level 1 when selected: "jab koi game select ho to phele level se hi shuru ho"
                var currentGridLevel by remember { mutableIntStateOf(1) }
                var currentMazeLevel by remember { mutableIntStateOf(1) }

                fun saveGridProgress(lvl: Int) {
                    currentGridLevel = lvl.coerceIn(1, 10)
                    prefs.edit().putInt("grid_level", currentGridLevel).apply()
                }

                fun saveMazeProgress(lvl: Int) {
                    currentMazeLevel = lvl.coerceIn(1, 10)
                    prefs.edit().putInt("maze_level", currentMazeLevel).apply()
                }

                when (currentScreen) {
                    ScreenState.MODE_SELECT -> {
                        ModeSelectScreen(
                            onSelectNormalMode = {
                                currentGridLevel = 1 // Starts from Level 1
                                currentScreen = ScreenState.NORMAL_GRID_GAME
                            },
                            onSelectMazeMode = {
                                currentMazeLevel = 1 // Starts from Level 1
                                currentScreen = ScreenState.MAZE_GAME
                            }
                        )
                    }

                    ScreenState.NORMAL_GRID_GAME -> {
                        BackHandler {
                            currentScreen = ScreenState.MODE_SELECT
                        }
                        GridGameScreen(
                            currentLevelNum = currentGridLevel,
                            soundManager = soundManager,
                            onNavigateToMazeMode = {
                                currentScreen = ScreenState.MODE_SELECT
                            },
                            onLevelAdvanced = { nextLvl ->
                                saveGridProgress(nextLvl)
                            }
                        )
                    }

                    ScreenState.MAZE_GAME -> {
                        BackHandler {
                            currentScreen = ScreenState.MODE_SELECT
                        }
                        GameScreen(
                            currentLevelNum = currentMazeLevel,
                            soundManager = soundManager,
                            onNavigateToLevelSelect = {
                                currentScreen = ScreenState.MODE_SELECT
                            },
                            onLevelAdvanced = { nextLvl ->
                                saveMazeProgress(nextLvl)
                            }
                        )
                    }
                }
            }
        }
    }
}
