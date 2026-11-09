package net.typho.aga_editor

import com.formdev.flatlaf.FlatDarkLaf
import net.typho.aga_editor.window.editor.MultiFileEditorPane
import net.typho.aga_editor.window.editor.styling.KotlinCompilerStyling
import java.awt.Dimension
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.UIManager
import kotlin.io.path.Path

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
        UIManager.put("defaultFont", UIManager.getFont("monospaced.font"))

        window.title = "Aga Editor"
        window.iconImages = listOf(150, 100, 75, 50, 30).map { res -> readImage("icons/aga_${res}x${res}.png") }
        window.minimumSize = Dimension(1280, 800)
        window.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        window.setLocationRelativeTo(null)

        val sandbox = Path("sandbox")
        val editor = MultiFileEditorPane()
        //editor.open(sandbox.resolve("test.txt"))
        editor.open(sandbox.resolve("KotlinClass.kt"), KotlinCompilerStyling)
        window.contentPane.add(editor)

        window.isVisible = true
        window.toFront()
    }
}