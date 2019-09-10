package value;

public interface Value {
	public static final Value NULL = new Value() {
		@Override
		public Value call(Value v) {
			return Value.NULL;
		}
	};
	
	public Value call (Value v);
	
	public default Value call (String s) {
		return this.call(new ValueIdentifier(s));
	}
	
	public static boolean compare(Value v, String identifier){
		if (v instanceof ValueIdentifier) {
			return ((ValueIdentifier) v).id == identifier;
		}
		
		return false;
	}
}
