package com.nullpt.testapplication.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener

/**
 * @author BGQ
 * 带展开收起的FlexLayout
 */
class FlexListView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {

        /**
         * int参数的默认值
         */
        private const val DEFAULT_INT = 0

        /**
         * 动画默认执行时间
         */
        private const val ANIMATION_DURATION = 500L

        /**
         * 默认展开行数
         */
        private const val DEFAULT_LINE_NUMBER = 2

    }

    /**
     * 子view位置记录
     */
    private val mItemRect = mutableMapOf<View, Rect>()

    /**
     * 子view
     */
    private val mItemView = mutableListOf<View>()

    /**
     * 已经显示的item数量
     */
    private var mShownCount = DEFAULT_INT

    /**
     * 初始展开行数
     */
    private var mShownLine = DEFAULT_LINE_NUMBER

    /**
     * 收起时显示的数量
     */
    private var mHiddenCount = DEFAULT_INT

    /**
     * 布局刷新辅助
     */
    private var mAdapterHelper = AdapterHelper()

    /**
     * 状态变更中
     */
    private var mSwitching = false

    /**
     * 记录当前的展开状态
     */
    private var mShownState = ShownState.HIDDEN
        set(value) {
            field = value
            mShownLine = if (value == ShownState.HIDDEN) {
                shownLine
            } else {
                Int.MAX_VALUE
            }
        }

    /**
     * 展示行数
     */
    var shownLine = mShownLine
        set(value) {
            field = value
            mShownLine = value
            requestLayout()
        }

    /**
     * 对接RecyclerView的适配器
     */
    var adapter: Adapter? = null
        set(value) {
            value?.mAdapterHelper = mAdapterHelper
            field = value
            requestLayout()
        }

    var measureCount = 0

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mAdapter = adapter
        if (mAdapter == null || mAdapter.getItemCount() == 0) {
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        measureCount++
        Log.i("Test", "id :$id ,onMeasure: $measureCount")

        //初始化
        removeAllViews()
        mItemView.clear()
        mShownCount = 0
        if (mShownState == ShownState.HIDDEN) {
            mHiddenCount = 0
        }

        val controlView = mAdapter.onCreateControl(LayoutInflater.from(context))
        controlView.setOnClickListener(this)
        addView(controlView)
        mAdapter.onBindControl(controlView)
        measureChild(controlView, widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize: Int

        val itemCount = mAdapter.getItemCount()
        var lineHeight = 0
        var widthUsed = paddingStart
        var heightUsed = paddingTop
        var line = 1

        for (index in 0 until itemCount) {
            val child = mAdapter.onCreateView(LayoutInflater.from(context))
            addView(child, index)
            setOnClickListener(child, index)
            mAdapter.onBindView(child, index)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            //所有itemView的高度限制为相同
            if (lineHeight == 0) {
                lineHeight = child.measuredHeight
            }
            val childWidth = child.measuredWidth
            val offsetControl = if (line == mShownLine) controlView.measuredWidth else 0
            if (widthUsed + childWidth > widthSize - paddingStart - paddingEnd - offsetControl) {
                //记录换行数
                line++
                //可显示的最后一行,添加控制视图
                if (line > mShownLine) {
                    if (mItemRect[controlView] != null) {
                        mItemRect[controlView]!!.set(
                                widthUsed, heightUsed,
                                widthUsed + controlView.measuredWidth, heightUsed + lineHeight
                        )
                    } else {
                        //不会无线创建，忽略@SuppressLint("DrawAllocation")
                        mItemRect[controlView] = Rect(
                                widthUsed, heightUsed,
                                widthUsed + controlView.measuredWidth, heightUsed + lineHeight
                        )
                    }
                    mItemView.add(controlView)
                    break
                }

                //换行
                widthUsed = paddingStart
                heightUsed += lineHeight

            }

            //正常添加
            if (mItemRect[child] != null) {
                mItemRect[child]!!.set(
                        widthUsed, heightUsed, widthUsed + childWidth, heightUsed + lineHeight
                )
            } else {
                //不会无线创建，忽略@SuppressLint("DrawAllocation")
                mItemRect[child] = Rect(
                        widthUsed, heightUsed, widthUsed + childWidth, heightUsed + lineHeight
                )
            }
            widthUsed += childWidth
            mShownCount++
            mItemView.add(child)
        }

        //全部显示了，追加控制视图
        if (mShownCount == itemCount) {
            if (shownLine >= line) {
                //全部显示内容在规定范围内，不添加控制按钮
                removeView(controlView)
            } else {
                if (widthUsed + controlView.measuredWidth > widthSize - paddingStart - paddingEnd) {
                    //换行
                    widthUsed = paddingStart
                    heightUsed += lineHeight
                }
                if (mItemRect[controlView] != null) {
                    mItemRect[controlView]!!.set(
                            widthUsed, heightUsed,
                            widthUsed + controlView.measuredWidth, heightUsed + heightUsed
                    )
                } else {
                    //不会无线创建，忽略@SuppressLint("DrawAllocation")
                    mItemRect[controlView] = Rect(
                            widthUsed, heightUsed,
                            widthUsed + controlView.measuredWidth, heightUsed + heightUsed
                    )
                }
                mItemView.add(controlView)
            }
        }

        //记录收起时显示的数量
        if (mShownState == ShownState.HIDDEN) {
            mHiddenCount = mItemView.size - 1
        }

        //计算总高度
        heightSize = heightUsed + lineHeight + paddingTop + paddingBottom

        setMeasuredDimension(widthSize, heightSize)
    }

    private fun setOnClickListener(child: View, index: Int) {
        child.setOnClickListener {
            if (adapter != null) {
                adapter!!.mOnItemClickListener?.onItemClick(adapter!!, child, index)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        println("$this onLayout")
        for ((position, child) in mItemView.withIndex()) {
            val rect = mItemRect[child]!!
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
            if (mSwitching && mShownState == ShownState.SHOWN && position >= mHiddenCount) {
                ObjectAnimator.ofFloat(child, "alpha", 0f, 1f).apply {
                    duration = ANIMATION_DURATION
                    start()
                }
            }
        }
        mSwitching = false
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun requestLayout() {
        super.requestLayout()
    }

    override fun onClick(v: View) {
        //切换状态
        switchState(if (getShownState() == ShownState.SHOWN) ShownState.HIDDEN else ShownState.SHOWN)
    }

    /**
     * 切换展开状态
     */
    fun switchState(state: ShownState) {
        mShownState = state
        mSwitching = true
        requestLayout()
    }

    /**
     * 获取当前的展开状态
     */
    fun getShownState() = mShownState

    /**
     * 获取当前的展开数量
     */
    fun getShownCount() = mShownCount

    /**
     * 适配器类
     */
    abstract class Adapter {

        /**
         * item事件
         */
        var mOnItemClickListener: OnItemClickListener? = null

        /**
         * 内部使用
         */
        lateinit var mAdapterHelper: AdapterHelper

        /**
         * 创建控制视图
         */
        abstract fun onCreateControl(inflater: LayoutInflater): View

        /**
         * 创建普通视图
         */
        abstract fun onCreateView(inflater: LayoutInflater): View

        /**
         * 绑定控制视图
         */
        abstract fun onBindControl(controlView: View)

        /**
         * 绑定普通视图
         */
        abstract fun onBindView(itemView: View, position: Int)

        /**
         * 设置视图数据数量
         */
        abstract fun getItemCount(): Int

        /**
         * 列表刷新
         */
        fun notifyDataSizeChange() {
            mAdapterHelper.notifyDataSizeChange()
        }

        /**
         * 列表条目刷新
         */
        fun notifyItemChange(position: Int) {
            mAdapterHelper.notifyItemChange(position)
        }

        /**
         * 列表范围刷新
         */
        fun notifyItemRangeChange(position: Int, size: Int) {
            mAdapterHelper.notifyItemRangeChange(position, size)
        }

        /**
         * 获取当前的展开状态
         */
        fun getShownState() = mAdapterHelper.currentShownState

    }

    /**
     * 刷新辅助类
     */
    inner class AdapterHelper {
        /**
         * 列表刷新
         */
        fun notifyDataSizeChange() {
            requestLayout()
        }

        /**
         * 列表条目刷新
         */
        fun notifyItemChange(position: Int) {
            if (position >= 0 && position < mItemView.size) {
                ObjectAnimator.ofFloat(mItemView[position], "alpha", 1f, 0.3f).apply {
                    duration = ANIMATION_DURATION / 2
                    start()
                }.addListener(onEnd = {
                    adapter?.onBindView(mItemView[position], position)
                    ObjectAnimator.ofFloat(mItemView[position], "alpha", 0.3f, 1f).apply {
                        duration = ANIMATION_DURATION / 2
                        start()
                    }
                })
            } else {
                notifyDataSizeChange()
            }
        }

        /**
         * 列表范围刷新
         */
        fun notifyItemRangeChange(position: Int, size: Int) {
            notifyDataSizeChange()
            // TODO: 2020/12/30 暂时使用notifyDataSizeChange刷新，后续实现notifyDataSizeChange完整功能
            /*
            for (i in min(position, mItemView.size) until min(position + size, mItemView.size)) {
                notifyItemChange(i)
            }*/
        }

        /**
         * 获取当前的展开状态
         */
        val currentShownState
            get() = getShownState()
    }

    interface OnItemClickListener {
        fun onItemClick(adapter: Adapter, view: View, position: Int)
    }

    enum class ShownState {
        /**
         * 隐藏状态
         */
        HIDDEN,

        /**
         * 展开状态
         */
        SHOWN
    }
}