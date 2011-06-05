/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.lexer;

public class TwigTopLexerState {
    
    public enum Main {
        INIT,
        HTML,
        
        OPEN,
        TWIG,
        CLOSE,
        
        CLOSE_RAW,
        RAW
    };
    
    public enum Type {
        NONE, INSTRUCTION, VARIABLE, COMMENT
    };
    
    Main main;
    Type type;
    
    public TwigTopLexerState() {
        main = Main.INIT;
        type = type.NONE;
    }
    
    public TwigTopLexerState( TwigTopLexerState copy ) {
        main = copy.main;
        type = copy.type;
    }
    
    public TwigTopLexerState( Main main, Type type ) {
        this.main = main;
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.main != null ? this.main.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals( Object object ) {
        if ( object == null ) return false;
        if ( getClass() != object.getClass() ) return false;
        TwigTopLexerState compare = (TwigTopLexerState) object;
        if ( main != compare.main ) return false;
        if ( type != compare.type ) return false;
        return true;
    }
    
}
