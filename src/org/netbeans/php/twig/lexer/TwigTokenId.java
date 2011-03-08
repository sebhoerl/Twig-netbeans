/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.php.twig.lexer;

import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.Language;

/**
 *
 * @author sebastian
 */
public class TwigTokenId implements TokenId {

    protected String name;
    protected int ordinal;
    protected String category;

    public TwigTokenId( String name, String category, int ordinal ) {
        this.name = name;
        this.ordinal = ordinal;
        this.category = category;
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

    private static final Language<TwigTokenId> language = new TwigLanguageHierarchy().language();

    public static Language<TwigTokenId> getLanguage() {
        return language;
    }

}
