package net.typho.aga_editor

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.util.SystemFileChooser
import net.typho.aga_editor.window.editor.MultiFileEditorPane
import net.typho.aga_editor.window.editor.styling.KotlinCompilerStyling
import net.typho.aga_editor.window.file_tree.FileTree
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.imageio.ImageIO
import javax.swing.*
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

        window.contentPane.layout = BorderLayout()

        val editor = MultiFileEditorPane()
        val files = FileTree(sandbox, editor::open)

        window.contentPane.add(files, BorderLayout.WEST)
        window.contentPane.add(editor, BorderLayout.CENTER)

        val menuBar = JMenuBar()
        menuBar.add(JMenu("File").also { menu ->
            menu.add(JMenuItem(object : AbstractAction("Open") {
                override fun actionPerformed(e: ActionEvent) {
                    val chooser = SystemFileChooser()
                    val result = chooser.showOpenDialog(menu)

                    if (result == SystemFileChooser.APPROVE_OPTION) {
                        editor.open(chooser.selectedFile.toPath())
                    }
                }
            }))
        })
        window.jMenuBar = menuBar

        window.isVisible = true
        window.toFront()
    }
}