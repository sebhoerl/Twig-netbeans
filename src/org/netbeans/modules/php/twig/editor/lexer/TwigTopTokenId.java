/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum TwigTopTokenId implements TokenId {

    T_HTML( null, "twig_html" ),
    T_ERROR( null, "twig_error" ),
    T_TWIG( null, "twig" ),
    T_TWIG_RAW( null, "twig_raw" );
    
    private String fixedText;
    private String primaryCategory;

    TwigTopTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }
    private static final Language<TwigTopTokenId> language =
            new LanguageHierarchy<TwigTopTokenId>() {

                @Override
                protected Collection<TwigTopTokenId> createTokenIds() {
                    return EnumSet.allOf( TwigTopTokenId.class );
                }

                @Override
                protected Map<String, Collection<TwigTopTokenId>> createTokenCategories() {
                    Map<String, Collection<TwigTopTokenId>> cats = new HashMap<String, Collection<TwigTopTokenId>>();
                    return cats;
                }

                @Override
                protected Lexer<TwigTopTokenId> createLexer( LexerRestartInfo<TwigTopTokenId> info ) {
                    return TwigTopLexer.create( info );
                }

                @Override
                protected String mimeType() {
                    return "text/twig";
                }

                @Override
                protected LanguageEmbedding<?> embedding( Token<TwigTopTokenId> token,
                        LanguagePath languagePath, InputAttributes inputAttributes ) {
                    
                    TwigTopTokenId id = token.id();
                    if ( id == T_HTML || id == T_TWIG_RAW ) {
                        return LanguageEmbedding.create( HTMLTokenId.language(), 0, 0, true );
                    } else if ( id == T_TWIG ) {
                        return LanguageEmbedding.create( TwigTokenId.language(), 0, 0 );
                    }

                    return null;
                    
                }
            }.language();

    public static Language<TwigTopTokenId> language() {
        return language;
    }
}