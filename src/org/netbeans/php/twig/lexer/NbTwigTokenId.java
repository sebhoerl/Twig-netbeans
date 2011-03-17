/**
 * Twig plugin for Netbeans.
 *
 * Copyright (c) 2011 Sebastian Hörl
 *
 * For warranty and licensing information, view the LICENSE file.
 */

package org.netbeans.php.twig.lexer;

import org.netbeans.api.lexer.*;

/**
 * "Converts" the custom tokens of the TwigLexer into
 * token ids for the Netbeans API.
 *
 * @author Sebastian Hörl
 */
public class NbTwigTokenId implements TokenId {

    protected String name;
    protected int ordinal;
    protected String category;
    public TwigToken.Type type;

    public NbTwigTokenId( TwigToken.Type type ) {
        this.name = TwigToken.typeToString( type );
        this.ordinal = TwigToken.typeToOrdinal( type );
        this.category = TwigToken.typeToString( type );
        this.type = type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public String primaryCategory() {
        return this.category;
    }

    private static final Language<NbTwigTokenId> language = new NbTwigLanguageHierarchy().language();

    public static Language<NbTwigTokenId> getLanguage() {
        return language;
    }

}
