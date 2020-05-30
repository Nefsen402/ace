package value.intrinsic;

import value.Value;
import value.ValueDefer;
import value.ValueProbe;

public class Assign implements Value{
	public static final Value instance = ValueDefer.accept(name -> ValueDefer.accept(value -> new Assign(name, value)));
	
	private final Value name, value;
	
	public Assign (Value name, Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Value resolve(ValueProbe probe, Value value) {
		return new Assign(this.name.resolve(probe, value), this.value.resolve(probe, value));
	}
	
	@Override
	public Value call (Value v) {
		return this.value.call(v);
	}
	
	@Override
	public Value getID (Getter getter) {
		return this.name.getID(getter);
	}
	
	@Override
	public String toString() {
		return "Assignment(" + this.name.toString() + ") -> " + this.value.toString();
	}
}