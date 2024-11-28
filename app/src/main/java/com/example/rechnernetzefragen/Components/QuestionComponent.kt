package com.example.rechnernetzefragen.Components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rechnernetzefragen.Option
import com.example.rechnernetzefragen.Question
import com.example.rechnernetzefragen.ui.theme.DarkGreen
import com.example.rechnernetzefragen.ui.theme.ErrorRed
import com.example.rechnernetzefragen.ui.theme.LightDarkGreen
import com.example.rechnernetzefragen.ui.theme.LightGreen

@Composable
fun QuestionComponent(
    question: Question,
    currentInputs: List<String>,
    onValueChanged: (List<String>) -> Unit,
    validate: Boolean
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    Column {
        question.options.forEach { option ->
            val isChosen = currentInputs.contains(option.answer)
            val questionBackgroundColor =
                if (validate) {
                    if (isChosen && option.isCorrect)
                        DarkGreen
                    else if(!isChosen && !option.isCorrect)
                        Color.White
                    else if(isChosen && !option.isCorrect)
                        ErrorRed
                    else LightDarkGreen.copy(alpha = 0.3f)
                } else {
                    if (isChosen) LightGreen else Color.White
                }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 6.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        0.5.dp,
                        Color.Black,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        vibrator.vibrate(
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        )
                        if (isChosen) {
                            onValueChanged(currentInputs.filter { it != option.answer })
                        } else {
                            val list = currentInputs.toMutableList()
                            list.add(option.answer)
                            onValueChanged(list)
                        }
                    }
                    .background(questionBackgroundColor)

            ) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    fontSize = 18.sp,
                    text = option.answer
                )
            }
        }
    }
}

@Composable
@Preview
fun RadioButtonPreview() {
    QuestionComponent(
        Question("this is the Question", listOf(Option("1", false), Option("2", true))),
        currentInputs = listOf("2"),
        onValueChanged = {},
        validate = true
    )
}