package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.JavaCompilerPrototype2
import java.nio.file.Path
import javax.swing.text.StyledDocument

object JavaCompilerStyling : Styling {
    override fun apply(file: Path, document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, Styling.style(), true)
        val text = document.getText(range.first, range.last - range.first + 1)
        val tokens = JavaCompilerPrototype2.process(text)

        for (token in tokens) {
            val style = when (token) {
                is JavaCompilerPrototype2.Comment -> Styling.comments
                is JavaCompilerPrototype2.Identifier -> Styling.methodDef // TODO
                is JavaCompilerPrototype2.KeywordToken -> Styling.keywords
                is JavaCompilerPrototype2.Number -> Styling.numbers
                is JavaCompilerPrototype2.Punctuation -> Styling.brackets[0]
                is JavaCompilerPrototype2.Text -> Styling.strings
            }

            document.setCharacterAttributes(
                token.range.first + range.first,
                (token.range.last - token.range.first) + range.first,
                style,
                true
            )
        }
    }
}