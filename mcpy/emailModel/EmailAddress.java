package mcpy.emailModel;

// Data only class to represent an email address..

public class EmailAddress{
  public String address;
  public String fullName;
  public boolean hasFullName;
  
  public EmailAddress(){
    hasFullName = false;
  }
}
