/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.kotlincoroutines.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.android.kotlincoroutines.ui.theme.DongnamCoroutinesLabTheme
import kotlinx.coroutines.launch

/**
 * Show layout.activity_main and setup data binding.
 */
class MainActivity : ComponentActivity() {

    /**
     * Inflate layout.activity_main and setup data binding.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_main)

//        val rootLayout: ConstraintLayout = findViewById(R.id.rootLayout)
//        val title: TextView = findViewById(R.id.title)
//        val taps: TextView = findViewById(R.id.taps)
//        val spinner: ProgressBar = findViewById(R.id.spinner)
//
        // Get MainViewModel by passing a database to the factory
        val database = getDatabase(this)
        val repository = TitleRepository(getNetworkService(), database.titleDao)
        val viewModel = ViewModelProvider(this, MainViewModel.FACTORY(repository))
                .get(MainViewModel::class.java)
//
//        // When rootLayout is clicked call onMainViewClicked in ViewModel
//        rootLayout.setOnClickListener {
//            viewModel.onMainViewClicked()
//        }

        setContent {
            DongnamCoroutinesLabTheme {
                // update the title when the [MainViewModel.title] changes
                var title by remember { mutableStateOf("get Started") }
                var taps by remember { mutableStateOf("0 Taps") }
                var spinner by remember { mutableStateOf(true) }
                var snackbar by remember { mutableStateOf("") }


                viewModel.title.observe(this) { value ->
                    value?.let {
                        title = it
                    }
                }

                viewModel.taps.observe(this) { value ->
                    taps = value
                }

                // show the spinner when [MainViewModel.spinner] is true
                viewModel.spinner.observe(this) { value ->
//                    Log.d("KOTLINCLASS", "value: $value")
                    value.let { show ->
                        spinner = if (show) true else false
                    }
                }

                viewModel.snackbar.observe(this) { text ->
                    Log.d("KOTLINCLASS", "snackbar text: $text")
                    text.let {
                        snackbar = it
                    }
                }

                val snackBarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Row(Modifier
                            .padding(16.dp)
                            .background(Color.Green)) {
                            Text(
                                text = "Kotlin Coroutines",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                    },
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                ) { paddingValues ->
                    val coroutineScope = rememberCoroutineScope()
                    // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
                    LaunchedEffect(snackbar) {
                        if (snackbar.isNotEmpty()) coroutineScope.launch {
                            Log.d("KOTLINCLASS", "coroutineScope.launch")
                            snackBarHostState.showSnackbar(
                                message = "Hello SnackBar",
                                duration = SnackbarDuration.Short,
                                actionLabel = "label",
                                withDismissAction = true,
                            )
                            viewModel.onSnackbarShown()
                        }
                    }
                    Column(modifier = Modifier
                        .padding(paddingValues)
//                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color.Red)
                        .clickable {
                            viewModel.onMainViewClicked()
                        },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(snackbar)
                        Box(Modifier
                            .weight(1f)
                            .background(Color.White)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                            )
//                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Hello World! ${taps}",
                                modifier = Modifier.padding(64.dp),
                                style = MaterialTheme.typography.titleLarge,
                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            val coroutineScope = rememberCoroutineScope()
                            Button(
                                onClick = {
                                    viewModel.onSnackbarShown()
                                },
                                modifier = Modifier.padding(128.dp),
                            ) {
                                Text("Button")
                            }
                            if (spinner) CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.BottomEnd),
//                                    .navigationBarsPadding()
//                                    .padding(bottom = 8.dp)
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }

                }
            }
        }


    }
}
