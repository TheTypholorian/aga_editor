package net.typho.aga_editor.window.editor.styling

import java.nio.file.Path
import javax.swing.text.StyledDocument

object KotlinCompilerStyling : Styling {
    /*
    private var module: KaModule
    @JvmField
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            val platform = JvmPlatforms.jvmPlatformByTargetVersion(JvmTarget.DEFAULT)
            this.platform = platform

            addModule(
                buildKtSourceModule {
                    moduleName = "sandbox"
                    this.platform = platform
                    addSourceRoot(Path("sandbox")) // TODO

                    addRegularDependency(buildKtLibraryModule {
                        libraryName = "kotlin-stdlib"
                        this.platform = platform
                        addBinaryRoot(MavenUtil.cachedLookup(
                            ArtifactCoords("org.jetbrains.kotlin", "kotlin-stdlib", "2.4.21"),
                            Path("sandbox", "maven_cache"),
                            listOf(MavenUtil.MAVEN_CENTRAL)
                        ).orThrow().artifact)
                    })
                }.also { module = it }
            )
        }
    }
    @JvmField
    val factory = KtPsiFactory(session.project, false)
     */

    override fun apply(file: Path, document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, Styling.style(), true)
        val text = document.getText(range.first, range.last - range.first + 1)

        /*
        file.contextModule = module
        analyze(file) {
            file.accept(object : KtTreeVisitorVoid() {
                var bracketDepth = 0
                var braceDepth = 0
                var parDepth = 0
                var angleDepth = 0

                override fun visitElement(element: PsiElement) {
                    val style = when (element.elementType) {
                        in KEYWORDS -> Styling.keywords
                        in SOFT_KEYWORDS -> Styling.keywords
                        in COMMENTS -> Styling.comments
                        INTEGER_LITERAL, Tree.Kind.FLOAT_LITERAL -> Styling.numbers
                        OPEN_QUOTE, CLOSING_QUOTE, in STRINGS -> Styling.strings
                        ANNOTATION_ENTRY -> Styling.annotations

                        IDENTIFIER -> when (element.parent?.elementType) {
                            FUN -> Styling.methodDef
                            PROPERTY -> Styling.property
                            else -> null
                        }
                        REFERENCE_EXPRESSION -> {
                            val symbol = (element as KtNameReferenceExpression).resolveSuccessfulSymbol()

                            if (symbol == null) {
                                Styling.error
                            } else {
                                when (symbol) {
                                    is KaKotlinPropertySymbol -> Styling.property
                                    else -> null
                                }
                            }
                        }

                        LBRACKET -> Styling.brackets.getOrNull((bracketDepth++) % Styling.brackets.size)
                        RBRACKET -> Styling.brackets.getOrNull((--bracketDepth) % Styling.brackets.size)

                        LBRACE -> Styling.brackets.getOrNull((braceDepth++) % Styling.brackets.size)
                        RBRACE -> Styling.brackets.getOrNull((--braceDepth) % Styling.brackets.size)

                        LPAR -> Styling.brackets.getOrNull((parDepth++) % Styling.brackets.size)
                        RPAR -> Styling.brackets.getOrNull((--parDepth) % Styling.brackets.size)

                        LT -> Styling.brackets.getOrNull((angleDepth++) % Styling.brackets.size)
                        GT -> Styling.brackets.getOrNull((--angleDepth) % Styling.brackets.size)

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
                    super.visitElement(element)
                }
            })
            file.accept(object : KtTreeVisitorVoid() {
                override fun visitElement(element: PsiElement) {
                    if (element.elementType == ERROR_ELEMENT) {
                        document.setCharacterAttributes(
                            element.textRange.startOffset + range.first,
                            element.textRange.length.coerceAtLeast(1) + range.first,
                            Styling.error,
                            true
                        )
                    }
                    super.visitElement(element)
                }
            })
        }
         */
    }
}