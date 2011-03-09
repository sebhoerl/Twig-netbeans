package org.netbeans.php.twig.lexer;

import java.util.*;
import org.netbeans.spi.lexer.*;

public class NbTwigLanguageHierarchy extends LanguageHierarchy<NbTwigTokenId> {

    protected ArrayList<NbTwigTokenId> tokenList = new ArrayList();
    protected HashMap<TwigToken.Type,NbTwigTokenId> tokenMap = new HashMap();
    protected TwigLexer lexer = new TwigLexer();

    NbTwigLanguageHierarchy() {

        TwigToken.Type[] types = TwigToken.getTypes();

        for ( TwigToken.Type item : types ) {
            NbTwigTokenId id = new NbTwigTokenId( item );
            tokenList.add( id );
            tokenMap.put( item, id );
        }

    }

    public NbTwigTokenId getToken( TwigToken.Type type ) {
        return tokenMap.get( type );
    }

    @Override
    protected Collection<NbTwigTokenId> createTokenIds() {
        return tokenList;
    }

    @Override
    protected Lexer<NbTwigTokenId> createLexer(LexerRestartInfo<NbTwigTokenId> lri) {
        return new NbTwigLexer( lri, this );
    }

    public TwigLexer getLexer() { return lexer; }

    @Override
    protected String mimeType() {
        return "text/twig";
    }

}
