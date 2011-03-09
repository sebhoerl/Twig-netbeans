package org.netbeans.php.twig.lexer;

public class TwigToken {

    public enum Type {
        EOF,
        TEXT,
        BLOCK_START,
        RAW_START,
        COMMENT_START,
        VAR_START,
        BLOCK_END,
        RAW_END,
        COMMENT_END,
        VAR_END,
        NAME,
        NUMBER,
        STRING,
        OPERATOR,
        PUNCTUATION,
        COMMENT,
        _DEBUG_BLOCK,
        _DEBUG_VAR
    };

    static public String typeToString( Type type ) {
        switch ( type ) {
            case EOF: return "EOF";
            case TEXT: return "TEXT";
            case BLOCK_START: return "BLOCK_START";
            case RAW_START: return "RAW_START";
            case COMMENT_START: return "COMMENT_START";
            case VAR_START: return "VAR_START";
            case BLOCK_END: return "BLOCK_END";
            case RAW_END: return "RAW_END";
            case COMMENT_END: return "COMMENT_END";
            case VAR_END: return "VAR_END";
            case NAME: return "NAME";
            case NUMBER: return "NUMBER";
            case STRING: return "STRING";
            case OPERATOR: return "OPERATOR";
            case PUNCTUATION: return "PUNCTUATION";
            case COMMENT: return "COMMENT";
            case _DEBUG_BLOCK: return "_DEBUG_BLOCK";
            case _DEBUG_VAR: return "_DEBUG_VAR";
        }

        System.out.println( "Unknown Twig token!" );

        return "UNKNOWN";
    }

    static public int typeToOrdinal( Type type ) {
        switch ( type ) {
            case EOF: return 0;
            case TEXT: return 1;
            case BLOCK_START: return 2;
            case RAW_START: return 3;
            case COMMENT_START: return 4;
            case VAR_START: return 5;
            case BLOCK_END: return 6;
            case RAW_END: return 7;
            case COMMENT_END: return 8;
            case VAR_END: return 9;
            case NAME: return 10;
            case NUMBER: return 11;
            case STRING: return 12;
            case OPERATOR: return 13;
            case PUNCTUATION: return 14;
            case COMMENT: return 15;
            case _DEBUG_BLOCK: return 16;
            case _DEBUG_VAR: return 17;
        }

        System.out.println( "Unknown Twig token!" );

        return -1;
    }

    static public Type[] getTypes() {
        return Type.values();
    }

    public int offset;
    public String content;
    Type type;
    TwigLexer.State state;

    public TwigToken( Type type, String content, int offset, TwigLexer.State state ) {
        this.offset = offset;
        this.type = type;
        this.content = content;
        this.state = state;
    }

    @Override
    public String toString() {
        return typeToString( type ) + " @ " + Integer.toString( offset ) + "(" + content + ")";
    }

}