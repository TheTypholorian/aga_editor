package net.typho.aga_editor.window.editor.styling

import com.intellij.openapi.util.Disposer
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.CoreEnvironmentDeprecation
import org.jetbrains.kotlin.cli.create
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import java.awt.Color
import javax.swing.text.StyledDocument

open class KotlinCompilerStyling : Styling, AutoCloseable {
    @JvmField
    val disposable = Disposer.newDisposable()
    @JvmField
    val config = CompilerConfiguration.create()
    @OptIn(CoreEnvironmentDeprecation::class)
    @JvmField
    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        config,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
    @JvmField
    val factory = KtPsiFactory(environment.project, false)

    @JvmField
    val keywords = style(color = Color(0xCF8E6d))
    @JvmField
    val comments = style(color = Color(0x7A7E85))
    @JvmField
    val numbers = style(color = Color(0x2AACB8))
    @JvmField
    val strings = style(color = Color(0x6AAB73))
    @JvmField
    val annotations = style(color = Color(0xB3AE60))
    @JvmField
    val instanceField = style(color = Color(0xc77dbb))
    @JvmField
    val staticField = style(color = Color(0xc77dbb), italic = true)

    @OptIn(KtExperimentalApi::class)
    override fun apply(document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, style(), true)
        val file = factory.createFile(document.getText(range.first, range.last - range.first + 1))
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitKtElement(element: KtElement) {
                element.allChildren.forEach { element ->
                    val style = when (element.elementType) {
                        in KtTokens.KEYWORDS -> keywords
                        in KtTokens.SOFT_KEYWORDS -> keywords
                        in KtTokens.COMMENTS -> comments
                        KtTokens.INTEGER_LITERAL, KtTokens.FLOAT_LITERAL -> numbers
                        in KtTokens.STRINGS -> strings
                        KtStubElementTypes.ANNOTATION_ENTRY -> annotations
                        else -> null
                    }

                    style?.let {
                        document.setCharacterAttributes(
                            element.textRange.startOffset + range.first,
                            element.textRange.length + range.first,
                            style,
                            true
                        )
                    }
                }
                super.visitKtElement(element)
            }
        })
    }

    override fun close() {
        Disposer.dispose(disposable)
    }

    companion object Default : KotlinCompilerStyling()
}