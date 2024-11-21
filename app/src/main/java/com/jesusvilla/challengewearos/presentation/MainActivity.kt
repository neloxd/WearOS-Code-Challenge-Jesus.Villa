/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.jesusvilla.challengewearos.presentation

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.jesusvilla.base.models.DataMessage
import com.jesusvilla.base.models.MainIntent
import com.jesusvilla.challengewearos.R
import com.jesusvilla.challengewearos.presentation.theme.ChallengewearosTheme
import com.jesusvilla.challengewearos.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)


        setContent {
            val mainViewModel = hiltViewModel<MainViewModel>()
            //WearApp("Android")
            WearApp(state = mainViewModel.state) {
                mainViewModel.onEvent(it)
            }
        }
    }
}

@Composable
fun WearApp(state: DataMessage, onEvent: (MainIntent) -> Unit) {
    MaterialTheme {
        val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()

        Scaffold(
            timeText = {
                TimeText()
            },
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(
                    scalingLazyListState = scalingLazyListState
                )
            }
        ) {
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingValues(4.dp)),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "",
                        maxLines = 1,
                        fontSize = TextUnit.Unspecified
                    )
                }
                Text(
                    text = "Haz tu pregunta al asistente",
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                )
                TextInput(onEvent)
                if(state.loading){
                    CircularProgressIndicator()
                } else {
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 28.dp,
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 40.dp
                    ),
                    verticalArrangement = Arrangement.Center,
                    state = scalingLazyListState
                ) {
                    items(count = state.list.size, key = {
                        it
                    }) { data ->
                        val value = state.list[data]
                        Chip(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            icon = {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.btn_star_big_on),
                                    contentDescription = "Star",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .wrapContentSize(align = Alignment.Center),
                                )
                            },
                            label = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colors.onPrimary,
                                    text = value.text//"Message ${index + 1}"
                                )
                            },
                            onClick = {
                            }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
fun TextInput(onEvent: (MainIntent) -> Unit) {
    val label = remember { mutableStateOf("PreviewMessage")}
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val text: CharSequence? = results.getCharSequence("enter_text")
                label.value = text as String
                onEvent.invoke(MainIntent.message(label.value))
            }
        }
    Column() {
        Spacer(modifier = Modifier
            .height(16.dp)
            .padding(8.dp)
        )
        /*Chip(
            label = { Text(label.value) },
            onClick = {

            }
        )*/
        Chip(
            label = { Text("Escribe tu mensaje") },
            onClick = {
                val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent();
                val remoteInputs: List<RemoteInput> = listOf(
                    RemoteInput.Builder("enter_text")
                        .setLabel("Elige la opción")
                        .wearableExtender {
                            setEmojisAllowed(false)
                            setInputActionType(EditorInfo.IME_ACTION_DONE)
                        }.build()
                )

                RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

                launcher.launch(intent)
            }
        )
    }
}


@Composable
fun WearApp(greetingName: String) {
    ChallengewearosTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            //TimeText()
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingValues(6.dp)),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ChatBot",
                        maxLines = 1,
                    )
                }
                Text(
                    text = "Haz tu pregunta al asistente",
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                )
            }

            //Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    //WearApp("Preview Android")
    //WearApp()
}