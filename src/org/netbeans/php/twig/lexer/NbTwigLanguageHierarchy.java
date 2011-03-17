/**
 * Twig plugin for Netbeans.
 *
 * Copyright (c) 2011 Sebastian Hörl
 *
 * For warranty and licensing information, view the LICENSE file.
 */

package org.netbeans.php.twig.lexer;

import java.util.*;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.*;
import org.netbeans.api.html.lexer.HTMLTokenId;

/**
 * Plugin entry point for the code highlighting stuff. Maps
 * TwigToken.Type values to NbTwigToken instances for the Netbeans API.
 *
 * @author Sebastian Hörl
 */
public class NbTwigLanguageHierarchy extends LanguageHierarchy<NbTwigTokenId> {

    protected ArrayList<NbTwigTokenId> tokenList = new ArrayList();
    protected HashMap<TwigToken.Type,NbTwigTokenId> tokenMap = new HashMap();

    NbTwigLanguageHierarchy() {

        TwigToken.Type[] types = TwigToken.getTypes();

        for ( TwigToken.Type item : types ) {
            NbTwigTokenId id = new NbTwigTokenId( item );
            tokenList.add( id );
            tokenMap.put( item, id );
        }

    }

    @Override
    protected LanguageEmbedding<?> embedding( Token<NbTwigTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
        NbTwigTokenId id = token.id();

        if ( id.type == TwigToken.Type.TEXT ) {

            return LanguageEmbedding.create( HTMLTokenId.language(), 0, 0, true );

        }

        return null;
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

    @Override
    protected String mimeType() {
        return "text/twig";
    }

}
