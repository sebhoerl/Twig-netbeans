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
        UNKNOWN,
        OUTSIDE,
        INSIDE
    };

    State currentState;

    public TwigLexer( LexerRestartInfo<TwigTokenId> info, TwigLanguageHierarchy hierarchy )
    {

        this.info = info;
        this.hierarchy = hierarchy;

        if ( info.state() == null ) {
            currentState = State.OUTSIDE;
        } else {
            currentState = (State)info.state();
        }

    }

    @Override
    public Token<TwigTokenId> nextToken() {

        LexerInput input = info.input();

        while ( input.read() != LexerInput.EOF )
        {

            String read = input.readText().toString();

            // Instruction starts here
            if ( read.length() == 2 && read.equals( "{%" ) ) {
                currentState = State.INSIDE;
                return info.tokenFactory().createToken( hierarchy.getToken( "OPEN_INSTRUCTION" ) );
            }

            // Instruction ends here
            if ( read.length() == 2 && read.equals( "%}" ) ) {
                currentState = State.OUTSIDE;
                return info.tokenFactory().createToken( hierarchy.getToken( "CLOSE_INSTRUCTION" ) );
            }

            if ( read.length() == 2 && read.equals( "{{" ) ) {
                currentState = State.INSIDE;
                return info.tokenFactory().createToken( hierarchy.getToken( "OPEN_OUTPUT" ) );
            }

            if ( read.length() == 2 && read.equals( "}}" ) ) {
                currentState = State.OUTSIDE;
                return info.tokenFactory().createToken( hierarchy.getToken( "CLOSE_OUTPUT" ) );
            }

            // Normal area
            if ( read.length() > 1 ) {
                String sub = read.substring( read.length() - 2 );
                if ( sub.equals( "{%" ) || sub.equals( "%}" ) || sub.equals( "}}" ) || sub.equals( "{{" ) ) {
                    input.backup( 2 );
                    return info.tokenFactory().createToken( hierarchy.getToken( (currentState == State.INSIDE) ? "INSIDE" : "DEFAULT" ) );
                }
            }

        }

        if ( input.readLength() < 1 ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( (currentState == State.INSIDE) ? "INSIDE" : "DEFAULT"  ) );

    }

    @Override
    public Object state() {
        return currentState;
    }

    @Override
    public void release() {}

}
