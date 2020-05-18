package value;

import parser.Color;

public class ValueProbe implements Value {
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		if (probe == this) {
			return value;
		}else {
			return this;
		}
	}
	
	@Override
	public boolean canCreateEffects() {
		return true;
	}
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	public Value callClear (Value arg) {
		return new Call (new ValueEffect(this), arg);
	}
	
	public Value getIDClear (Getter getter) {
		return new Identifier(new ValueEffect(this), getter);
	}
	
	public static class Call extends ValueProbe {
		public final Value parent;
		public final Value argument;
		
		public Call (Value parent, Value argument) {
			this.parent = parent;
			this.argument = argument;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString() + "\n");
			b.append(Color.indent(this.parent.toString(), "|-", "| "));
			b.append("\n");
			b.append(Color.indent(this.argument.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return this.parent.resolve(probe, value).call(this.argument.resolve(probe, value));
		}
	}
	
	public static class Identifier extends ValueProbe {
		public final Value parent;
		public final Getter getter;
		
		public Identifier (Value parent, Getter getter) {
			this.parent = parent;
			this.getter = getter;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString() + "\n");
			b.append(Color.indent(this.parent.toString(), "|-", "  "));
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			return this.parent.resolve(probe, value).getID(id -> {
				return this.getter.resolved(id).resolve(probe, value);
			});
		}
	}
}
