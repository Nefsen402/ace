package parser.token;

import node.Node;
import node.NodeCall;
import node.NodeIdentifier;

public class TokenOperatorImmediate extends Token implements Modifier{
	private final Token token;
	private final String operator;
	
	public TokenOperatorImmediate(String operator, Token token) {
		this.operator = operator;
		this.token = token;
	}
	
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String toString() {
		return this.operator + this.token.toString();
	}
	
	@Override
	public Node createEvent() {
		return new NodeCall(this.token.createEvent(), new NodeIdentifier('`' + this.operator + '`'));
	}
	
	@Override
	public boolean isModifier() {
		return this.token instanceof Modifier && ((Modifier) this.token).isModifier();
	}
}
