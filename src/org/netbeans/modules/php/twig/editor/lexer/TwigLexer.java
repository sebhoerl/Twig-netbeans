/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

public class TwigLexer implements Lexer<TwigTokenId> {

    protected TwigLexerState state;
    protected final TokenFactory<TwigTokenId> tokenFactory;
    protected final LexerInput input;
    
    private TwigLexer( LexerRestartInfo<TwigTokenId> info ) {
        
        tokenFactory = info.tokenFactory();
        input = info.input();
        state = info.state() == null ? new TwigLexerState() : new TwigLexerState( (TwigLexerState)info.state() );
        
        initialize();

    }
    
    public static synchronized TwigLexer create( LexerRestartInfo<TwigTokenId> info ) {
        return new TwigLexer( info );
    }    
    
    @Override
    public Token<TwigTokenId> nextToken() {
        
        TwigTokenId tokenId = findNextToken();
        return tokenId == null ? null : tokenFactory.createToken( tokenId );
        
    }
    
    @Override
    public Object state() {
        return new TwigLexerState( state );
    }

    @Override
    public void release() {
    }
    
    static protected String INSTRUCTION_START = "{%";
    static protected String COMMENT_START = "{#";
    static protected String VARIABLE_START = "{{";
    
    static protected String INSTRUCTION_END = "{%";
    static protected String COMMENT_END = "{#";
    static protected String VARIABLE_END = "{{";
    
    static protected String PUNCTUATION = "|()[]{}?:.,";
    
    static protected Pattern REGEX_ALPHANUM_END = Pattern.compile( "[A-Za-z0-9]$" );
    static protected Pattern REGEX_WHITESPACE_END = Pattern.compile( "[\\s]+$" );
    protected Pattern REGEX_OPERATOR = null;
    
    int OPERATOR_LENGTH = 0;
    
    final static List<String> OPERATORS = new ArrayList<String>();
    static {
        
        OPERATORS.add( "import" );
        OPERATORS.add( "from" );
        OPERATORS.add( "as" );
        OPERATORS.add( "=" );
        OPERATORS.add( "not" );
        OPERATORS.add( "+" );
        OPERATORS.add( "-" );
        OPERATORS.add( "or" );
        OPERATORS.add( "and" );
        OPERATORS.add( "==" );
        OPERATORS.add( "!=" );
        OPERATORS.add( ">" );
        OPERATORS.add( "<" );
        OPERATORS.add( ">=" );
        OPERATORS.add( "<=" );
        OPERATORS.add( "not in" );
        OPERATORS.add( "in" );
        OPERATORS.add( "~" );
        OPERATORS.add( "*" );
        OPERATORS.add( "/" );
        OPERATORS.add( "//" );
        OPERATORS.add( "%" );
        OPERATORS.add( "is" );
        OPERATORS.add( "is not" );
        OPERATORS.add( ".." );
        OPERATORS.add( "**" );
        
    };
    
    protected class SortOperators implements Comparator<String> {
        @Override
        public int compare( String a, String b ) {
            return a.length() - b.length();
        }
    }
    
    protected String implode( List<String> list, String delimeter ) {
        String s = "";
        boolean first = true;
        for ( String item : list ) {
            if ( !first ) s += delimeter;
            s += item;
            first = false;
        }
        return s;
    }
    
    protected void initialize() {
        
        Collections.sort( OPERATORS, new SortOperators() );
        Collections.reverse( OPERATORS );
        
        ArrayList<String> regex = new ArrayList<String>();
        
        for ( String operator : OPERATORS ) {
            
            if ( REGEX_ALPHANUM_END.matcher( operator ).find() ) {
                regex.add( Pattern.quote( operator ) + "[ ()]" );
                if ( operator.length() + 1 > OPERATOR_LENGTH ) OPERATOR_LENGTH = operator.length() + 1;
            } else {
                regex.add( Pattern.quote( operator ) );
                if ( operator.length() > OPERATOR_LENGTH ) OPERATOR_LENGTH = operator.length();
            }
            
        }
        
        REGEX_OPERATOR = Pattern.compile( "^" + implode( regex, "|^" ) );
        
    }
    
    public TwigTokenId findNextToken() {
        
        int c = input.read();
        int d = c;
        if ( c == LexerInput.EOF ) return null;
        
        Matcher matcher;
        
        while ( c != LexerInput.EOF ) {

            CharSequence text = input.readText();
            d = c;

            switch ( state.main ) {

                case INIT:
                    if ( CharSequenceUtilities.startsWith( text, COMMENT_START ) ) {
                        state.main = TwigLexerState.Main.COMMENT;
                    } else if ( CharSequenceUtilities.startsWith( text, INSTRUCTION_START ) ) {
                        state.main = TwigLexerState.Main.INSTRUCTION;
                        state.sub = TwigLexerState.Sub.INIT;
                        return TwigTokenId.T_TWIG_INSTRUCTION;
                    } else if ( CharSequenceUtilities.startsWith( text, VARIABLE_START ) ) {
                        state.main = TwigLexerState.Main.VARIABLE;
                        state.sub = TwigLexerState.Sub.INIT;
                        return TwigTokenId.T_TWIG_VARIABLE;
                    }
                    break;
                    
                case VARIABLE:
                case INSTRUCTION:

                    /* Whitespaces */
                    
                    if ( Character.isWhitespace( c ) ) {
                        
                        do {
                            c = input.read();
                        } while ( c != LexerInput.EOF && Character.isWhitespace( c ) );
                        
                        if ( c != LexerInput.EOF ) input.backup( 1 );
                        return TwigTokenId.T_TWIG_WHITESPACE;
                        
                    }
                    
                    /* End markups */
                    
                    if ( c == '%' || c == '}' ) {
                        
                        d = input.read();
                        int e = input.read();
                        
                        if ( d == '}' && e == LexerInput.EOF ) {
                            
                            if ( state.main == TwigLexerState.Main.INSTRUCTION && c == '%' )
                                return TwigTokenId.T_TWIG_INSTRUCTION;
                            
                            if ( state.main == TwigLexerState.Main.VARIABLE && c == '}' )
                                return TwigTokenId.T_TWIG_VARIABLE;
                            
                        }
                        
                        input.backup( 2 );
                        
                    }
                    
                    /* Operators */
                    
                    if ( !( state.main == TwigLexerState.Main.INSTRUCTION && state.sub == TwigLexerState.Sub.INIT ) ) {
                    
                        d = c;
                        
                        int characters = 0;
                        while ( c != LexerInput.EOF && input.readLength() < OPERATOR_LENGTH ) {
                            c = input.read();
                            characters++;
                        }
                        
                        matcher = REGEX_OPERATOR.matcher( input.readText() );
                        if ( matcher.find() ) {

                            String operator = matcher.group();
                            matcher = REGEX_WHITESPACE_END.matcher( operator );

                            if ( matcher.find() ) {

                                input.backup( characters - matcher.start() );
                                return TwigTokenId.T_TWIG_OPERATOR;

                            } else {

                                input.backup( characters - operator.length() + 1 );
                                return TwigTokenId.T_TWIG_OPERATOR;

                            }

                        }

                        input.backup( characters );
                        c = d;
                    
                    } else if ( c == '-' ) { /* Trim operator */
                        return TwigTokenId.T_TWIG_OPERATOR;
                    }
                            
                    /* Names */     
                    
                    if ( Character.isLetter( c ) || c == '_' ) {
                        
                        do {
                            c = input.read();
                        } while ( c != LexerInput.EOF && ( Character.isLetter( c ) || Character.isDigit( c ) || c == '_' ) );
                        
                        if ( c != LexerInput.EOF ) input.backup( 1 );
                        
                        if ( state.main == TwigLexerState.Main.INSTRUCTION && state.sub == TwigLexerState.Sub.INIT ) {
                            state.sub = TwigLexerState.Sub.NONE;
                            return TwigTokenId.T_TWIG_FUNCTION;
                        } else {
                            return TwigTokenId.T_TWIG_NAME;
                        }
                        
                    }
                    
                    /* Numbers */

                    if ( Character.isDigit( c ) ) {
                        
                        boolean dotFound = false;
                        
                        do {
                            if ( c == '.' ) dotFound = true;
                            c = input.read();
                        } while ( c != LexerInput.EOF && ( Character.isDigit( c ) || (!dotFound && c == '.') ) );
                        
                        if ( c != LexerInput.EOF ) input.backup( 1 );
                        return TwigTokenId.T_TWIG_NUMBER;
                        
                    }
                    
                    /* Double quoted strings */
                    
                    if ( c == '"' ) {
                        
                        boolean escaped = false;
                        
                        do {
                            if ( c == '\\' && !escaped ) escaped = true;
                            else escaped = false;
                            c = input.read();
                        } while ( c != LexerInput.EOF && ( escaped || c != '"' ) );
                        
                        return TwigTokenId.T_TWIG_STRING;
                        
                    }
                    
                    /* Single quoted strings */
                    
                    if ( c == '\'' ) {
                        
                        boolean escaped = false;
                        
                        do {
                            if ( c == '\\' && !escaped ) escaped = true;
                            else escaped = false;
                            c = input.read();
                        } while ( c != LexerInput.EOF && ( escaped || c != '\'' ) );
                        
                        return TwigTokenId.T_TWIG_STRING;
                        
                    }
                    
                    /* PUNCTUATION */
                    
                    if ( PUNCTUATION.indexOf( c ) >=0 ) return TwigTokenId.T_TWIG_PUNCTUATION;
                    
                    return TwigTokenId.T_TWIG_OTHER;
                    
            }

            c = input.read();

        }
        
        if ( state.main == TwigLexerState.Main.COMMENT ) return TwigTokenId.T_TWIG_COMMENT;
        return TwigTokenId.T_TWIG_OTHER;

    }
    
}