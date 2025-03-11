package com.tanh.scribblegame.util

enum class MatchStatus {
    WAITING,   //Khi số người chơi chưa đủ
    ONGOING,   //Khi game đang diễn ra
    ENDING     //Khi tất cả người chơi o trạng thái LEFT
}