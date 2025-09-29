package hawk0120.tools

class ToolDispatcher {

    private val registry: Map<Tool, ToolStrategy<*, *>> = mapOf(
        Tool.SAVE_MEMORY to SaveMemoryStrategy(),
        Tool.DELETE_MEMORY to DeleteMemoryStrategy(),
        Tool.GET_MEMORY to GetMemoryStrategy(),
        Tool.POST_OUTPUT to PostOutputStrategy(),
        )


    fun <Input, Output> dispatch(tool: Tool, args: Input): Output? {
        val strategy = registry[tool] as? ToolStrategy<Input, Output>
        return strategy?.execute(args)
    }
}

