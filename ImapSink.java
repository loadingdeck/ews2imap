import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import java.security.KeyStore;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import mcpy.emailModel.*;

class ImapSink {
 private String userName;
 private String passWord;
 private String address;
 private IMAPFolder inbox;
 private Session session;

 public ImapSink(String userName_, String passWord_, String address_) {
  userName = userName_;
  passWord = passWord_;
  address = address_;
 }

 public boolean connect() {
  try {
   Properties props = System.getProperties();
   System.out.println("imapsink connect");
   props.setProperty("mail.imap.auth.plain.disable", "false");
   props.setProperty("mail.imap.auth.login.disable", "false");
   props.setProperty("mail.imap.starttls.enable", "true");
   props.setProperty("mail.imaps.auth.plain.disable", "false");
   props.setProperty("mail.imaps.auth.login.disable", "false");
   props.setProperty("mail.imaps.starttls.enable", "true");

   KeyStore ks = KeyStore.getInstance("JKS");

   session = Session.getInstance(props, null);
   System.out.println("imapsink connect");
   Store store = null;
   store = session.getStore("imap");
   System.out.println("imapsink connect");
   store.connect(address, userName, passWord);
   System.out.println("imapsink connect");

   inbox = (IMAPFolder) store.getFolder("Inbox");
   inbox.open(Folder.READ_WRITE);
  } catch (Exception e) {
   System.out.println("imapsink connect exception.");
   System.out.println(e.toString());
   e.printStackTrace();
   return false;
  }
  return true;
 }

 public void uploadMessage(Email msg) {
  try {
   Message message = new MimeMessage(session);
   message.setSubject(msg.subject);

   for (int i = 0; i != msg.headers.length; i++) {
    message.addHeader(msg.headers[i].name, msg.headers[i].value.trim());
   }

   message.setContent(msg.contents, msg.contType);
   message.setFrom(new InternetAddress(msg.from.address, msg.from.fullName));

   Message[] uids = inbox.addMessages(new Message[] {
    message
   });

   inbox.setFlags(uids, new Flags(Flags.Flag.SEEN), msg.isRead);

  } catch (AddressException e) {
   System.out.println("ImapSink::uploadMessage AddressException");
  } catch (MessagingException e) {
   System.out.println("ImapSink::uploadMessage MessagingException");
  } catch (Exception e) {
   System.out.println("ImapSink::uploadMessage miscellaneous exception");
  }
 }
}
