package com.example.game

import android.graphics.Color



/**
 * 描述：
 * @author zhangzexin
 * @time   2019/8/27
 */
class GridSquare {
    var mType: Int = -1

    constructor(mType: Int) {
        this.mType = mType
    }

    public fun getColor() :Int {
      when (mType) {
            GameType.GRID//空格子
            -> return Color.WHITE
            GameType.FOOD//食物
            -> return Color.BLUE
            GameType.SNAKE//蛇
            -> return Color.parseColor("#FF4081")
        }
        return Color.WHITE
    }

    public fun setType(type: Int) {
        mType = type
    }

}