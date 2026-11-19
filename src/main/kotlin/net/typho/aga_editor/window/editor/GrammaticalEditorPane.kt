package net.typho.aga_editor.window.editor

import net.typho.aga_editor.window.editor.styling.Styling
import java.awt.AWTEvent
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.font.TextAttribute
import javax.swing.*
import javax.swing.text.GapContent
import javax.swing.text.Segment

open class GrammaticalEditorPane(
    @JvmField
    val styling: Styling
) : JComponent(), Scrollable {
    @JvmField
    val content = GapContent()
    val text: Segment
        get() {
            val text = Segment()
            content.getChars(0, content.length() - 1, text)
            return text
        }

    init {
        enableEvents(AWTEvent.KEY_EVENT_MASK or AWTEvent.INPUT_METHOD_EVENT_MASK)
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val lineHeight = getFontMetrics(font).height
        var y = 5 + lineHeight

        val start = System.currentTimeMillis()
        styling.update(text, mapOf(
            TextAttribute.FAMILY to font.family,
            TextAttribute.SIZE to 12
        )).forEach { line ->
            g.drawString(line, 5, y)
            y += lineHeight
        }
        println("${System.currentTimeMillis() - start} ms")
    }

    override fun getPreferredScrollableViewportSize() = preferredSize

    override fun getScrollableUnitIncrement(
        visibleRect: Rectangle,
        orientation: Int,
        direction: Int
    ) = when (orientation) {
        SwingConstants.VERTICAL -> visibleRect.height / 10 // TODO
        SwingConstants.HORIZONTAL -> visibleRect.width / 10
        else -> throw AssertionError()
    }

    override fun getScrollableBlockIncrement(
        visibleRect: Rectangle,
        orientation: Int,
        direction: Int
    ) = when (orientation) {
        SwingConstants.VERTICAL -> visibleRect.height
        SwingConstants.HORIZONTAL -> visibleRect.width
        else -> throw AssertionError()
    }

    override fun getScrollableTracksViewportWidth(): Boolean {
        val parent = SwingUtilities.getUnwrappedParent(this)

        if (parent is JViewport) {
            return parent.width > preferredSize.width
        }

        return false
    }

    override fun getScrollableTracksViewportHeight(): Boolean {
        val parent = SwingUtilities.getUnwrappedParent(this)

        if (parent is JViewport) {
            return parent.height > preferredSize.height
        }

        return false
    }
}