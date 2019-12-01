package parser;

import java.io.File;
import java.nio.file.Files;

import node.Node;
import parser.resolver.ResolverCompound;
import parser.resolver.ResolverFile;
import parser.resolver.ResolverPackage;
import parser.resolver.ResolverPath;
import parser.resolver.ResolverScope;
import parser.resolver.ResolverUnsafe;
import parser.token.Token;
import parser.token.TokenScope;
import value.Value;

public class Packages {
	public static final boolean PRINT_AST = false;
	public static final boolean PRINT_EVENTS = false;
	
	public static File root;
	
	public static void main(String[] args) {
		root = new File(args[0]).getParentFile();
		Packages.file(args[0]);
	}
	
	public static Value load (Stream s, Value resolver, String name) {
		Token ast;
		
		try {
			//when trying to run an .ace file as a bash file, the bash interpreter
			//will look for a string starting with #! to signify what program to use to run this file
			//this lets you run a .ace file as a regular bash script as long as it starts with '#!'
			//followed by the path to the compiler/interpreter
			if (s.isNext("#!")) while (s.hasChr() && !s.next('\n')) s.chr();
			
			ast = TokenScope.createBase(s);
		}catch (ParserException e) {
			System.out.println(name + ":" + (s.getLine() + 1) + ":" + (s.getCol() + 1) + ": " + e.getMessage());
			
			throw e;
		}
		
		if (PRINT_AST) {
			System.out.println(ast);
			
			return null;
		}else {
			Node event = ast.createEvent();
			
			if (PRINT_EVENTS) {
				System.out.println(event);
				return null;
			}else {
				return event.run(resolver);
			}
		}
	}
	
	public static Value file (String path) {
		try {
			return Packages.load(new Stream(Files.readAllBytes(new File(path).toPath())), new ResolverCompound(
				new ResolverScope(),
				new ResolverPath (new ResolverUnsafe(), "unsafe"),
				new ResolverPackage("ace"),
				new ResolverFile(Packages.root),
				new ResolverFile(new File(path).getParentFile())
			), path);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
