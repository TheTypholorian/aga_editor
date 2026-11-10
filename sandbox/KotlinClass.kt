class KotlinClass {
	@JvmField
	val field = 10

	fun test() {
		println(field)
		
		"this is a string"
	}

	companion object Companion {
		@JvmStatic
		fun main(args: Array<String>) {
			println(123) // this is a comment

			if (true) {
				println(args[0])
			}

			rainbowBracketsTest(((((((())))))))
		}
	}
}