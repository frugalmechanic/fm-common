package fm.common

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Properties
import javax.activation.{DataHandler, DataSource, FileDataSource}
import javax.mail.{Message, Part, Session, Transport}
import javax.mail.internet.{AddressException, InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import javax.mail.util.ByteArrayDataSource
import scala.concurrent.Future
import scala.util.matching.Regex

object EmailSender {
  // RegEx From: https://www.w3.org/TR/html5/forms.html#valid-e-mail-address
  //   Modified to require at least one .<tld> at the end, so root@localhost isn't valid (changed *$""") => +$""")
  private val emailRegex: Regex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$""".r

  sealed trait Attachment {
    def fileName: String
    def contentType: String
    def makeDataSource: DataSource
  }

  final case class FileAttachment(fileName: String, contentType: String, file: File) extends Attachment {
    def makeDataSource: DataSource = new FileDataSource(file)
  }

  final case class BytesAttachment(fileName: String, contentType: String, bytes: Array[Byte]) extends Attachment {
    def makeDataSource: DataSource = new ByteArrayDataSource(bytes, contentType)
  }

  final case class StringAttachment(fileName: String, contentType: String, contents: String) extends Attachment {
    def makeDataSource: DataSource = new ByteArrayDataSource(contents.getBytes(StandardCharsets.UTF_8), contentType)
  }

  /*
   * Simple reg-ex based email address validation
   */
  def isValidEmail(email: String): Boolean = {
    email.toBlankOption.map{ emailRegex.findFirstMatchIn(_).isDefined }.getOrElse(false)
  }

  private val emailSenderTaskRunner: TaskRunner = TaskRunner(
    name = "EmailSender",
    threads = 16,
    queueSize = 256,
    blockOnFullQueue = false /* throw an exception when full */
  )
}

final case class EmailSender(user: String, pass: String, host: String) {
  private val session: Session = {
    val props: Properties = new Properties
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.user", user)
    props.put("mail.smtp.password", pass)
    props.put("mail.smtp.port", "587")
    props.put("mail.smtp.auth", "true")

    Session.getInstance(props)
  }

  // Non-Attachment version for backwards compat
  def sendPlaintext(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String
  ): Unit = {
    sendPlaintext(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      attachments = Nil
    )
  }

  def sendPlaintext(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Unit = sendSyncImpl(
    loggingName = "sendPlaintext",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = plaintextBody.toBlankOption,
    htmlBody = None,
    attachments = attachments
  )

  // Non-Attachment version for backwards compat
  def sendPlaintextAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String
  ): Future[Unit] = {
    sendPlaintextAsync(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      attachments = Nil
    )
  }

  def sendPlaintextAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendPlaintextAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = plaintextBody.toBlankOption,
    htmlBody = None,
    attachments = attachments
  )

  // Non-Attachment version for backwards compat
  def sendHtml(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String
  ): Unit = {
    sendHtml(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      htmlBody = htmlBody,
      attachments = Nil
    )
  }

  def sendHtml(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Unit = sendSyncImpl(
    loggingName = "sendHtml",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = None,
    htmlBody = htmlBody.toBlankOption,
    attachments = attachments
  )

  // Non-Attachment version for backwards compat
  def sendHtmlAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String
  ): Future[Unit] = {
    sendHtmlAsync(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      htmlBody = htmlBody,
      attachments = Nil
    )
  }

  def sendHtmlAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    htmlBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendHtmlAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = None,
    htmlBody = htmlBody.toBlankOption,
    attachments = attachments
  )

  // Non-Attachment version for backwards compat
  def sendMultipart(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    htmlBody: String
  ): Unit = {
    sendMultipart(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      htmlBody = htmlBody,
      attachments = Nil
    )
  }

  def sendMultipart(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    htmlBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Unit = sendSyncImpl(
    loggingName = "sendMultipart",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = plaintextBody.toBlankOption,
    htmlBody = htmlBody.toBlankOption,
    attachments = attachments
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
  ): Future[Unit] = {
    sendMultipartAsync(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      htmlBody = htmlBody,
      attachments = Nil
    )
  }

  def sendMultipartAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: String,
    htmlBody: String,
    attachments: Seq[EmailSender.Attachment]
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendMultipartAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = plaintextBody.toBlankOption,
    htmlBody = htmlBody.toBlankOption,
    attachments = attachments
  )

  def sendMultipartAsync(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: Option[String],
    htmlBody: Option[String],
    attachments: Seq[EmailSender.Attachment]
  ): Future[Unit] = sendAsyncImpl(
    loggingName = "sendMultipartAsync",
    to = to,
    from = from,
    cc = cc,
    bcc = bcc,
    replyTo = replyTo,
    subject = subject,
    plaintextBody = plaintextBody,
    htmlBody = htmlBody,
    attachments = attachments
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
    htmlBody: Option[String],
    attachments: Seq[EmailSender.Attachment]
  ): Unit = Service.call(s"EmailSender.${loggingName}", backOffStrategy = Service.BackOffStrategy.exponentialForRemote(), maxRetries = 3) {
    sendImpl(
      to = to,
      from = from,
      cc = cc,
      bcc = bcc,
      replyTo = replyTo,
      subject = subject,
      plaintextBody = plaintextBody,
      htmlBody = htmlBody,
      attachments = attachments
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
    htmlBody: Option[String],
    attachments: Seq[EmailSender.Attachment]
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
        htmlBody = htmlBody,
        attachments = attachments
      )
    }
  }

  private def sendImpl(
    to: String,
    from: String,
    cc: Seq[String],
    bcc: Seq[String],
    replyTo: String,
    subject: String,
    plaintextBody: Option[String],
    htmlBody: Option[String],
    attachments: Seq[EmailSender.Attachment]
  ): Unit = {
    require(plaintextBody.isDefined || htmlBody.isDefined, "Either plaintextBody or htmlBody has to be set!")

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
      case _: AddressException => // Bad replyTo Address, so don't set it
    }

    message.setSentDate(new java.util.Date)
    message.setSubject(subject, "utf-8")

    val multipart: MimeMultipart = new MimeMultipart()

    // According to the RFC for Multipart content, the plainest format should be set first and the richest format last
    // Source: https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
    plaintextBody.foreach{ plaintext: String =>
      val bodyPart: MimeBodyPart = new MimeBodyPart
      bodyPart.setText(plaintext, "utf-8")
      multipart.addBodyPart(bodyPart)
    }

    htmlBody.foreach{ html: String =>
      val bodyPart: MimeBodyPart = new MimeBodyPart
      bodyPart.setText(html, "utf-8", "html")
      multipart.addBodyPart(bodyPart)
    }

    attachments.foreach { att: EmailSender.Attachment =>
      val bodyPart: MimeBodyPart = new MimeBodyPart
      bodyPart.setFileName(att.fileName)
      bodyPart.setDataHandler(new DataHandler(att.makeDataSource))
      bodyPart.setHeader("Content-Type", att.contentType)
      bodyPart.setDisposition(Part.ATTACHMENT)
      multipart.addBodyPart(bodyPart)
    }

    message.setContent(multipart)

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