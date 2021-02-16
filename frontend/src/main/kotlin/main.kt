import kotlinx.browser.document
import kotlinx.html.ButtonType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.create

external val launcher: dynamic

fun main() {
        for (game in launcher.listGames()) {
            document.getElementById("wrapper")!!.appendChild(
                document.create.div {
                    button(type = ButtonType.submit) {
                        +"Start ${game.getDisplay()}"
                    }
                }
            )
        }
}