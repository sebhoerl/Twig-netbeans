package org.netbeans.php.twig.lexer;

import java.util.*;
import java.util.regex.*;

/**
 * Lexer for the Twig language.
 * Based on https://github.com/fabpot/Twig/blob/master/lib/Twig/Lexer.php
 *
 * All code splitting stuff into tokens is done here. The NetBeans
 * interfaces are just some kind of decorators in this plugin.
 *
 * @author Sebastian Hörl
 */
public class TwigLexer {

    public ArrayList<TwigToken> tokens;

    static Pattern REGEX_NAME = Pattern.compile( "^[A-Za-z_][A-Za-z0-9_]*" );
    static Pattern REGEX_NUMBER = Pattern.compile( "^[0-9]+(\\.[0-9]+)?" );
    static Pattern REGEX_STRING = Pattern.compile( "^\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|^'([^'\\\\]*(\\\\.[^'\\\\]*)*)'", Pattern.MULTILINE );
    static Pattern REGEX_ALPHANUM_END = Pattern.compile( "[A-Za-z0-9]$" );
    static Pattern REGEX_WHITESPACE = Pattern.compile( "^[\\s]" );
    static Pattern REGEX_WHITESPACE_END = Pattern.compile( "[\\s]+$" );
    Pattern REGEX_OPERATOR = null;

    static Pattern REGEX_ENDRAW = Pattern.compile( "\\{%([\\s]*)endraw([\\s])*%\\}" );

    static String PUNCTUATION = "()[]{}?:.,|";

    List<String> OPERATORS = Arrays.asList(
        "import", "from", "as",
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

    TwigState state;

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

    public List<TwigToken> tokenize( String lexCode, TwigState backupState ) {

        tokens = new ArrayList();
        state = ( backupState == null ) ? new TwigState() : backupState;

        code = lexCode;
        code = code.replace( "\r\n", "\n" );
        code = code.replace( "\r", "\n" );

        cursor = 0;
        end = code.length();

        while ( cursor < end ) {

            switch ( state.mode ) {
                case RAW:
                    lexRawData(); break;
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

    protected void lexRawData() {

        Matcher matcher = REGEX_ENDRAW.matcher( code );
        if ( matcher.find( cursor )) {

            String content = code.substring( cursor, matcher.start() );
            pushToken( TwigToken.Type.TEXT, content );
            moveCursor( content );
            state.mode = TwigState.Mode.DATA;

        } else {

            String rest = code.substring( cursor );
            pushToken( TwigToken.Type.TEXT,rest );
            moveCursor( rest );

        }

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
            state.seekTag = true;
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
            state.mode = TwigState.Mode.COMMENT;

        } else if ( token.equals( BLOCK_START ) ) {

            pushToken( TwigToken.Type.BLOCK_START, token );
            moveCursor( token );
            state.mode = TwigState.Mode.BLOCK;

        } else if ( token.equals( VAR_START ) ) {

            pushToken( TwigToken.Type.VAR_START, token );
            moveCursor( token );
            state.mode = TwigState.Mode.VAR;

        }

    }

    protected void lexComment() {

        String text = "";
        int pos = code.indexOf( COMMENT_END, cursor );

        if ( pos < 0 ) {
            // Unclosed comment. Just add the rest of the code as commented text.
            // Parser will recognize it as an error.
            text = code.substring( cursor );
            pushToken( TwigToken.Type.COMMENT, text );
            moveCursor( text );
        } else {
            text = code.substring( cursor, pos );
            pushToken( TwigToken.Type.COMMENT, text );
            moveCursor( text );
            pushToken( TwigToken.Type.COMMENT_END, COMMENT_END );
            moveCursor( COMMENT_END );
            state.mode = TwigState.Mode.DATA;
        }

    }

    protected void lexVar() {

        int pos = code.indexOf( VAR_END, cursor );

        if ( pos == cursor && state.brackets.empty() ) {
            pushToken( TwigToken.Type.VAR_END, VAR_END );
            moveCursor( VAR_END );
            state.mode = TwigState.Mode.DATA;
        } else {
            lexExpression();
        }

    }

    protected void lexBlock() {

        int pos = code.indexOf( BLOCK_END, cursor );

        if ( pos == cursor && state.brackets.empty() ) {
            pushToken( TwigToken.Type.BLOCK_END, BLOCK_END );
            moveCursor( BLOCK_END );
            state.mode = state.captureRawData ? TwigState.Mode.RAW : TwigState.Mode.DATA;
            state.captureRawData = false;
        } else {
            lexExpression();
        }

    }

    protected void lexExpression() {

        Matcher matcher;
        String chunk = code.substring( cursor );

        matcher = REGEX_WHITESPACE.matcher( chunk );
        if ( matcher.find() ) {

            pushToken( TwigToken.Type.WHITESPACE, matcher.group() );
            moveCursor( matcher.group() );
            return;

        }

        matcher = REGEX_OPERATOR.matcher( chunk );
        if ( !state.seekTag && matcher.find() ) {

            String operator = matcher.group();
            matcher = REGEX_WHITESPACE_END.matcher( operator );

            if ( matcher.find() ) {

                operator = operator.substring( 0, matcher.start() );
                pushToken( TwigToken.Type.OPERATOR, operator );
                moveCursor( operator );

                pushToken( TwigToken.Type.WHITESPACE, matcher.group() );
                moveCursor( matcher.group() );

            } else {

                pushToken( TwigToken.Type.OPERATOR, operator );
                moveCursor( operator );

            }

            return;

        }

        matcher = REGEX_NAME.matcher( chunk );
        if ( matcher.find() ) {

            if ( state.seekTag && matcher.group().equals( "raw" ) ) {
                state.captureRawData = true;
            }

            pushToken( state.seekTag ? TwigToken.Type.TAG : TwigToken.Type.NAME, matcher.group() );
            moveCursor( matcher.group() );
            state.seekTag = false;
            return;

        }

        matcher = REGEX_NUMBER.matcher( chunk );
        if ( matcher.find() ) {

            pushToken( TwigToken.Type.NUMBER, matcher.group() );
            moveCursor( matcher.group() );
            return;

        }

        String currentChar = chunk.substring( 0, 1 );
        if ( PUNCTUATION.indexOf( currentChar ) > -1 ) {

            if ( "([{".indexOf( currentChar ) > - 1 ) { // opening brackets

                state.brackets.push( currentChar );
                pushToken( TwigToken.Type.PUNCTUATION, currentChar );
                moveCursor( currentChar );
                return;

            } else if ( ")]}".indexOf( currentChar ) > - 1 ) { // closing brackets

                if ( state.brackets.empty() ) {

                    // too many closing brackets
                    pushToken( TwigToken.Type.ERROR, currentChar );
                    moveCursor( String.valueOf( currentChar ) );
                    return;

                } else {
                    
                    String bracket = state.brackets.pop()
                        .replace( "(", ")" )
                        .replace( "{", "}" )
                        .replace( "[", "]" );

                    if ( !bracket.equals( currentChar ) ) {

                        // wrong bracket.. let parser do the rest
                        pushToken( TwigToken.Type.ERROR, currentChar );
                        moveCursor( String.valueOf( currentChar ) );
                        return;

                    } else {

                        pushToken( TwigToken.Type.PUNCTUATION, currentChar );
                        moveCursor( currentChar );
                        return;

                    }
                    
                }

            } else { // no bracket

                pushToken( TwigToken.Type.PUNCTUATION, currentChar );
                moveCursor( currentChar );
                return;

            }

        }

        matcher = REGEX_STRING.matcher( chunk );
        if ( matcher.find() ) {

            pushToken( TwigToken.Type.STRING, matcher.group() );
            moveCursor( matcher.group() );
            return;

        }

        // Now we got a problem... delegate it to the parser
        pushToken( TwigToken.Type.ERROR, currentChar );
        moveCursor( currentChar );
        return;

    }

    protected void moveCursor( String s ) {
        cursor += s.length();
    }

    protected void pushToken( TwigToken.Type type, String content ) {
        if ( content.length() == 0 && type != TwigToken.Type.EOF ) return;
        tokens.add( new TwigToken( type, content, cursor, state ) );
    }

    public TwigState getState() {
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
