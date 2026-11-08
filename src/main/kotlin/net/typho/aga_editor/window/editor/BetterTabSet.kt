package net.typho.aga_editor.window.editor

import javax.swing.JComponent
import javax.swing.text.TabSet
import javax.swing.text.TabStop
import kotlin.math.ceil

open class BetterTabSet(
    @JvmField
    val interval: Float
) : TabSet(null) {
    constructor(editor: JComponent, size: Float) : this(editor.getFontMetrics(editor.font).charWidth(' ') * size)

    override fun getTabCount(): Int {
        throw UnsupportedOperationException("Unlimited tabs :aga:")
    }

    override fun getTab(index: Int): TabStop {
        return TabStop(index * interval)
    }

    override fun getTabAfter(location: Float): TabStop? {
        return getTab(getTabIndexAfter(location))
    }

    override fun getTabIndex(tab: TabStop): Int {
        return (tab.position / interval).toInt()
    }

    override fun getTabIndexAfter(location: Float): Int {
        return ceil(location / interval).toInt()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is BetterTabSet) return false

        if (interval != o.interval) return false

        return true
    }

    override fun hashCode(): Int {
        return interval.hashCode()
    }

    override fun toString(): String {
        return "BetterTabSet(interval=$interval)"
    }
}