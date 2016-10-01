import java.net.*;
import java.io.*;
import java.util.LinkedList;
import microsoft.exchange.webservices.data.credential.*;
import microsoft.exchange.webservices.data.core.*;
import microsoft.exchange.webservices.data.core.enumeration.misc.*;
import microsoft.exchange.webservices.data.core.enumeration.property.*;
import microsoft.exchange.webservices.data.core.enumeration.search.*;
import microsoft.exchange.webservices.data.core.enumeration.service.*;
import microsoft.exchange.webservices.data.search.*;
import microsoft.exchange.webservices.data.core.service.item.*;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.search.filter.*;
import microsoft.exchange.webservices.data.core.service.schema.*;
import mcpy.emailModel.Email;

class ExchangeSource {
 private String userName;
 private String password;
 private String uri;
 private ExchangeService service;

 public ExchangeSource(String userName_, String password_, String Uri_) {
  userName = userName_;
  password = password_;
  uri = Uri_;
 }


 public void connectExchange() {
  try {
   service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
   ExchangeCredentials credentials = new WebCredentials(userName, password);
   service.setCredentials(credentials);
   service.setUrl(new URI(uri));
  } catch (Exception e) {
   System.out.println("connectExchange exception!");
  }
 }

 public LinkedList < Email > listItems() {
  try {
   int offset = 0;
   LinkedList < Email > messages = new LinkedList < Email > ();

   while (true) {
    int inOffset = offset;
    ItemView view = new ItemView(100, offset);
    SearchFilter unreadFilter = new SearchFilter.IsEqualTo(EmailMessageSchema.IsRead, false);
    FindItemsResults < Item > findResults = service.findItems(new FolderId(WellKnownFolderName.Inbox), view);

    for (Item item: findResults.getItems()) {
     Email cur = new Email();

     // Do something with the item as shown
     System.out.println("Message details:");
     System.out.println("id==========" + item.getId());
     System.out.println("sub==========" + item.getSubject());
     System.out.println("Item class:: " + item.getItemClass());

     //Copy details into Email object..
     cur.subject = item.getSubject();

     if (item.getItemClass().compareTo("IPM.Note") == 0) {
      EmailMessage msg = EmailMessage.bind(service, item.getId());
      MessageBody mbdy = msg.getBody();

      if (msg.getHasAttachments() == false) {
       System.out.println("Message without attachments..");
       cur.contents = mbdy.toString();
       if (mbdy.getBodyType() == BodyType.HTML) {
        cur.contType = "text/html";
       } else {
        cur.contType = "text/plain";
       }
      } else {
       System.out.println("Message with attachments..");
       AttachmentCollection ac = msg.getAttachments();
       for (Attachment at: ac) {
        System.out.println("Attachment!");
        if (at instanceof ItemAttachment) {
         ItemAttachment ia = (ItemAttachment) at;
         System.out.println("Item Attachment");
         System.out.println("Message with attachments..");
         ia.load();
         cur.contType = ia.getContentType();

         System.out.println("Message with attachments.. content type " + cur.contType);

         System.out.println("Message with attachments..");
         //cur.contents = new String(ia.getItem().getMimeContent().getContent());
         System.out.println(ia.getItem().getMimeContent().getContent());
         System.out.println("Message with attachments..");
        } else if (at instanceof FileAttachment) {
         System.out.println("File attachment.");
        }
       }
      }
      cur.headers = new mcpy.emailModel.Header[msg.getInternetMessageHeaders().getCount()];
      int j = 0;
      for (microsoft.exchange.webservices.data.property.complex.InternetMessageHeader mh: msg.getInternetMessageHeaders()) {
       mcpy.emailModel.Header nh = new mcpy.emailModel.Header();
       nh.name = mh.getName();
       nh.value = mh.getValue();
       cur.headers[j] = nh;
       j++;
      }

      System.out.println("listItems");
      cur.to = new mcpy.emailModel.EmailAddress[msg.getToRecipients().getCount()];
      j = 0;
      for (microsoft.exchange.webservices.data.property.complex.EmailAddress ea: msg.getToRecipients()) {
       mcpy.emailModel.EmailAddress ead = new mcpy.emailModel.EmailAddress();
       ead.address = ea.getAddress();
       String nn = ea.getName();
       if (nn.length() > 0) {
        ead.hasFullName = true;
        ead.fullName = nn;
       }
       cur.to[j] = ead;
       j++;
      }

      System.out.println("listItems");
      cur.cc = new mcpy.emailModel.EmailAddress[msg.getCcRecipients().getCount()];
      j = 0;
      for (microsoft.exchange.webservices.data.property.complex.EmailAddress ea: msg.getCcRecipients()) {
       mcpy.emailModel.EmailAddress ead = new mcpy.emailModel.EmailAddress();
       ead.address = ea.getAddress();
       String nn = ea.getName();
       if (nn.length() > 0) {
        ead.hasFullName = true;
        ead.fullName = nn;
       }
       cur.cc[j] = ead;
       j++;
      }

      System.out.println("listItems");
      mcpy.emailModel.EmailAddress from = new mcpy.emailModel.EmailAddress();
      from.address = msg.getFrom().getAddress();
      from.fullName = msg.getFrom().getName();
      from.hasFullName = true;
      cur.from = from;
      System.out.println("listItems");

      cur.isRead = msg.getIsRead();
      if (!cur.isRead) {
       msg.setIsRead(true);
       msg.update(ConflictResolutionMode.AlwaysOverwrite);
      }

      cur.date = msg.getLastModifiedTime();
     }

     if (!cur.isRead) {
      messages.addLast(cur);
     }

     System.out.println("\n\n");
     offset++;
    }

    if (offset == inOffset) {
     break;
    }
   }

   return messages;

  } catch (Exception e) {
   System.out.println("listFirstTenItems exception!");
   return new LinkedList < Email > ();
  }
 }
}
