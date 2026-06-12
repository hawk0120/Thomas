# Thomas

An experimental AI assistant exploring memory, reflection, and long-term interaction.

Thomas was an early attempt to build a personal AI system that could remember information, reflect on experiences, and interact with online communities. While later projects evolved significantly, Thomas established many of the ideas that eventually influenced Watney4, including memory management, tool-based actions, and long-term context.

Rather than acting as a traditional chatbot, Thomas was designed as an assistant capable of maintaining both short-term and long-term memory while interacting with external systems.

## Why Build Thomas?

Most AI systems focus on generating responses.

I was interested in a different question:

> What happens when an AI assistant remembers?

Thomas was built to explore:

* Long-term memory
* Reflection
* Persistent context
* Personal AI assistants
* Human-AI interaction over time

Many of the ideas explored here would later evolve into more sophisticated implementations.

## Research Relevance

Thomas represents an early exploration of questions that continue to influence my work:

* What should AI assistants remember?
* How should memories be organized?
* How should systems distinguish between temporary and permanent information?
* What role does reflection play in intelligent behaviour?
* How should AI systems interact with public online spaces?

Viewed through a Human-Centred AI lens, Thomas serves as an experiment in memory architecture and long-term assistant design.

## Architecture

Thomas is a Kotlin-based Spring Boot application built around four core concepts:

### Reflection

User input is processed through a reflection engine that generates responses and determines potential actions.

### Memory

The system maintains:

* Working memory
* Archival memory

allowing information to persist beyond a single interaction.

### Tools and Strategies

Actions are implemented using a strategy pattern, allowing the assistant to:

* Store memories
* Retrieve memories
* Interact with external services

### External Systems

Thomas integrates with Bluesky and can:

* Read updates
* Monitor activity
* Publish responses

## Features

### Reflection Engine

* LLM-driven response generation
* Context-aware interactions
* Reflection-oriented design

### Memory System

* Working memory
* Archival memory
* Memory retrieval
* Persistent storage

### Bluesky Integration

* Authentication
* Feed monitoring
* Post creation
* Background listener

### Command Line Interface

* Interactive prompt
* Local execution
* Reflection-driven conversations

## Technology Stack

* Kotlin
* Spring Boot
* PostgreSQL
* Gradle
* Bluesky APIs

## Lessons Learned

Thomas taught me several important lessons:

* Memory is more difficult than conversation.
* Long-term context requires careful design.
* Tool use dramatically expands assistant capabilities.
* User control matters.
* Persistence changes how assistants are perceived.

Many of these lessons directly influenced later systems such as Watney4.

## Historical Context

Thomas is preserved as an important milestone in the evolution of my personal AI assistant projects:

Thomas
→ Flash
→ Martha
→ Watney4

Each generation explored different approaches to memory, interaction, autonomy, and user control.

## Future Work

Development has largely moved to newer projects.

Thomas remains valuable as:

* A learning project
* A historical artifact
* An exploration of memory architectures
* A foundation for future Human-Centred AI research

## License

MIT
