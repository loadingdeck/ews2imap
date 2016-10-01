import java.net.*;
import java.io.*;
import java.util.*;
import mcpy.emailModel.*;

class Main{
	public static void main(String args[]){
		Main ths = new Main();

		ExchangeSource nes = new ExchangeSource(args[0], args[1], args[2]);
		nes.connectExchange();
		LinkedList<Email> messages = nes.listItems();

		ImapSink imap = new ImapSink(args[3], args[4], args[5]);

		imap.connect();

		for(Email a : messages){
			imap.uploadMessage(a);
		}
	}
}
