package com.vitekkor.compolybot.model

enum class Level(val levelName: String) {
    LEVEL0("ЗАСЕКРЕЧЕНО"),
    LEVEL1("октябрёнок"),
    LEVEL2("пионер"),
    LEVEL3("пролетарий"),
    LEVEL4("комсомолец"),
    LEVEL5("член профсоюза"),
    LEVEL6("посол"),
    LEVEL7("генсек"),
    LEVEL8("Гелич");

    companion object {

        private val levels = mapOf(
            Long.MIN_VALUE..-1L to LEVEL0,
            0L..20L to LEVEL1,
            21L..50L to LEVEL2,
            51L..100L to LEVEL3,
            101L..200L to LEVEL4,
            201L..500L to LEVEL5,
            501L..1000L to LEVEL6,
            1001L..5000L to LEVEL7,
            5001L..Long.MAX_VALUE to LEVEL8
        )

        fun getLevel(rep: Long): Level {
            for ((key, value) in levels) {
                if (rep in key) return value
            }
            throw IllegalArgumentException("Can't find proper level in level map")
        }
    }
}
