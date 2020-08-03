package parser;

import value.ValuePartial.Probe;

// simple hash set implementation that uses linear probing
// designed to be memory efficient.
public class ProbeSet {
	private int[] set = new int[8];
	private int size;
	
	public ProbeSet () {}
	
	public ProbeSet (ProbeSet.Resolver ... probes) {
		this ();
		for (ProbeSet.Resolver p : probes) p.getResolves(this);
	}
	
	private ProbeSet(ProbeSet set) {
		this.set = set.set.clone();
		this.size = set.size;
	}
	
	private ProbeSet(ProbeSet set, int remove) {
		this.size = set.size;
		this.set = new int[set.set.length];
		
		for (int i = 0; i < set.set.length; i++) {
			if (set.set[i] == remove) {
				this.size--;
				continue;
			}
			
			if (set.set[i] != 0) set(this.set, set.set[i]);
		}
		
	}
	
	public boolean has(Probe probe) {
		int hash = probe.id % this.set.length;
		
		while (this.set[hash] != 0) {
			if (this.set[hash] == probe.id) return true;
			
			hash++;
			if (hash >= this.set.length) hash = 0;
		}
		
		return false;
	}
	
	public void ensureAvailable (int size) {
		int wants = this.set.length;
		while ((this.size + size) * 2 > wants) {
			wants *= 2;
		}
		
		if (wants != this.set.length) {
			int[] set = this.set;
			this.set = new int[wants];
			
			for (int i = 0; i < set.length; i++) {
				if (set[i] > 0) set(this.set, set[i]);
			}
		}
	}
	
	public void set (ProbeSet set) {
		this.ensureAvailable(set.size);
		
		for (int i = 0; i < set.set.length; i++) {
			if (set.set[i] != 0) {
				if (set(this.set, set.set[i])) this.size++;
			}
		}
	}
	
	public void set (Probe ... probes) {
		this.ensureAvailable(probes.length);
		
		for (Probe p : probes) {
			if (set(this.set, p.id)) this.size++;
		}
	}
	
	public static boolean set (int[] set, int id) {
		int hash = id % set.length;
		
		while (set[hash] != 0) {
			if (set[hash] == id) {
				return false;
			}
			
			hash++;
			if (hash >= set.length) hash = 0;
		}
		
		set[hash] = id;
		return true;
	}
	
	public ProbeSet add (Probe ... probes){
		ProbeSet set = new ProbeSet(this);
		set.set(probes);
		return set;
	}
	
	public ProbeSet replace (Probe remove, Resolver resolver) {
		ProbeSet set = new ProbeSet(this, remove.id);
		resolver.getResolves(set);
		return set;
	}
	
	public static interface Resolver {
		public default void getResolves(ProbeSet set) {}
	}
}