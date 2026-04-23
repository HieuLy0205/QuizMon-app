package com.example.quizmon.data.model

import java.time.temporal.TemporalAmount

data class UserStats(
    //số tiền user trong shop
    var conins: Int = 0,
    var exp: Int = 0,
    var level: Int = 1,
    var petExp: Int = 0,
    var petLevel: Int = 1
)


