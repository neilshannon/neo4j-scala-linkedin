import com.neilshannon.Neo4jLinkedIn
import dispatch.classic.{ConfiguredHttpClient, Http}
import org.apache.http.{HttpResponse, HttpRequest}
import org.apache.http.protocol.HttpContext
import org.specs2.mutable.Specification
import unfiltered.specs2.jetty.Served

class Neo4jLinkedInSpec extends Specification with Served {

  def setup = { _.filter(new Neo4jLinkedIn) }

  val http = new Http {
    override def make_client = {
      val client = new ConfiguredHttpClient(new Http.CurrentCredentials(None))

      client.setRedirectStrategy(new org.apache.http.impl.client.DefaultRedirectStrategy {
        override def isRedirected(req: HttpRequest, res: HttpResponse, ctx: HttpContext) = false
      })

      client
    }
  }

  "the neo4j-linked in app" should {
    "redirect a user to the linkedin api page" in {
      val redirectUrl: String = http.x(host / "linkedin_auth" >:> { _("Location").head })
      redirectUrl must startWith("https://www.linkedin.com/uas/oauth2/authorization")
    }
  }
}