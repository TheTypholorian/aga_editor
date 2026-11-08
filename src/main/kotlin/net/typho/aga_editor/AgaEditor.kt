package net.typho.aga_editor

import com.formdev.flatlaf.FlatDarkLaf
import java.awt.Dimension
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.UIManager

object AgaEditor {
    init {
        FlatDarkLaf.setup()
    }

    @JvmField
    val window = JFrame()

    fun readImage(path: String) = ImageIO.read(AgaEditor::class.java.classLoader.getResourceAsStream(path))

    @JvmStatic
    fun main(args: Array<String>) {
        UIManager.put("TitlePane.iconSize", Dimension(24, 24))
        UIManager.put("TitlePane.centerTitle", true)

        window.title = "Aga Editor"
        window.iconImages = listOf(150, 100, 75, 50, 30).map { res -> readImage("icons/aga_${res}x${res}.png") }
        window.minimumSize = Dimension(400, 300)
        window.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        window.setLocationRelativeTo(null)

        window.isVisible = true
        window.toFront()
    }
}