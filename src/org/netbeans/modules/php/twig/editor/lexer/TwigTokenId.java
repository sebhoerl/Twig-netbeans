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
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public enum TwigTokenId implements TokenId {

    T_TWIG_NAME( null, "twig_name" ),
    T_TWIG_STRING( null, "twig_string" ),
    T_TWIG_NUMBER( null, "twig_number" ),
    T_TWIG_OPERATOR( null, "twig_operator" ),
    T_TWIG_PUNCTUATION( null, "twig_punctuation" ),
    T_TWIG_WHITESPACE( null, "twig_whitespace" ),
    T_TWIG_FUNCTION( null, "twig_function" ),
    
    T_TWIG_INSTRUCTION( null, "twig_instruction" ),
    T_TWIG_VARIABLE( null, "twig_variable" ),
    T_TWIG_COMMENT( null, "twig_comment" ),
    
    T_TWIG_OTHER( null, "twig_other" )
    ;
    
    private final String fixedText;
    private final String primaryCategory;

    TwigTokenId( String fixedText, String primaryCategory ) {
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
    
    private static final Language<TwigTokenId> language =
            new LanguageHierarchy<TwigTokenId>() {

                @Override
                protected Collection<TwigTokenId> createTokenIds() {
                    return EnumSet.allOf( TwigTokenId.class );
                }

                @Override
                protected Map<String, Collection<TwigTokenId>> createTokenCategories() {
                    Map<String, Collection<TwigTokenId>> cats = new HashMap<String, Collection<TwigTokenId>>();
                    return cats;
                }

                @Override
                protected Lexer<TwigTokenId> createLexer(LexerRestartInfo<TwigTokenId> info) {
                    return TwigLexer.create( info );
                }

                @Override
                protected String mimeType() {
                    return "text/twig-markup";
                }

                @Override
                protected LanguageEmbedding<?> embedding( Token<TwigTokenId> token,
                        LanguagePath languagePath, InputAttributes inputAttributes ) {
                    return null;
                }
            }.language();

    public static Language<TwigTokenId> language() {
        return language;
    }
}