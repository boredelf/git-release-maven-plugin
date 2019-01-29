package br.com.poupex.plugins.maven.gitrelease


enum class Increment {
    MAJOR, MINOR, PATCH;

    companion object {
        fun from(value: String) = try {
            Increment.valueOf(value.toUpperCase())
        } catch (e: Exception) {
            throw RuntimeException("No valid increment value, must be: major, minor or patch.")
        }
    }
}