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

        tokenList.add( new TwigTokenId( "TWIG_TEXT", "text", 1 ) );

        tokenList.add( new TwigTokenId( "TWIG_COMMENT_START", "comment", 2 ) );
        tokenList.add( new TwigTokenId( "TWIG_BLOCK_START", "markup", 3 ) );
        tokenList.add( new TwigTokenId( "TWIG_VAR_START", "markup", 4 ) );

        tokenList.add( new TwigTokenId( "TWIG_COMMENT_END", "comment", 5 ) );
        tokenList.add( new TwigTokenId( "TWIG_BLOCK_END", "markup", 6 ) );
        tokenList.add( new TwigTokenId( "TWIG_VAR_END", "markup", 7 ) );

        tokenList.add( new TwigTokenId( "TWIG_NAME", "identifier", 8 ) );
        tokenList.add( new TwigTokenId( "TWIG_NUMBER", "number", 9 ) );
        tokenList.add( new TwigTokenId( "TWIG_STRING", "string", 10 ) );
        tokenList.add( new TwigTokenId( "TWIG_OPERATOR", "keyword", 11 ) );
        tokenList.add( new TwigTokenId( "TWIG_PUNCTUATION", "keyword", 12 ) );

        tokenList.add( new TwigTokenId( "TWIG_COMMENT", "comment", 13 ) );

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
