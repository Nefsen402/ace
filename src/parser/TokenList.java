package parser;

import java.util.ArrayList;
import java.util.List;

import parser.token.Token;
import parser.token.TokenImmediate;

public class TokenList {
	private final List<Token> tokens;
	
	public TokenList () {
		this.tokens = new ArrayList<Token>();
	}
	
	public void push (Token t) {
		this.tokens.add(t);
	}
	
	public Token[] toArray () {
		Token[] tokens = new Token[this.tokens.size()];
		
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = this.tokens.get(i);
		}
		
		return tokens;
	}
	
	public int size () {
		return this.tokens.size();
	}
	
	public Token get (int i) {
		return this.tokens.get(i);
	}
	
	public void set (int i, Token t) {
		this.tokens.set(i, t);
	}
	
	public Token createImmediateString () {
		Token t = this.tokens.get(this.tokens.size() - 1);
		
		for (int i = this.tokens.size() - 2; i >= 0; i--) {
			t = new TokenImmediate(this.tokens.get(i), t);
		}
		
		return t;
	}
}