import org.jsoup.Jsoup
import sys.process._
import java.net.URL
import java.io.File
import scala.language.postfixOps


object Main extends App {
  val doc = Jsoup.connect("https://httpstatusdogs.com/").get()
  doc.select("a[class=thumbnail]").forEach(_handle_elem)

  def _handle_elem(elem: org.jsoup.nodes.Element): Unit = {
    val img_elem = elem.select("img").first()
    val name = img_elem.attr("alt")
    val url = "https://httpstatusdogs.com/" + img_elem.attr("src")
    println(f"downloading ${name}...")
    downloadFile(url, s"${name}.jpg")
  }

  // found an stackoverflow 😬
  def downloadFile(url: String, filename: String): Unit = {
    new URL(url) #> new File(filename) !!
  }
}
