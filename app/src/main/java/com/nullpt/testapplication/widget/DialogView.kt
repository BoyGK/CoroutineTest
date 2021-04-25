package com.nullpt.testapplication.widget

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import kotlin.math.max

class DialogView(context: Context, adapter: FlexListView.Adapter) : CustomLayout(context) {

    val list1 = FlexListView(context).apply {
        shownLine = 3
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        this.adapter = adapter
    }

    val list2 = FlexListView(context).apply {
        shownLine = 4
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        this.adapter = adapter
    }

    val list3 = FlexListView(context).apply {
        shownLine = 5
        layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        this.adapter = adapter
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(list1)
        addView(list2)
        addView(list3)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        list1.layoutParams.width = widthSize / 2
        list2.layoutParams.width = widthSize / 2
        list1.autoMeasure()
        list2.autoMeasure()
        list3.autoMeasure()

        setMeasuredDimension(
                measuredWidth,
                max(list1.measuredHeight, list2.measuredHeight) + list3.measuredHeight
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        list1.layout(x = 0, y = 0)
        list2.layout(x = list1.right, y = 0)
        list3.layout(x = 0, y = max(list1.bottom, list2.bottom))

    }
}