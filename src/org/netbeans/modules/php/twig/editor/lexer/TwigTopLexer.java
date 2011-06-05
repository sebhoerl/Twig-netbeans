/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

public class TwigTopLexer implements Lexer<TwigTopTokenId> {

    protected TwigTopLexerState state;
    protected final TokenFactory<TwigTopTokenId> tokenFactory;
    protected final LexerInput input;
    
    static String OPEN_INSTRUCTION = "{%";
    static String OPEN_VARIABLE = "{{";
    static String OPEN_COMMENT = "{#";

    static String CLOSE_INSTRUCTION = "%}";
    static String CLOSE_VARIABLE = "}}";
    static String CLOSE_COMMENT = "#}";

    static Pattern START_RAW = Pattern.compile( "^\\{%[\\s]raw" );
    static Pattern END_RAW = Pattern.compile( "\\{%[\\s]*endraw[\\s]*%\\}$" );
    
    private TwigTopLexer( LexerRestartInfo<TwigTopTokenId> info ) {
        
        tokenFactory = info.tokenFactory();
        input = info.input();
        state = info.state() == null ? new TwigTopLexerState() : new TwigTopLexerState( (TwigTopLexerState)info.state() );

    }
    
    public static synchronized TwigTopLexer create( LexerRestartInfo<TwigTopTokenId> info ) {
        return new TwigTopLexer( info );
    }    
    
    @Override
    public Token<TwigTopTokenId> nextToken() {
        
        TwigTopTokenId tokenId = findNextToken();
        return tokenId == null ? null : tokenFactory.createToken( tokenId );
        
    }
    
    @Override
    public Object state() {
        return new TwigTopLexerState( state );
    }

    @Override
    public void release() {
    }
    
    TwigTopLexerState.Type findTag( CharSequence text, boolean open ) {

        if ( open && CharSequenceUtilities.endsWith( text, OPEN_INSTRUCTION ) ) 
            return TwigTopLexerState.Type.INSTRUCTION;
        if ( !open && CharSequenceUtilities.endsWith( text, CLOSE_INSTRUCTION ) ) 
            return TwigTopLexerState.Type.INSTRUCTION;

        if ( open && CharSequenceUtilities.endsWith( text, OPEN_VARIABLE ) ) 
            return TwigTopLexerState.Type.VARIABLE;
        if ( !open && CharSequenceUtilities.endsWith( text, CLOSE_VARIABLE ) ) 
            return TwigTopLexerState.Type.VARIABLE;

        if ( open && CharSequenceUtilities.endsWith( text, OPEN_COMMENT ) ) 
            return TwigTopLexerState.Type.COMMENT;
        if ( !open && CharSequenceUtilities.endsWith( text, CLOSE_COMMENT ) ) 
            return TwigTopLexerState.Type.COMMENT;

        return TwigTopLexerState.Type.NONE;

    }
    
    public TwigTopTokenId findNextToken() {
        int c = input.read();
        TwigTopLexerState.Type result;

        if ( c == LexerInput.EOF ) return null;

        while ( c != LexerInput.EOF ) {

            CharSequence text = input.readText();

            switch ( state.main ) {
                case RAW:
                    if ( CharSequenceUtilities.endsWith( text, "%}" ) ) {
                        Matcher matcher = END_RAW.matcher( text );
                        if ( matcher.find() ) {
                            String captured = matcher.group();
                            state.main = TwigTopLexerState.Main.OPEN;
                            state.type = TwigTopLexerState.Type.INSTRUCTION;
                            if ( text.length() - captured.length() > 0 ) {
                                input.backup( captured.length() );
                                return TwigTopTokenId.T_TWIG_RAW;
                            }
                        }
                    }
                    break;
                case INIT:
                case HTML:
                    result = findTag( text, true );
                    if ( result != TwigTopLexerState.Type.NONE ) {
                        state.main = TwigTopLexerState.Main.OPEN;
                        state.type = result;
                        if ( input.readLength() > 2 ) {
                            input.backup( 2 );
                            return TwigTopTokenId.T_HTML;
                        }
                    } else break;
                case OPEN:
                    if ( input.readLength() == 2 ) {
                        state.main = TwigTopLexerState.Main.TWIG;
                    } break;
                case TWIG:
                    result = findTag( text, false );
                    if ( result != TwigTopLexerState.Type.NONE ) {
                        if ( result == state.type ) {

                            boolean escape = false;
                            boolean doubleQuotes = false;
                            boolean singleQuotes = false;

                            if ( result != TwigTopLexerState.Type.COMMENT ) {

                                for ( int i = 0; i < text.length() - 2; i++ ) {
                                    char q = text.charAt( i );
                                    if ( q == '\\' ) escape = true;
                                    else if ( !escape ) {
                                        if ( q == '"' && !singleQuotes ) doubleQuotes = !doubleQuotes;
                                        else if ( q == '\'' && !doubleQuotes ) singleQuotes = !singleQuotes;
                                    } else escape = false;
                                }

                            }

                            if ( singleQuotes || doubleQuotes ) break;

                            if ( result == TwigTopLexerState.Type.INSTRUCTION && START_RAW.matcher( text ).find() ) {
                                state.main = TwigTopLexerState.Main.CLOSE_RAW;
                            } else {
                                state.main = TwigTopLexerState.Main.CLOSE;
                            }

                            if ( input.readLength() > 2 ) {
                                input.backup( 2 );
                            }
                        } break;
                    } break;
                case CLOSE_RAW:
                case CLOSE:
                    if ( 
                      ( state.type == TwigTopLexerState.Type.INSTRUCTION && CharSequenceUtilities.endsWith( text, CLOSE_INSTRUCTION ) ) ||
                      ( state.type == TwigTopLexerState.Type.VARIABLE && CharSequenceUtilities.endsWith( text, CLOSE_VARIABLE ) ) ||      
                      ( state.type == TwigTopLexerState.Type.COMMENT && CharSequenceUtilities.endsWith( text, CLOSE_COMMENT ) )      
                    ) {
                        state.main = (state.main == TwigTopLexerState.Main.CLOSE) ? TwigTopLexerState.Main.HTML : TwigTopLexerState.Main.RAW;
                        return TwigTopTokenId.T_TWIG;
                    } break;

            }

            c = input.read();

        }
        
        switch ( state.main ) {
            case RAW: return TwigTopTokenId.T_TWIG_RAW;
            case TWIG: return TwigTopTokenId.T_TWIG;
            case HTML: return TwigTopTokenId.T_HTML;
        }
        
        return TwigTopTokenId.T_HTML;

    }
    
}