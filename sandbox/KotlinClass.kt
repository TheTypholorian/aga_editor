class KotlinClass {
	@JvmField
	val field = 10

	fun test() {
		println(field)
		
		"this is a string"
		
		if (field) {
			println()
		}
	}

	companion object Companion {
		@JvmStatic
		val STATIC_FIELD = 200
	
		@JvmStatic
		fun main(args: Array<String>) {
			println(123) // this is a comment

			if (true) {
				println(args[0])
			}

			rainbowBracketsTest(((((((((())))))))))
		}
	}
}