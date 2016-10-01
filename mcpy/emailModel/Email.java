package mcpy.emailModel;

import java.util.Date;

// Email class, to provide an email provider agnostic structure for representing an email message..
//  - Data only class, no functions..

  public class Email{
  public EmailAddress from;
  public EmailAddress[] to;
  public EmailAddress[] cc;
  public String subject;

  public String contents;
  public String contType;
  public boolean isRead;
  public Date date;

  public Header[] headers;

  public Email() {
    isRead = false;
  }
}
