package node;

import value.Value;
import value.ValueIdentifier;

public class NodeEnvironment implements Node{
	private final Node contents;
	
	public NodeEnvironment (Node contents) {
		this.contents = contents;
	}
	
	@Override
	public Value run(Value environment) {
		return p -> this.contents.run(env -> {
			Value ident = new ValueIdentifier(((ValueIdentifier) env).id, environment);
			Value v = p.call(ident);
			
			if (v == Value.NULL) {
				return ident;
			}else {
				return v;
			}
		});
	}
	
	@Override
	public String toString() {
		return "{" + this.contents.toString() + "}";
	}
}