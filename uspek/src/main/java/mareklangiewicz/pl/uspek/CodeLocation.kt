package mareklangiewicz.pl.uspek

data class CodeLocation(val fileName: String, val lineNumber: Int) {
    override fun toString() = ".($fileName:$lineNumber)"
}