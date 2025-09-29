package hawk0120.tools

interface ToolStrategy<I, O> {
    fun execute(input: I): O
}