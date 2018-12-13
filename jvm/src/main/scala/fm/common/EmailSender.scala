package fm.common

import java.util.Properties
import javax.mail.{Message, Session, Transport}
import javax.mail.internet.{AddressException, InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import scala.concurrent.Future
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

  def sendPlaintext(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String
  ): Unit = sendSyncImpl(
    loggingName = "sendPlaintext",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = Some(plaintextBody),
    htmlBody = None
  )

  def sendPlaintextAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendPlaintextAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = Some(plaintextBody),
    htmlBody = None
  )

  def sendHtml(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String
  ): Unit = sendSyncImpl(
    loggingName = "sendHtml",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = None,
    htmlBody = Some(htmlBody)
  )

  def sendHtmlAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendHtmlAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = None,
    htmlBody = Some(htmlBody)
  )

  def sendMultipart(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    htmlBody: String
  ): Unit = sendSyncImpl(
    loggingName = "sendMultipart",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = Some(plaintextBody),
    htmlBody = Some(htmlBody)
  )

  def sendMultipartAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    htmlBody: String
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendMultipartAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = Some(plaintextBody),
    htmlBody = Some(htmlBody)
  )

  private def sendSyncImpl(
    loggingName: String,
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: Option[String],
    htmlBody: Option[String]
  ): Unit = Service.call(s"EmailSender.${loggingName}", backOffStrategy = Service.BackOffStrategy.exponentialForRemote(), maxRetries = 3) {
    sendImpl(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      htmlBody = htmlBody
    )
  }

  private def sendAsyncImpl(
    loggingName: String,
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: Option[String],
    htmlBody: Option[String]
  ): Future[Unit] = Service.callAsync(s"EmailSender.${loggingName}", backOffStrategy = Service.BackOffStrategy.exponentialForRemote(), maxRetries = 3) {
    EmailSender.emailSenderTaskRunner.submit{
      sendImpl(
        to = to,
        from = from,
        cc = cc,
        bcc = bcc,
        replyTo = replyTo,
        subject = subject,
        plaintextBody = plaintextBody,
        htmlBody = htmlBody
      )
    }
  }

  private def sendImpl(to: String, from: String, cc: Seq[String], bcc: Seq[String], replyTo: String, subject: String, plaintextBody: Option[String], htmlBody: Option[String]): Unit = {
    require(plaintextBody.isDefined || htmlBody.isDefined, "Either plaintextBody or htmlBody has to be set!")

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

    if (plaintextBody.isDefined && htmlBody.isDefined) {
      message.setContent(createMultipart(plaintextBody.get, htmlBody.get))
    } else {
      plaintextBody.foreach{ message.setText(_, "utf-8") }
      htmlBody.foreach{ message.setText(_, "utf-8", "html")  }
    }

    // Not AutoCloseable
    val transport: Transport = session.getTransport("smtp")
    try {
      transport.connect(host, user, pass)
      transport.sendMessage(message, message.getAllRecipients())
    } finally {
      transport.close()
    }
  }

  private def createMultipart(plaintextBody: String, htmlBody: String): MimeMultipart = {
    val multipart: MimeMultipart = new MimeMultipart()

    // According to the RFC for Multipart content, the plainest format should be set first and the richest format last
    // Source: https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
    multipart.addBodyPart(createBodyPart(text = plaintextBody, isHtml = false))
    multipart.addBodyPart(createBodyPart(text = htmlBody     , isHtml = true ))

    multipart
  }

  private def createBodyPart(text: String, isHtml: Boolean): MimeBodyPart = {
    val bodyPart: MimeBodyPart = new MimeBodyPart

    if (isHtml) {
      bodyPart.setText(text, "utf-8", "html")
    } else {
      bodyPart.setText(text, "utf-8")
    }

    bodyPart
  }
}