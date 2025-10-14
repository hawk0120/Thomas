package hawk0120.tools

class ToolDispatcher(private val saveMemoryStrategy: SaveMemoryStrategy,
                    private val deleteMemoryStrategy: DeleteMemoryStrategy,
                    private val getMemoryStrategy: GetMemoryStrategy,
                    private val postOutputStrategy: PostOutputStrategy) {

    private val registry: Map<Tool, ToolStrategy<*, *>> = mapOf(
        Tool.SAVE_MEMORY to saveMemoryStrategy,
        Tool.DELETE_MEMORY to deleteMemoryStrategy,
        Tool.GET_MEMORY to getMemoryStrategy,
        Tool.POST_OUTPUT to postOutputStrategy,
    )

    fun <Input, Output> dispatch(tool: Tool, args: Input): Output? {
        val strategy = registry[tool] as? ToolStrategy<Input, Output>
        return strategy?.execute(args)
    }
}
