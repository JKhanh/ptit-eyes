package com.ptit.theeyes.utils

import kotlinx.coroutines.CoroutineDispatcher

//For deciding where will the threads\coroutines be running
class AppDispatchers(val main: CoroutineDispatcher, val io: CoroutineDispatcher, val default: CoroutineDispatcher)