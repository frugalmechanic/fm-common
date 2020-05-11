package fm.common

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TestEmailSender extends AnyFunSuite with Matchers {
  test("isValidEmail") {
    import EmailSender._

    def testAddresses(addresses: Seq[String], result: Boolean): Unit = {
      addresses.foreach { email: String =>
        withClue(email) { isValidEmail(email) shouldBe result }
      }
    }

    // Some (decent-ish) test cases: https://blogs.msdn.microsoft.com/testing123/2009/02/06/email-address-test-cases/

    val validAddresses: Seq[String] = Seq(
      "email@domain.com",
      "firstname.lastname@domain.com",
      "email@subdomain.domain.com",
      "firstname+lastname@domain.com",
      "email@123.123.123.123",
      //"email@[123.123.123.123]",
      //"“email”@domain.com",
      "1234567890@domain.com",
      "email@domain-one.com",
      "_______@domain.com",
      "email@domain.name",
      "email@domain.co.jp",
      "firstname-lastname@domain.com"
    )

    testAddresses(validAddresses, true)

    val invalidAddresses: Seq[String] = Seq(
      "plainaddress",
      "#@%^%#$@#$@#.com",
      "@domain.com",
      "Joe Smith <email@domain.com>",
      "email.domain.com",
      "email@domain@domain.com",
      //".email@domain.com",
      //"email.@domain.com",
      //"email..email@domain.com",
      "あいうえお@domain.com",
      "email@domain.com (Joe Smith)",
      "email@domain",
      "email@-domain.com",
      //"email@111.222.333.44444",
      "email@domain..com"
    )

    testAddresses(invalidAddresses, false)
  }
}
