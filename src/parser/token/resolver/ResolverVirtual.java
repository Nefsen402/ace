package parser.token.resolver;

import java.util.Arrays;
import java.util.HashSet;

import parser.token.Resolver;
import value.Unsafe;
import value.node.Node;
import value.node.NodeIdentifier;

public class ResolverVirtual extends Resolver {
	protected final Resolver[] resolvers;
	
	public ResolverVirtual (String name, Resolver ... resolvers) {
		super(name);
		this.resolvers = resolvers;
	}
	
	public Resolver[] getResolvers() {
		return resolvers;
	}
	
	@Override
	public Node createNode() {
		IdentifierResolver pairRoot = null;
		IdentifierResolver[] pairs;
		
		{
			HashSet<String> names = new HashSet<>();
			Resolver[] p = this.getResolvers();
			
			pairs = new IdentifierResolver[p.length];
			int len = 0;
			
			for (int i = 0; i < p.length; i++) {
				String name = p[i].getName();
				
				if (!names.contains(name)) {
					names.add(name);
					
					pairs[len++] = new IdentifierResolver(p[i]);
					if (name.equals("root")) pairRoot = pairs[len - 1];
				}
			}
			
			pairs = Arrays.copyOf(pairs, len);
		}
		
		Node rel = Node.id();
		Node root = Node.id();
		Node proot = Node.id();
		
		Node set = Node.id();
		Node get = Node.id();
		Node rparam = Node.id();
		
		Node genRoot;
		Node parent;
		
		if (this.getName().equals("root")) {
			genRoot = Node.call(get, Node.id(), rparam);
			
			if (pairRoot != null) {
				Node param2 = Node.id();
				parent = Node.call(Unsafe.FUNCTION, param2, Node.env(Node.call(get, Node.id(), Node.id("root"), param2)));
			}else{
				parent = proot;
			}
		}else if (pairRoot != null) {
			genRoot = Node.call(get, Node.id(), Node.id("root"), rparam);
			parent = Node.call(proot, Unsafe.PARENT, Node.id(this.getName()));
		} else {
			genRoot = Node.call(proot, rparam);
			parent = Node.call(proot, Unsafe.PARENT, Node.id(this.getName()));
		}
		
		Node block = Node.call(parent, rel);
		
		for (IdentifierResolver p : pairs) {
			block = Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, rel, p.identifier, Node.env(
				p.uniqueIdentifier
			), Node.env(
				block
			)));
		}
		
		block = Node.call(Unsafe.FUNCTION, rel, Node.env(block));
		
		for (IdentifierResolver p : pairs) {
			Node troot;
			
			if (p == pairRoot) {
				Node param = Node.id();
				
				troot = Node.call(Unsafe.FUNCTION, param, Node.env(
					Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, Unsafe.PARENT, param, Node.env(
						proot
					), Node.env(
						Node.call(proot, param)
					)))
				));
			}else {
				troot = root;
			}
			
			block = Node.call(Unsafe.FUNCTION, p.uniqueIdentifier, Node.env(block), Node.call(p.createNode(), troot));
		}
		
		block = Node.call(Unsafe.MUTABLE, Unsafe.DO, Node.call(Unsafe.FUNCTION, set, Node.env(
			Node.call(Unsafe.FUNCTION, get, Node.env(
				Node.call(set, Node.call(Unsafe.FUNCTION, root, Node.env(
					block
				), Node.call(Unsafe.FUNCTION, rparam, Node.env(
					Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, Node.id("root"), rparam, Node.env(
							rparam
					), Node.env(
						Node.call(Unsafe.SCOPE, Node.call(Unsafe.COMPARE, Unsafe.PARENT, rparam, Node.env(
							parent
						), Node.env(
							Node.call(Unsafe.ASSIGN, rparam, genRoot)
						)))
					)))
				))))
			))
		)));
		
		return Node.call(Unsafe.FUNCTION, proot, Node.env(block));
	}
	
	public ResolverVirtual insert (Resolver r) {
		Resolver[] thispairs = this.getResolvers();
		
		for (int i = 0; i < thispairs.length; i++) {
			Resolver pair = thispairs[i];
			
			if (pair.getName().equals(r.getName())) {
				Resolver[] pairs = new Resolver[thispairs.length];
				System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
				pairs[i] = ((ResolverVirtual) pair).insert(r);
				
				return new ResolverVirtual(this.getName(), pairs);
			}
		}
		
		Resolver[] pairs = new Resolver[thispairs.length + 1];
		System.arraycopy(thispairs, 0, pairs, 0, thispairs.length);
		pairs[thispairs.length] = r;
		return new ResolverVirtual(this.getName(), pairs);
	}
	
	@Override
	public String toString() {
		Resolver[] children = this.getResolvers();
		
		if (children.length == 0) return "";
		
		StringBuilder b = new StringBuilder();
		
		b.append(this.getName()).append('\n');
		
		for (int i = 0; i < children.length; i++) {
			Resolver entry = children[i];
			
			boolean last = i == children.length - 1;
			
			if (last) b.append('\u2514');
			else b.append('\u251C');
			
			//b.append(' ').append(entry.name).append('\n');
			//b.append(last ? "  " : "\u2502 ");
			
			String val = entry.toString();
			
			for (int ii = 0; ii < val.length(); ii++) {
				char c = val.charAt(ii);
				
				if (c == '\n') {
					b.append(last ? "\n  " : "\n\u2502 ");
				}else {
					b.append(c);
				}
			}
			
			b.append("\n");
		}
		
		return b.substring(0, b.length() - 1);
	}
	
	private static class IdentifierResolver extends Resolver {
		public final NodeIdentifier uniqueIdentifier = Node.id();
		public final NodeIdentifier identifier;
		public final Resolver parent;
		
		public IdentifierResolver(Resolver parent) {
			super(parent.getName());
			this.parent = parent;
			
			this.identifier = Node.id(parent.getName());
		}
		
		@Override
		public Node createNode() {
			return this.parent.createNode();
		}
	}
}
