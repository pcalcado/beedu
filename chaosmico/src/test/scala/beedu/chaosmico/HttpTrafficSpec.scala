package beedu.chaosmico

import java.io.{ByteArrayOutputStream, InputStream}

import com.twitter.finagle.httpx.{Method, Request, Response, Status}
import com.twitter.finagle.{Httpx, Service}
import com.twitter.util.{Await, Future}
import org.specs2.mutable.After

class HttpTrafficSpec extends org.specs2.mutable.Specification {
  sequential

  trait Context extends After {
    val serverHost = "localhost"
    val serverPort = 8080
    val proxyHost = "localhost"
    val proxyPort = serverPort + 10

    val httpServer = {
      val service = new Service[Request, Response] {
        def apply(req: Request): Future[Response] = {
          if (req.path == "/wdyt") {
            val response: Response = Response(req.version, Status.Ok)
            response.contentString = s"I think you said [${req.getContentString()}] with [${req.headerMap.iterator.mkString(",")}}]"
            Future.value(response)
          } else if (req.path == "/echo") {
            val response: Response = Response(req.version, Status.Ok)
            response.content = req.content
            Future.value(response)
          } else {
            Future.value(Response(req.version, Status.NotFound))
          }
        }
      }
      Httpx.serve(s":${serverPort}", service)
    }

    val chaosMicoProxy = new ChaosMicoProxy(serverHost, serverPort, proxyHost, proxyPort)

    override def after: Any = {
      httpServer.close()
      chaosMicoProxy.stop()
    }
  }

  "HTTP traffic" >> {
    "is transparently proxied from proxy to client" in new Context {
      chaosMicoProxy.run()
      val payload = " ❤ ☀ ☆ ☂ ☻ ♞ ☯ ☭ ☢ € →"
      val expectedResponse = "I think you said [ ❤ ☀ ☆ ☂ ☻ ♞ ☯ ☭ ☢ € →] with [(Content-Length,44)}]"
      val client = Httpx.newService(s"${proxyHost}:${proxyPort}")

      val request = Request(Method.Get, "/wdyt")
      request.contentString = payload
      val response = Await.result(client(request))

      response.getContentString() ==== expectedResponse
    }

    "returns binary blobs just fine" in new Context {
      chaosMicoProxy.run()

      val jpgPic: InputStream = this.getClass.getResourceAsStream("/spider-monkeys-24.jpg")

      val bytes = {
        val stream = new ByteArrayOutputStream()
        Iterator
          .continually(jpgPic.read)
          .takeWhile(-1 !=)
          .foreach(stream.write)
        stream.toByteArray
      }

      val client = Httpx.newService(s"${proxyHost}:${proxyPort}")

      val request = Request(Method.Get, "/echo")
      request.write(bytes)
      val response = Await.result(client(request))

      response.content ==== com.twitter.io.Buf.ByteArray.Owned(bytes)
    }
  }
}
