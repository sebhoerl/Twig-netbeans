/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.php.twig.lexer;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author sebastian
 */
public class TwigLanguageHierarchy extends LanguageHierarchy<TwigTokenId> {

    protected List<TwigTokenId> tokenList;
    protected Map<String,TwigTokenId> tokenMap;

    TwigLanguageHierarchy() {

        tokenList = new LinkedList<TwigTokenId>();
        tokenMap = new HashMap<String,TwigTokenId>();

        tokenList.add( new TwigTokenId( "DEFAULT", "default", 1 ) );

        tokenList.add( new TwigTokenId( "OPEN_INSTRUCTION", "markup", 2 ) );
        tokenList.add( new TwigTokenId( "CLOSE_INSTRUCTION", "markup", 3 ) );

        tokenList.add( new TwigTokenId( "OPEN_OUTPUT", "markup", 4 ) );
        tokenList.add( new TwigTokenId( "CLOSE_OUTPUT", "markup", 5 ) );

        tokenList.add( new TwigTokenId( "OPEN_COMMENT", "comment", 6 ) );
        tokenList.add( new TwigTokenId( "CLOSE_COMMENT", "comment", 7 ) );

        tokenList.add( new TwigTokenId( "INSIDE", "expression", 8 ) );

        for ( TwigTokenId token : tokenList )
            tokenMap.put( token.name(), token );
        
    }

    @Override
    protected Collection<TwigTokenId> createTokenIds() {
        return tokenList;
    }

    public TwigTokenId getToken( String name ) {
        return tokenMap.get( name );
    }

    @Override
    protected Lexer<TwigTokenId> createLexer(LexerRestartInfo<TwigTokenId> lri) {
        return new TwigLexer( lri, this );
    }

    @Override
    protected String mimeType() {
        return "text/twig";
    }



}
