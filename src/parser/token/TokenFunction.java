package parser.token;

import event.Event;
import event.EventFunction;
import event.EventHiddenFunction;
import event.EventModifier;
import event.EventNull;
import parser.Stream;

public class TokenFunction extends TokenBlock {
	public TokenFunction (Stream s) {
		super(TokenBase.readBlock(s, '}'));
	}
	
	public static String indent (String s) {
		StringBuilder ss = new StringBuilder();
		ss.append('\t');
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			if (c == '\n') {
				ss.append('\n').append('\t');
			}else {
				ss.append(c);
			}
		}
		
		return ss.toString();
	}
	
	@Override
	public String toString () {
		if (this.getLength() <= 1) {
			return "{" + TokenBlock.toString(this, '\n') + "}";
		}else {
			return "{\n" + indent(TokenBlock.toString(this, '\n')) + "\n}";
		}
	}
	
	public Event createModifierEvent () {
		if (this.getTokens().length == 0){
			return new EventNull();
		}else {
			return new EventModifier(super.createEvent());
		}
	}
	
	public Event createHiddenEvent () {
		if (this.getTokens().length == 0) {
			return new EventNull();
		}else {
			return new EventHiddenFunction(super.createEvent());
		}
	}
	
	@Override
	public Event createEvent() {
		if (this.getTokens().length == 0){
			return new EventNull();
		}else {
			return new EventFunction(super.createEvent());
		}
	}
}
