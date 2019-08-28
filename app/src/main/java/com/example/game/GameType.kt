package com.example.game

/**
 * 描述：
 * @author zhangzexin
 * @time   2019/8/27
 */
//public class GameType {
//    companion object {
//        public val GRID = 0
//        public val FOOD = 1
//        public val SNAKE = 2
//
//        var LEFT = 1
//        var TOP = 2
//        var RIGHT = 3
//        var BOTTOM = 4
//    }

    internal interface GameType {
        companion object {
            val GRID = 0
            public val FOOD = 1
            public val SNAKE = 2

            var LEFT = 1
            var TOP = 2
            var RIGHT = 3
            var BOTTOM = 4
        }
    }

//}