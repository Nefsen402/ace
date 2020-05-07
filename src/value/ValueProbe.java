package value;

public class ValueProbe implements Value {
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		if (probe == this) {
			return value;
		}else {
			return this;
		}
	}
	
	public Value clear () {
		ValueProbe parent = this;
		
		return new ValueProbe () {
			@Override
			public Value resolve(ValueProbe probe, Value value) {
				Value v = parent.resolve(probe, value);
				
				return ValueEffect.clear(v);
			}
		};
	}
	
	@Override
	public Value call (Value arg) {
		return new Call(this, arg);
	}
	
	@Override
	public Value getID (Getter getter) {
		return new Identifier(this, getter);
	}
	
	public static void append(String s, String padding, StringBuilder b) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			b.append(c);
			
			if (c == '\n') {
				b.append(padding);
			}
		}
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
			b.append(super.toString() + "\n|-");
			append(this.parent.toString(), "| ", b);
			b.append("\n|-");
			append(this.argument.toString(), "  ", b);
			
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
		public final Resolve[] resolves;
		
		public Identifier (Value parent, Getter getter, Resolve ... resolves) {
			this.parent = parent;
			this.getter = getter;
			this.resolves = resolves;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(super.toString() + "\n|-");
			append(this.parent.toString(), "  ", b);
			
			return b.toString();
		}
		
		@Override
		public Value resolve(ValueProbe probe, Value value) {
			Value presolved = this.parent.resolve(probe, value);
			
			//the parent is a probe, that means we still don't know enough info to know the id of this value.
			if (presolved instanceof ValueProbe) {
				return new Identifier(presolved, getter);
			}else {
				Value ret = presolved.getID(this.getter);
				
				for (int i = 0; i < this.resolves.length; i++) {
					ret = ret.resolve(this.resolves[i].probe, this.resolves[i].value);
				}
				
				return ret.resolve(probe, value);
			}
		}
	}
	
	public static class Resolve {
		public final Value value;
		public final ValueProbe probe;
		
		public Resolve(ValueProbe probe, Value value) {
			this.probe = probe;
			this.value = value;
		}
	}
}
