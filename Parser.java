import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;
import java.util.*;

public class Parser implements IParser {

  //method to check if a word is in the context free grammar
  public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {
    //retuns if a parse tree can be generated from the word
    return generateParseTree(cfg, w) != null;
  }

  //method to generate a parse tree from a word, by getting the first variable and then attempting to derive the word from it
  //starts at initial depth of 0, and max depth of 2 * word length 
  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
    Variable startSymbol = cfg.getStartVariable();
    // Attempt to derive the word starting from the start symbol
    ParseTreeNode tree = deriveWord(cfg, startSymbol, w, 0, 2 * w.length());
    return tree;
  }
  //method to try and derive word using recursion. It stops if it exceeds max depth to stop it running infinity
  public ParseTreeNode deriveWord(ContextFreeGrammar cfg, Symbol symbol, Word target, int depth, int maxDepth) {
    if (depth > maxDepth) return null;

    //Method to handle matching on first symbol
    if (symbol instanceof Terminal) {
      if (target.length() == 1 && target.get(0).equals(symbol)) {
        return new ParseTreeNode(symbol);
      }
      return null;
    }

    // Recursive case: try to expand non-terminals
    if (symbol instanceof Variable) {
      for (Rule rule : cfg.getRules()) {
        if (rule.getVariable().equals(symbol)) {
          Word expansion = rule.getExpansion();
          List<ParseTreeNode> children = new ArrayList<>();

          if (expansion.length() == 1) {
            // Single symbol - try to derive the word from it
            ParseTreeNode child = deriveWord(cfg, expansion.get(0), target, depth + 1, maxDepth);
            if (child != null) children.add(child);
          } else if (expansion.length() == 2) {
            // Double symbol - split the word and derive on both sides
            for (int split = 1; split < target.length(); split++) {
              ParseTreeNode leftChild = deriveWord(cfg, expansion.get(0), target.subword(0, split), depth + 1, maxDepth);
              ParseTreeNode rightChild = deriveWord(cfg, expansion.get(1), target.subword(split, target.length()), depth + 1, maxDepth);

              //add chidren to list and break if we find a valid answer
              if (leftChild != null && rightChild != null) {
                children.add(leftChild);
                children.add(rightChild);
                break; //
              }
            }
          }

          //constructrs parse tree with appropriate children
          if (!children.isEmpty()) {
            if (children.size() == 1) return new ParseTreeNode(symbol, children.get(0));
            if (children.size() == 2) return new ParseTreeNode(symbol, children.get(0), children.get(1));
          }
        }
      }
    }

    return null;
  }
}
