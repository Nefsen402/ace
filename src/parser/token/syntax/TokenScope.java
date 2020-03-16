package parser.token.syntax;

import parser.Stream;
import parser.token.Modifier;
import parser.token.Token;
import value.Value;

public class TokenScope extends TokenProcedure implements Modifier{
	public TokenScope (Stream s) {
		super(readBlock(s, ')'));
	}
	
	public TokenScope (Token ... tokens) {
		super(tokens);
	}
	
	@Override
	public String toString (){
		if (this.getTokens().length == 0) {
			return "()";
		}else if (this.getTokens().length == 1) {
			return "(" + this.getTokens()[0].toString() + ")";
		}else {
			return "(\n" + TokenEnvironment.indent(TokenProcedure.toString(this, '\n')) + "\n)";
		}
	}
	
	@Override
	public Value createNode() {
		return Value.call(Value.id("Scope"), super.createNode());
	}
	
	@Override
	public boolean isModifier() {
		Token[] tokens = this.getTokens();
		if (tokens.length != 1) return false;
		if (!(tokens[0] instanceof TokenStatement)) return false;
		
		return ((TokenStatement) tokens[0]).isModifier();
	}
}
