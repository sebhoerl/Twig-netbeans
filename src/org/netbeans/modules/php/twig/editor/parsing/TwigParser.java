package org.netbeans.modules.php.twig.editor.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.swing.event.ChangeListener;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.php.twig.editor.lexer.TwigTokenId;

public class TwigParser extends Parser {

    Snapshot snapshot;
    TwigParserResult result;
    
    final static List<String> parseElements = new ArrayList<String>();
    static {
        parseElements.add( "for" );
        parseElements.add( "endfor" );
        
        parseElements.add( "if" );
        parseElements.add( "elseif" );
        parseElements.add( "else" );
        parseElements.add( "endif" );
        
        parseElements.add( "block" );
        parseElements.add( "endblock" );
        
        parseElements.add( "set" );
        parseElements.add( "endset" );

        parseElements.add( "macro" );
        parseElements.add( "endmacro" );
        
        parseElements.add( "filter" );
        parseElements.add( "endfilter" );
        
        parseElements.add( "autoescape" );
        parseElements.add( "endautoescape" );
        
        parseElements.add( "spaceless" );
        parseElements.add( "endspaceless" );
        
    }
    
    @Override
    public void parse( Snapshot snapshot, Task task, SourceModificationEvent sme ) throws ParseException {
        this.snapshot = snapshot;
        result = new TwigParserResult( snapshot );
        
        TokenHierarchy<?> tokenHierarchy = snapshot.getTokenHierarchy();
        
        LanguagePath twigPath = null;
        
        for ( LanguagePath path : tokenHierarchy.languagePaths() ) {
            
            if ( path.mimePath().endsWith( "twig-markup" ) ) { twigPath = path; break; }
            
        }
        
        if ( twigPath != null ) {
            
            List<TokenSequence<?>> tokenSequenceList = tokenHierarchy.tokenSequenceList( twigPath, 0, Integer.MAX_VALUE );
            List<Instruction> instructionList = new ArrayList<Instruction>();
            
            for ( TokenSequence<?> sequence : tokenSequenceList ) {
                
                while ( sequence.moveNext() ) {
                    
                    Token<TwigTokenId> token = (Token<TwigTokenId>) sequence.token();
                    
                    /* Parse instruction */
                    
                    if ( token.id() == TwigTokenId.T_TWIG_INSTRUCTION ) {
                        
                        Instruction instruction = new Instruction();
                        instruction.startTokenIndex = sequence.index();
                        instruction.endTokenIndex = sequence.index();
                        instruction.from = token.offset(tokenHierarchy);
                        
                        while ( sequence.moveNext() ) {
                            
                            token = (Token<TwigTokenId>) sequence.token();
                            if ( token.id() == TwigTokenId.T_TWIG_NAME ) {
                                instruction.extra = token.text();
                            }
                            if ( token.id() == TwigTokenId.T_TWIG_INSTRUCTION ) {
                                instruction.endTokenIndex = sequence.index();
                                instruction.length = token.offset(tokenHierarchy) - instruction.from + token.length();
                                break;
                            }
                            
                        }
                        
                        if ( instruction.startTokenIndex != instruction.endTokenIndex ) { // Closed instruction found
                            
                            sequence.moveIndex( instruction.startTokenIndex );
                            
                            while ( sequence.moveNext() ) {
                                
                                token = (Token<TwigTokenId>) sequence.token();
                                if ( token.id() == TwigTokenId.T_TWIG_FUNCTION ) {
                                    
                                    instruction.function = token.text();
                                    instruction.functionTokenIndex = sequence.index();
                                    instruction.functionFrom = token.offset(tokenHierarchy);
                                    instruction.functionLength = token.length();
                                    break;
                                    
                                }
                                
                            }
                            
                            if ( parseElements.contains( instruction.function.toString() ) ) 
                            {
                                /* Have we captured a standalone instruction? */
                                if ( CharSequenceUtilities.equals( instruction.function, "block" ) ) {
                                    
                                    boolean standalone = false;
                                    int names = 0;
                                    
                                    do {
                                        
                                        sequence.moveNext();
                                        token = (Token<TwigTokenId>)sequence.token();
                                        
                                        if ( token.id() == TwigTokenId.T_TWIG_NAME || token.id() == TwigTokenId.T_TWIG_STRING ) {
                                            names++;
                                        }
                                        
                                        if ( names > 1 ) {
                                            standalone = true;
                                            break;
                                        }
                                        
                                    } while ( sequence.index() < instruction.endTokenIndex );
                                    
                                    if ( !standalone ) instructionList.add( instruction );
                                    else { // add a inline "block" immediately to the result set
                                        result.addBlock( "*inline-block", instruction.from, instruction.length, instruction.extra );
                                    }
                                    
                                } else if ( CharSequenceUtilities.equals( instruction.function, "set" ) ) {
                                    
                                    boolean standalone = false;
                                    
                                    do {
                                        
                                        sequence.moveNext();
                                        token = (Token<TwigTokenId>)sequence.token();
                                        
                                        if ( token.id() == TwigTokenId.T_TWIG_OPERATOR ) {
                                            standalone = true;
                                            break;
                                        }
                                        
                                    } while ( sequence.index() < instruction.endTokenIndex );
                                    
                                    if ( !standalone ) instructionList.add( instruction );
                                    
                                } else {
                                    instructionList.add( instruction );
                                }
                                
                            }
                            
                            sequence.moveIndex( instruction.endTokenIndex );
                            
                        }
                        
                    }
                    
                }
                
            } // endfor: All instructions are now saved in instructionList
            
            /* Analyse instruction structure */
            
            Stack<Instruction> instructionStack = new Stack<Instruction>();
            
            for ( Instruction instruction : instructionList ) {
                
                if ( CharSequenceUtilities.startsWith( instruction.function, "end" ) ) {
                    
                    if ( instructionStack.empty() ) { // End tag, but no more tokens on stack!
                        
                        result.addError( 
                            "Unopened '" + instruction.function + "' block",
                            instruction.functionFrom,
                            instruction.functionLength
                        );

                    } else if ( CharSequenceUtilities.endsWith( instruction.function, instructionStack.peek().function ) ) {
                        // end[sth] found a [sth] on the stack!
                        
                        Instruction start = instructionStack.pop();
                        result.addBlock( start.function, start.from, instruction.from - start.from + instruction.length, start.extra );
                        
                    } else {
                        // something wrong lies on the stack!
                        // assume that current token is invalid and let it stay on the stack

                        result.addError( 
                            "Unexpected '" + instruction.function + "', expected 'end" + instructionStack.peek().function + "'",
                            instruction.functionFrom,
                            instruction.functionLength
                        );
                        
                    }
                    
                } else {
                    instructionStack.push( instruction );
                }
                
            }
            
            // All instructions were parsed. Are there any left on the stack?
            if ( !instructionStack.empty() ) {
                // Yep, they were never closed!
                
                while ( !instructionStack.empty() ) {
                    
                    Instruction instruction = instructionStack.pop();
                    
                    result.addError( 
                        "Unclosed '" + instruction.function + "'",
                        instruction.functionFrom,
                        instruction.functionLength
                    );
                    
                }
                
            }
            
            // Parsing done!
            
        }
        
    }

    @Override
    public Result getResult(Task task) throws ParseException {
        return result;
    }
    
    @Override
    public void addChangeListener(ChangeListener cl) {}

    @Override
    public void removeChangeListener(ChangeListener cl) {}
    
    static public class Factory extends ParserFactory {

        @Override
        public Parser createParser( Collection<Snapshot> clctn ) {
            return new TwigParser();
        }
        
    }
    
    class Instruction {
        
        CharSequence function = null;
        CharSequence extra = null;
        int startTokenIndex = 0;
        int endTokenIndex = 0;
        int functionTokenIndex = 0;
        
        int from = 0;
        int length = 0;
        
        int functionFrom = 0;
        int functionLength = 0;
        
    }
    
}
