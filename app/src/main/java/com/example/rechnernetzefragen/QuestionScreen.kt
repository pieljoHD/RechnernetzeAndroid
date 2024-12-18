package com.example.rechnernetzefragen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rechnernetzefragen.Components.QuestionComponent
import com.example.rechnernetzefragen.ui.theme.DarkGreen
import com.example.rechnernetzefragen.ui.theme.LightDarkGreen
import com.example.rechnernetzefragen.ui.theme.LightGreen
import com.example.rechnernetzefragen.ui.theme.VeryLigthGrey

enum class ButtonState(
    val text: String,
    val fillColor: Color
) {
    CHECK("check", Color.White),
    NEXT("next", LightGreen)
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun QuestionScreen(questionStorage: QuestionStorage) {

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val questionList = remember {
        questionStorage.questionList.shuffled()
    }

    val currentSelection: MutableState<List<String>> = remember {
        mutableStateOf(listOf())
    }

    val buttonState: MutableState<ButtonState> = remember {
        mutableStateOf(ButtonState.CHECK)
    }

    val currentQuestionCount = remember {
        mutableIntStateOf(0)
    }

    val currentOptions = remember {
        mutableStateOf(questionList[currentQuestionCount.intValue].options.shuffled())
    }

    val validateState = remember {
        mutableStateOf(false)
    }

    val currentProgress = remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(Unit) {

    }

    currentProgress.floatValue = currentQuestionCount.intValue.toFloat().div(questionList.size)
    val progressAnimated: Float by animateFloatAsState(currentProgress.floatValue, label = "")

    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }

        item {
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(12.dp),
                progress = progressAnimated,
                color = LightDarkGreen,
                strokeCap = StrokeCap.Round
            )
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = questionList[currentQuestionCount.intValue].question,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                QuestionComponent(
                    options = currentOptions.value,
                    currentInputs = currentSelection.value,
                    onValueChanged = {
                        currentSelection.value = it
                    },
                    validate = validateState.value
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp, bottom = 40.dp)
                            .border(
                                1.dp,
                                if (currentSelection.value.isNotEmpty()) DarkGreen else VeryLigthGrey,
                                RoundedCornerShape(50)
                            )
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                if (buttonState.value == ButtonState.CHECK) {
                                    validateState.value = true
                                    buttonState.value = ButtonState.NEXT
                                    if (
                                        isOptionListValid(
                                            questionList[currentQuestionCount.intValue].options.filter { it.isCorrect },
                                            currentSelection.value
                                        )
                                    ) {
                                        val timings: LongArray = longArrayOf(100)
                                        val amplitudes: IntArray = intArrayOf(200)
                                        val repeatIndex = -1
                                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
                                    } else {
                                        val timings: LongArray = longArrayOf(200, 150,200)
                                        val amplitudes: IntArray = intArrayOf(200, 0, 150)
                                        val repeatIndex = -1
                                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
                                    }

                                } else {
                                    validateState.value = false
                                    currentQuestionCount.intValue++
                                    buttonState.value = ButtonState.CHECK
                                    currentSelection.value = listOf()
                                    currentOptions.value =  questionList[currentQuestionCount.intValue].options.shuffled()
                                }
                            }
                            .background(buttonState.value.fillColor)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = buttonState.value.text,
                            fontSize = 18.sp,
                            color = if (currentSelection.value.isNotEmpty()) DarkGreen else Color.LightGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}

fun isOptionListValid(options: List<Option>, strings: List<String>): Boolean {
    val optionAnswers = options.map { it.answer }
    val stringsSet = strings.toSet()

    if (!optionAnswers.all { it in stringsSet }) {
        return false
    }

    if (strings.size > options.size) {
        return false
    }

    return true
}