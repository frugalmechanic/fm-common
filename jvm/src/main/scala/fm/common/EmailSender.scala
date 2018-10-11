package fm.common

import java.util.Properties
import javax.mail.{Message, Session, Transport}
import javax.mail.internet.{AddressException, InternetAddress, MimeMessage}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

object EmailSender {
  // RegEx From: https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
  //   Modified to require at least one .<tld> at the end, so root@localhost isn't valid (changed *$""") => +$""")
  private val emailRegex: Regex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$""".r

  /*
   * Simple reg-ex based email address validation
   */
  def isValidEmail(email: String): Boolean = {
    email.toBlankOption.map{ emailRegex.findFirstMatchIn(_).isDefined }.getOrElse(false)
  }

  private val emailSenderTaskRunner: TaskRunner = TaskRunner(
    name = "EmailSender",
    threads = 16,
    queueSize = 128,
    blockOnFullQueue = false /* throw an exception when full */
  )
}

final case class EmailSender(user: String, pass: String, host: String) {

  def send(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    body: String
  ): Unit = {
    Service.call("EmailSender", backOffStrategy = Service.BackOffStrategy.exponentialForRemote(), maxRetries = 3) {
      sendImpl(to, from, cc, bcc, replyTo, subject, body)
    }
  }
  
  def sendAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    body: String
  ): Future[Unit] = {
    Service.callAsync("EmailSenderAsync", backOffStrategy = Service.BackOffStrategy.exponentialForRemote(), maxRetries = 3) {
      EmailSender.emailSenderTaskRunner.submit{
        sendImpl(to, from, cc, bcc, replyTo, subject, body)
      }
    }
  }
  
  private def sendImpl(to: String, from: String, cc: Seq[String], bcc: Seq[String], replyTo: String, subject: String, body: String): Unit = {
    val props: Properties = new Properties
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.user", user)
    props.put("mail.smtp.password", pass)
    props.put("mail.smtp.port", "587")
    props.put("mail.smtp.auth", "true")

    val session: Session = Session.getDefaultInstance(props, null)
    val message: MimeMessage = new MimeMessage(session)

    message.setFrom(new InternetAddress(from))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))

    cc.foreach { cc: String =>
      message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc))
    }

    bcc.foreach { bcc: String =>
      message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc))
    }

    if (replyTo.isNotNullOrBlank) try {
      message.setReplyTo(Array(new InternetAddress(replyTo)))
    } catch {
      case ex: AddressException => // Bad replyTo Address, so don't set it
    }

    message.setSentDate(new java.util.Date)
    message.setSubject(subject, "utf-8")
    message.setText(body, "utf-8")
    
    // Not AutoCloseable
    val transport: Transport = session.getTransport("smtp")
    try {
      transport.connect(host, user, pass)
      transport.sendMessage(message, message.getAllRecipients())
    } finally {
      transport.close()
    }
  }
}