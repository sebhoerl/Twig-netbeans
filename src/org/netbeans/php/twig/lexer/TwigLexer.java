package org.netbeans.php.twig.lexer;

import java.util.*;
import java.util.regex.*;

public class TwigLexer {

    public enum State {
        DATA,
        BLOCK,
        VAR,
        COMMENT
    }

    public ArrayList<TwigToken> tokens;

    static Pattern REGEX_NAME = Pattern.compile( "^[A-Za-z_][A-Za-z0-9_]*" );
    static Pattern REGEX_NUMBER = Pattern.compile( "^[0-9]+(\\.[0-9]+)?" );
    static Pattern REGEX_STRING = Pattern.compile( "^\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|^'([^'\\\\]*(\\\\.[^'\\\\]*)*)'", Pattern.MULTILINE );
    static Pattern REGEX_ALPHANUM_END = Pattern.compile( "[A-Za-z0-9]$" );
    Pattern REGEX_OPERATOR = null;

    static List<String> PUNCTUATION = Arrays.asList( "(", ")", "[", "]", "{", "}", "?", ":", ".", ",", "|" );

    List<String> OPERATORS = Arrays.asList(
        "=", "not", "+", "-", "or", "and", "==", "!=", ">", "<", ">=", "<=",
        "not in", "in", "+", "-", "~", "*", "/", "//", "%",
        "is", "is not", "..", "**"
    );

    static String BLOCK_START = "{%";
    static String BLOCK_END = "%}";

    static String COMMENT_START = "{#";
    static String COMMENT_END = "#}";

    static String VAR_START = "{{";
    static String VAR_END = "}}";

    State state;

    protected class SortOperators implements Comparator {

        @Override
        public int compare( Object a, Object b ) {

            int d = ((String)a).length() - ((String)b).length();
            return d;

        }

    }

    protected final String implode( List<String> list, String delimeter ) {

        String s = "";
        boolean first = true;

        for ( String item : list ) {

            if ( !first ) s += delimeter;
            s += item;
            first = false;
            
        }

        return s;
        
    }

    public TwigLexer() {

        Collections.sort( OPERATORS, new SortOperators() );
        Collections.reverse( OPERATORS );

        ArrayList<String> regex = new ArrayList();

        for ( String operator : OPERATORS ) {

            if ( REGEX_ALPHANUM_END.matcher( operator ).find() ) {
                regex.add( Pattern.quote( operator ) + "[ ()]" );
            } else {
                regex.add( Pattern.quote( operator ) );
            }

        }

        REGEX_OPERATOR = Pattern.compile( "^" + implode( regex, "|^" ) );

    }

    protected int cursor;
    protected int end;
    protected String code;

    public List<TwigToken> tokenize( String lexCode, State backupState ) {

        tokens = new ArrayList();
        state = ( backupState == null ) ? State.DATA : backupState;

        code = lexCode;
        code = code.replace( "\r\n", "\n" );
        code = code.replace( "\r", "\n" );

        cursor = 0;
        end = code.length();

        while ( cursor < end ) {

            switch ( state ) {
                case DATA:
                    lexData(); break;
                case BLOCK:
                    lexBlock(); break;
                case VAR:
                    lexVar(); break;
                case COMMENT:
                    lexComment(); break;
            }

        }

        pushToken( TwigToken.Type.EOF, "" );
        return tokens;

    }

    protected void lexData() {

        int tmpPos = 0;
        int pos = end;
        String token = "";

        //Find opening markups
        tmpPos = code.indexOf( COMMENT_START, cursor );
        if ( tmpPos > -1 && tmpPos < pos ) {
            pos = tmpPos;
            token = COMMENT_START;
        }

        tmpPos = code.indexOf( VAR_START, cursor );
        if ( tmpPos > -1 && tmpPos < pos ) {
            pos = tmpPos;
            token = VAR_START;
        }

        tmpPos = code.indexOf( BLOCK_START, cursor );
        if ( tmpPos > -1 && tmpPos < pos ) {
            pos = tmpPos;
            token = BLOCK_START;
        }

        // No more markups
        if ( pos == end ) {
            pushToken( TwigToken.Type.TEXT, code.substring( cursor ) );
            cursor = end;
            return;
        }

        // Push previous text
        String text = code.substring( cursor, pos );
        pushToken( TwigToken.Type.TEXT, text );
        moveCursor( text );

        // Add token (for syntax highlighting we cannot ignore some of them as in Twig lexer)
        if ( token.equals( COMMENT_START ) ) {

            pushToken( TwigToken.Type.COMMENT_START, token );
            moveCursor( token );
            state = State.COMMENT;

        } else if ( token.equals( BLOCK_START ) ) {

            pushToken( TwigToken.Type.BLOCK_START, token );
            moveCursor( token );
            state = State.BLOCK;

            // TODO: handle raw input

        } else if ( token.equals( VAR_START ) ) {

            pushToken( TwigToken.Type.VAR_START, token );
            moveCursor( token );
            state = State.VAR;

        }

    }

    protected void lexComment() {

        String text = "";
        int pos = code.indexOf( COMMENT_END, cursor );

        if ( pos < 0 ) {
            // TODO: Unclosed comment!
            // Doing nothing for now... let following data until end be comment
            text = code.substring( cursor );
            pushToken( TwigToken.Type.COMMENT, text );
            moveCursor( text );
        } else {
            text = code.substring( cursor, pos );
            pushToken( TwigToken.Type.COMMENT, text );
            moveCursor( text );
            pushToken( TwigToken.Type.COMMENT_END, COMMENT_END );
            moveCursor( COMMENT_END );
            state = State.DATA;
        }

    }

    protected void lexVar() {

        String text = "";
        int pos = code.indexOf( VAR_END, cursor );

        if ( pos < 0 ) {
            // TODO: Unclosed var!
            // Doing nothing for now... let following data until end be var
            text = code.substring( cursor );
            pushToken( TwigToken.Type._DEBUG_VAR, text );
            moveCursor( text );
        } else {
            text = code.substring( cursor, pos );
            pushToken( TwigToken.Type._DEBUG_VAR, text );
            moveCursor( text );
            pushToken( TwigToken.Type.VAR_END, VAR_END );
            moveCursor( VAR_END );
            state = State.DATA;
        }

    }

    protected void lexBlock() {

        String text = "";
        int pos = code.indexOf( BLOCK_END, cursor );

        if ( pos < 0 ) {
            // TODO: Unclosed block!
            // Doing nothing for now... let following data until end be var
            text = code.substring( cursor );
            pushToken( TwigToken.Type._DEBUG_BLOCK, text );
            moveCursor( text );
        } else {
            text = code.substring( cursor, pos );
            pushToken( TwigToken.Type._DEBUG_BLOCK, text );
            moveCursor( text );
            pushToken( TwigToken.Type.BLOCK_END, BLOCK_END );
            moveCursor( BLOCK_END );
            state = State.DATA;
        }

    }

    protected void moveCursor( String s ) {
        cursor += s.length();
    }

    protected void pushToken( TwigToken.Type type, String content ) {
        tokens.add( new TwigToken( type, content, cursor, state ) );
    }

    public State getState() {
        return state;
    }

    public boolean isOperator( String s ) {

        return REGEX_OPERATOR.matcher( s ).find();

    }

    public boolean isString( String s ) {

        return REGEX_STRING.matcher( s ).find();

    }

    public boolean isName( String s ) {

        return REGEX_NAME.matcher( s ).find();

    }

    public boolean isNumber( String s ) {

        return REGEX_NUMBER.matcher( s ).find();

    }

}
