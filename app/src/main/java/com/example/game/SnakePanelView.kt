package com.example.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


/**
 * 描述：
 * @author zhangzexin
 * @time   2019/8/26
 */
class SnakePanelView : View {

    private var mGridSize: Int = 20 //游戏格子数

    private val mGridSquare = ArrayList<ArrayList<GridSquare>>()
    private val mSnakePositions = ArrayList<GridPosition>()

    private var mSnakeHeader: GridPosition? = null//蛇头部位置
    private var mFoodPosition: GridPosition? = null//蛇尾部位置

    private var mIsEndGame = false

    private val mRectSize = dp2px(context, 15f)

    private var mGridPaint: Paint = Paint()
    private var mStrokePaint: Paint = Paint()

    private var mStartX: Int = 0
    private var mStartY: Int = 0

    private var mSpeed: Long = 8 //游戏速度

    private var mSnakeDirection = GameType.RIGHT //默认游戏开始方向

    private var mSnakeLength: Int = 2 //贪吃蛇默认长度

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        var squares: ArrayList<GridSquare>

        for (index in 0 until mGridSize) {
            squares = ArrayList()
            for (j in 0 until mGridSize) {
                squares.add(GridSquare(GameType.GRID))
            }
            mGridSquare.add(squares)
        }

        mSnakeHeader = GridPosition(10, 10)
        mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
        mFoodPosition = GridPosition(0, 0)

        mIsEndGame = true

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mStartX = w / 2 - mGridSize * mRectSize / 2
        mStartY = dp2px(context, 40f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = mStartY * 2 + mGridSize * mRectSize
        setMeasuredDimension(getDefaultSize(suggestedMinimumHeight, widthMeasureSpec), height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE)
        with(mGridPaint) {
            reset()
            setAntiAlias(true)
            style = Paint.Style.FILL
        }

        with(mStrokePaint) {
            reset()
            color = Color.BLACK
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        for (i in 0 until mGridSize) {
            for (j in 0 until mGridSize) {
                val left: Float = (mStartX + i * mRectSize).toFloat()
                val top: Float = (mStartY + j * mRectSize).toFloat()
                val right: Float = left + mRectSize
                val bottom: Float = top + mRectSize
                canvas?.drawRect(left, top, right, bottom, mStrokePaint)
                mGridPaint.color = mGridSquare[i][j].getColor()
                canvas?.drawRect(left, top, right, bottom, mGridPaint)
            }
        }
    }


    private fun refreshFood(foodposition: GridPosition) {
        mGridSquare.get(foodposition.x).get(foodposition.y).setType(GameType.FOOD)
    }

    public fun setSpeed(speed: Long) {
        mSpeed = speed
    }

    fun setGridSize(gridSize: Int) {
        mGridSize = gridSize
    }

    fun setSnakeDirection(direction: Int) {
        if (mSnakeDirection == GameType.RIGHT && direction == GameType.LEFT) return
        if (mSnakeDirection == GameType.LEFT && direction == GameType.RIGHT) return
        if (mSnakeDirection == GameType.TOP && direction == GameType.BOTTOM) return
        if (mSnakeDirection == GameType.BOTTOM && direction == GameType.TOP) return

        mSnakeDirection = direction
    }


    /**
     * dp转px
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal,
            context.resources.displayMetrics
        ).toInt()
    }

    inner class GameMainThread : Thread() {
        override fun run() {
            while (!mIsEndGame) {
                moveSnake(mSnakeDirection)
                checkCollision()
                refreshGridSquere()
                handleSnakeTail()
                postInvalidate() //重绘界面
                handlerSpeed()
            }
        }

        // 运行速度
        private fun handlerSpeed() {
            sleep(1000 / 8)
        }
    }

    //
    private fun handleSnakeTail() {
        var snakeLength = mSnakeLength
        for (i in mSnakePositions.size - 1 downTo 0) {
            if (snakeLength > 0) {
                snakeLength--
            } else {
                val position = mSnakePositions.get(i)
                mGridSquare.get(position.x).get(position.y).setType(GameType.GRID)
                mSnakePositions.removeAt(i)
            }
        }
    }


    //刷新格子
    private fun refreshGridSquere() {
        for (mSnakePosition in mSnakePositions) {
            mGridSquare.get(mSnakePosition.x).get(mSnakePosition.y).setType(GameType.SNAKE)
        }
    }

    //检查碰撞
    private fun checkCollision() {
        val headerPosition = mSnakePositions.get(mSnakePositions.size - 1)
        var i: Int = 0
        while (i < (mSnakePositions.size - 1)) {
            val position = mSnakePositions.get(i)
            if (position.x == headerPosition.x && position.y == headerPosition.y) {
                mIsEndGame = true
            }
            i++
        }

        if (headerPosition.x == mFoodPosition!!.x && headerPosition.y == mFoodPosition!!.y) {
            mSnakeLength++
            generateFood()
        }
    }

    //生成食物
    private fun generateFood() {
        val random = java.util.Random()
        var foodX = random.nextInt(mGridSize - 1)
        var foodY = random.nextInt(mGridSize - 1)

        var i: Int = 0
        while (i < mSnakePositions.size) {
            if (foodX == mSnakePositions.get(i).x && foodY == mSnakePositions.get(i).y) {
                foodX = random.nextInt(mGridSize - 1)
                foodY = random.nextInt(mGridSize - 1)
                i = 0
            }
            i++
        }

        mFoodPosition!!.x = foodX
        mFoodPosition!!.y = foodY

        refreshFood(mFoodPosition!!)

    }


    //移动蛇的位置
    private fun moveSnake(mSnakeDirection: Int) {
        when (mSnakeDirection) {
            GameType.LEFT -> {
                if (mSnakeHeader!!.x - 1 < 0) {
                    mSnakeHeader!!.x = mGridSize - 1
                } else {
                    mSnakeHeader!!.x = mSnakeHeader!!.x - 1
                }
                mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
            }
            GameType.TOP -> {
                if (mSnakeHeader!!.y - 1 < 0) {
                    mSnakeHeader!!.y = mGridSize - 1
                } else {
                    mSnakeHeader!!.y = mSnakeHeader!!.y - 1
                }
                mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
            }
            GameType.RIGHT -> {
                if (mSnakeHeader!!.x + 1 >= mGridSize) {
                    mSnakeHeader!!.x = 0
                } else {
                    mSnakeHeader!!.x = mSnakeHeader!!.x + 1
                }
                mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
            }
            GameType.BOTTOM -> {
                if (mSnakeHeader!!.y + 1 >= mGridSize) {
                    mSnakeHeader!!.y = 0
                } else {
                    mSnakeHeader!!.y = mSnakeHeader!!.y + 1
                }
                mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
            }
        }
    }

    fun reStartGame() {
        if (!mIsEndGame) {
            return
        }
        for (squares in mGridSquare) {
            for (square in squares) {
                square.setType(GameType.GRID)
            }
        }
        if (mSnakeHeader != null) {
            mSnakeHeader!!.x = 10
            mSnakeHeader!!.y = 10
        } else {
            mSnakeHeader = GridPosition(10, 10)
        }
        mSnakePositions.clear()
        mSnakePositions.add(GridPosition(mSnakeHeader!!.x, mSnakeHeader!!.y))
        mSnakeLength = 2
        mSnakeDirection = GameType.RIGHT
        mSpeed = 32


        if (mFoodPosition != null) {
            mFoodPosition!!.x = (0)
            mFoodPosition!!.y = (0)
        } else {
            mFoodPosition = GridPosition(0, 0)
        }
        refreshFood(mFoodPosition!!)
        mIsEndGame = false
        val thread = GameMainThread()
        thread.start()

    }
}