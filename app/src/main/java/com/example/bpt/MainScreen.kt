package com.example.bpt

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import com.example.bpt.ui.theme.BPTTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Measurement(
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val systolicBP: Int,
    val diastolicBP: Int,
    val heartRate: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel = MainViewModel()) {
    val data by viewModel.data.observeAsState()

    var isDialogShown by remember { mutableStateOf(false) }

    if (isDialogShown) WriteMeasurementDialog {
        viewModel.newItem(it)
        isDialogShown = false
    }

    Scaffold(
        floatingActionButton = { FAB { isDialogShown = true } },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            data?.groupBy { "${it.dateTime.dayOfMonth} ${it.dateTime.month.name.lowercase()}" }
                ?.forEach { (date, measurements) ->
                    stickyHeader {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(0.2f))
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(text = date, color = Color.Gray)
                        }
                    }

                    items(measurements) { item ->
                        MeasureItem(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            item = item
                        )
                    }
                }
        }
    }
}

@Composable
fun WriteMeasurementDialog(onDismiss: (measurement: Measurement) -> Unit) {
    var systolicBP by remember { mutableStateOf(0) }
    var diastolicBP by remember { mutableStateOf(0) }
    var heartRate by remember { mutableStateOf(0) }

    val options = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)

    Dialog(onDismissRequest = {
        onDismiss(
            Measurement(
                systolicBP = systolicBP,
                diastolicBP = diastolicBP,
                heartRate = heartRate
            )
        )
    }) {
        Surface(
            Modifier
                .size(width = 180.dp, height = 40.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = stringResource(R.string.new_measurement_dialog_title),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.bp))
                    BasicTextField(
                        modifier = Modifier.width(26.dp),
                        value = systolicBP.toString(),
                        onValueChange = { systolicBP = it.toInt() },
                        keyboardOptions = options
                    )
                    Text(text = "/ ")
                    BasicTextField(
                        modifier = Modifier.width(26.dp),
                        value = diastolicBP.toString(),
                        onValueChange = { diastolicBP = it.toInt() },
                        keyboardOptions = options
                    )
                    Text(text = stringResource(R.string.hr))
                    BasicTextField(
                        modifier = Modifier.width(26.dp),
                        value = heartRate.toString(),
                        onValueChange = { heartRate = it.toInt() },
                        keyboardOptions = options
                    )
                }
            }
        }
    }
}

@Composable
private fun FAB(action: () -> Unit) {
    FloatingActionButton(backgroundColor = Color.Red, onClick = action) {
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            tint = Color.White,
            contentDescription = stringResource(R.string.fab_description)
        )
    }
}

@Composable
fun MeasureItem(modifier: Modifier = Modifier, item: Measurement) {
    val color = calculateBPColor(item.systolicBP)

    val background = MaterialTheme.colors.background

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(background, background, color, color, background, background)
                )
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME).take(5),
            color = Color.Gray,
            style = MaterialTheme.typography.caption
        )
        Text(text = "${item.systolicBP} / ${item.diastolicBP}")
        HeartRate(item.heartRate)
    }
}

@Composable
private fun calculateBPColor(bp: Int): Color {
    val color = when (bp) {
        in 100..119 -> Color(
            ColorUtils.blendARGB(
                Color.Gray.toArgb(),
                Color.Green.toArgb(),
                1.0f - (120 - bp) / 20f
            )
        )
        in 120..149 -> Color(
            ColorUtils.blendARGB(
                Color.Green.toArgb(),
                Color.Yellow.toArgb(),
                1.0f - (150 - bp) / 30f
            )
        )
        in 150..169 -> Color(
            ColorUtils.blendARGB(
                Color.Yellow.toArgb(),
                Color.Red.toArgb(),
                1.0f - (170 - bp) / 20f
            )
        )
        in 170..310 -> Color.Red
        else -> Color.Gray
    }
    return color.copy(alpha = 0.4f)
}

@Composable
fun HeartRate(rate: Int) {
    Row {
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            tint = Color.Gray,
            contentDescription = stringResource(R.string.heart_rate_description)
        )
        Text(modifier = Modifier.padding(start = 10.dp), text = rate.toString(), color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BPTTheme {
        MainScreen()
    }
}