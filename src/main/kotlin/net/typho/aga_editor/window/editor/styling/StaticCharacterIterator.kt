package net.typho.aga_editor.window.editor.styling

import java.text.AttributedCharacterIterator
import java.text.CharacterIterator

open class StaticCharacterIterator(
    @JvmField
    val text: CharSequence,
    @JvmField
    val attributes: Map<AttributedCharacterIterator.Attribute, Any> = mapOf()
) : AttributedCharacterIterator {
    @JvmField
    protected var pos = 0

    override fun getRunStart() = 0

    override fun getRunStart(attribute: AttributedCharacterIterator.Attribute) = 0

    override fun getRunStart(attributes: Set<AttributedCharacterIterator.Attribute>) = 0

    override fun getRunLimit() = text.length

    override fun getRunLimit(attribute: AttributedCharacterIterator.Attribute) = text.length

    override fun getRunLimit(attributes: Set<AttributedCharacterIterator.Attribute>) = text.length

    override fun getAttributes() = attributes

    override fun getAttribute(attribute: AttributedCharacterIterator.Attribute) = attributes[attribute]

    override fun getAllAttributeKeys() = attributes.keys

    override fun first(): Char {
        pos = 0
        return current()
    }

    override fun last(): Char {
        pos = if (text.isEmpty()) 0 else text.length - 1
        return current()
    }

    override fun current() = if (pos >= 0 && pos < text.length) text[pos] else CharacterIterator.DONE

    override fun next() = if (pos < text.length - 1) {
        pos++
        text[pos]
    } else {
        pos = text.length
        CharacterIterator.DONE
    }

    override fun previous() = if (pos > 0) {
        pos--
        text[pos]
    } else {
        pos = 0
        CharacterIterator.DONE
    }

    override fun setIndex(position: Int): Char {
        if (position !in text.indices) {
            throw IndexOutOfBoundsException(position)
        }

        pos = position
        return text[pos]
    }

    override fun getBeginIndex() = 0

    override fun getEndIndex() = text.length

    override fun getIndex() = pos

    override fun clone() = StaticCharacterIterator(text, attributes.toMap())
}