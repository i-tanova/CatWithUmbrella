package com.tacocat.catwithumbrella.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.tacocat.catwithumbrella.Day
import com.tacocat.catwithumbrella.R
import com.tacocat.catwithumbrella.WeatherRepository
import com.tacocat.catwithumbrella.WeatherUiState
import com.tacocat.catwithumbrella.networking.Weather
import com.tacocat.catwithumbrella.networking.WeatherInfo
import kotlinx.coroutines.delay

@Composable
fun WeatherScreen(state: WeatherUiState) {
    var day by remember { mutableStateOf(Day.Today) }
    var restart by remember { mutableIntStateOf(0) }
    val forecast = state.forecast

    // Cycle Today -> Tomorrow (3s) -> Weekend (7s); tapping restarts the cycle.
    LaunchedEffect(forecast, restart) {
        day = Day.Today
        if (forecast != null) {
            delay(3000)
            day = Day.Tomorrow
            delay(4000)
            day = Day.Weekend
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { restart++ },
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.safeDrawingPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.do_i_need_umbrella), color = TextGrey, fontSize = 24.sp)

            AnimatedVisibility(visible = forecast != null) {
                AnimatedContent(targetState = day, label = "day") { shownDay ->
                    val answer = forecast?.let { WeatherRepository.willRain(it, shownDay) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(
                                when (shownDay) {
                                    Day.Today -> R.string.day_today
                                    Day.Tomorrow -> R.string.day_tomorrow
                                    Day.Weekend -> R.string.day_weekend
                                }
                            ),
                            color = TextGrey,
                            fontSize = 32.sp,
                        )
                        Text(
                            stringResource(
                                when (answer) {
                                    true -> R.string.answer_yes
                                    false -> R.string.answer_no
                                    null -> R.string.answer_not_sure
                                }
                            ),
                            color = TextGrey,
                            fontSize = 60.sp,
                        )
                    }
                }
            }

            AnimatedVisibility(visible = state.locationName != null) {
                Text(state.locationName.orEmpty(), color = TextGrey, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview
@Composable
private fun WeatherScreenPreview() {
    CatTheme {
        WeatherScreen(
            WeatherUiState(
                locationName = "Sofia, Bulgaria",
                forecast = listOf(WeatherInfo(dt = System.currentTimeMillis() / 1000, weather = listOf(Weather(main = "Rain")))),
            )
        )
    }
}
