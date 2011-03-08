/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.php.twig.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.TokenFactory;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author sebastian
 */
public class TwigLexer implements Lexer<TwigTokenId> {

    protected LexerRestartInfo<TwigTokenId> info;
    protected TwigLanguageHierarchy hierarchy;

    enum State {
        DATA,
        BLOCK,
        VAR,
        COMMENT
    };

    State currentState;

    public TwigLexer( LexerRestartInfo<TwigTokenId> info, TwigLanguageHierarchy hierarchy )
    {

        this.info = info;
        this.hierarchy = hierarchy;

        if ( info.state() == null ) {
            currentState = State.DATA;
        } else {
            currentState = (State)info.state();
        }

    }

    public Token<TwigTokenId> lexData( LexerInput input ) {

        String chunk = "";

        while ( input.read() != LexerInput.EOF ) {

            chunk = input.readText().toString();

            // end data
            if (
                ( chunk.endsWith( "{%" ) ||
                chunk.endsWith( "{{" ) ||
                chunk.endsWith( "{#" ) ) &&
                chunk.length() > 2
            ) {
                input.backup( 2 );
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_TEXT" ) );
            }

            // start block
            if ( chunk.startsWith( "{%" ) ) {
                currentState = State.BLOCK;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_BLOCK_START" ) );
            }

            // start var
            if ( chunk.startsWith( "{{" ) ) {
                currentState = State.VAR;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_VAR_START" ) );
            }

            // start comment
            if ( chunk.toString().startsWith( "{#" ) ) {
                currentState = State.COMMENT;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_COMMENT_START" ) );
            }

        }

        if ( input.readLength() < 1 ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_TEXT" ) );

    }

    public Token<TwigTokenId> lexVar( LexerInput input ) {

        String chunk = "";

        while ( input.read() != LexerInput.EOF ) {

            chunk = input.readText().toString();

            // end var
            if ( chunk.endsWith( "}}" ) && chunk.length() > 2 ) {
                input.backup( 2 );
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_NAME" ) );
            }

            // start data
            if ( chunk.startsWith( "}}" ) ) {
                currentState = State.DATA;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_VAR_END" ) );
            }

        }

        if ( input.readLength() < 1 ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_NAME" ) );

    }

    public Token<TwigTokenId> lexComment( LexerInput input ) {

        String chunk = "";

        while ( input.read() != LexerInput.EOF ) {

            chunk = input.readText().toString();

            // end comment
            if ( chunk.endsWith( "#}" ) && chunk.length() > 2 ) {
                input.backup( 2 );
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_COMMENT" ) );
            }

            // start data
            if ( chunk.startsWith( "#}" ) ) {
                currentState = State.DATA;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_COMMENT_END" ) );
            }

        }

        if ( input.readLength() < 1 ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_COMMENT" ) );

    }

    public Token<TwigTokenId> lexBlock( LexerInput input ) {

        String chunk = "";

        while ( input.read() != LexerInput.EOF ) {

            chunk = input.readText().toString();

            // end block
            if ( chunk.endsWith( "%}" ) && chunk.length() > 2 ) {
                input.backup( 2 );
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_OPERATOR" ) );
            }

            // start data
            if ( chunk.startsWith( "%}" ) ) {
                currentState = State.DATA;
                return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_BLOCK_END" ) );
            }

        }

        if ( input.readLength() < 1 ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( "TWIG_OPERATOR" ) );

    }

    @Override
    public Token<TwigTokenId> nextToken() {

        LexerInput input = info.input();

        switch ( currentState ) {

            case BLOCK: return lexBlock( input );
            case COMMENT: return lexComment( input );
            case VAR: return lexVar( input );

        }

        return lexData( input );
        
    }

    @Override
    public Object state() {
        return currentState;
    }

    @Override
    public void release() {}

}
