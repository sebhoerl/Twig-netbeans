/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.lexer;

public class TwigLexerState {
    
    public enum Main {
        INIT, 
        COMMENT,
        VARIABLE, 
        INSTRUCTION
    };
    
    public enum Sub { NONE, INIT };
    
    Main main;
    Sub sub;
    
    public TwigLexerState() {
        main = Main.INIT;
        sub = Sub.NONE;
    }
    
    public TwigLexerState( TwigLexerState copy ) {
        main = copy.main;
        sub = copy.sub;
    }
    
    public TwigLexerState( Main main, Sub sub ) {
        this.main = main;
        this.sub = sub;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object == null ) return false;
        if ( getClass() != object.getClass() ) return false;
        TwigLexerState compare = (TwigLexerState) object;
        if ( main != compare.main ) return false;
        if ( sub != compare.sub ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.main != null ? this.main.hashCode() : 0);
        hash = 97 * hash + (this.sub != null ? this.sub.hashCode() : 0);
        return hash;
    }
    
}