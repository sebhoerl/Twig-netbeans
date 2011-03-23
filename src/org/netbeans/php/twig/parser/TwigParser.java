/**
 * Twig plugin for Netbeans.
 *
 * Copyright (c) 2011 Sebastian Hörl
 *
 * For warranty and licensing information, view the LICENSE file.
 */

package org.netbeans.php.twig.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.regex.Pattern;
import org.netbeans.php.twig.lexer.TwigState;
import org.netbeans.php.twig.lexer.TwigToken;

/**
 * Parser for the Twig language.
 *
 * @author Sebastian Hörl
 */
public class TwigParser {

    ListIterator<TwigToken> stream;
    TwigToken current;

    enum State {
        DATA, COMMENT, BLOCK, VAR
    }

    State state = State.DATA;

    Stack<TwigToken> blocks = new Stack<TwigToken>();
    List<TwigError> errors = new ArrayList<TwigError>();

    public List<TwigError> parse( List<TwigToken> tokens ) {

        stream = tokens.listIterator();
        current = new TwigToken( TwigToken.Type.EOF, "", 0, new TwigState() );
        state = State.DATA;
        errors = new ArrayList<TwigError>();

        while ( nextToken() ) {
            
            switch ( currentToken().type ) {
                case BLOCK_START:
                    parseBlock(); break;
                case VAR_START:
                    parseVar(); break;
                case COMMENT_START:
                    parseComment(); break;
                case TEXT:
                case WHITESPACE:
                    continue;
                case EOF:
                    break;
                default:
                    addError( 
                        "Unexpected token " + currentToken().type + " '" + currentToken().content + "'",
                        currentToken().offset,
                        currentToken().content.length()
                    );
            }
            
        }

        while ( !blocks.empty() ) {

            TwigToken token = blocks.pop();
            addError( "Unclosed '" + token.content + "'", token.offset, token.content.length() );

        }

        return errors;

    }
    
    public boolean needsEndTag( String tag ) {
        if ( tag.equals("if") ) return true;
        if ( tag.equals("for") ) return true;
        if ( tag.equals("spaceless") ) return true;
        if ( tag.equals("autoescape") ) return true;
        if ( tag.equals("raw") ) return true;
        if ( tag.equals("macro") ) return true;
        if ( tag.equals("filter") ) return true;
        if ( tag.equals("set") ) return true;
        if ( tag.equals("block" ) ) return true;
        return false;
    }

    static Pattern REGEX_IS_END = Pattern.compile( "^end" );

    public String buildBlockTag( String name ) {

        return "block:" + name;

    }

    public boolean isEndTag( String tag ) {
        if ( REGEX_IS_END.matcher( tag ).find() ) {
            return needsEndTag( tag.substring( 3 ) );
        }
        return false;
    }

    protected void parseBlock() {

        TwigToken open = currentToken();
        TwigToken tag = null;
        TwigToken name = null;
        int nonWhitespaces = 0;

        boolean empty = true;
        boolean foundOperator = false;


        while ( nextToken() ) {

            if ( currentToken().type == TwigToken.Type.BLOCK_END ) {

                if ( empty ) {

                    addError( "Empty block", open.offset, ( currentToken().offset + currentToken().content.length() ) - open.offset );

                }

                if ( tag != null ) {

                    if ( tag.content.equals("set") && foundOperator ) {
                        // ignore one-line "set" blocks
                    } else if ( tag.content.equals("block") && nonWhitespaces > 2 ) {
                        // ignore one-line "block" blocks
                    } else if (needsEndTag(tag.content)) {

                        if ( tag.content.equals("block" ) ) {

                            if ( name == null ) {
                                addError( "Unnamed block", tag.offset, tag.content.length() );
                            }

                        }
                        
                        blocks.push( tag );

                    }

                    if ( isEndTag( tag.content ) || tag.content.equals( "endblock" ) ) {

                        if ( blocks.empty() ) {

                            addError( "Unopened '" + tag.content.substring( 3 ) + "'", tag.offset, tag.content.length() );

                        } else {

                            TwigToken expected = blocks.pop();
                            if ( !tag.content.equals( "end" + expected.content ) ) {

                                addError( "Unclosed '" + expected.content + "'", expected.offset, expected.content.length() );
                                addError( "Expected 'end" + expected.content + "'", tag.offset, tag.content.length() );
                            
                            }

                        }

                    }

                }

                return;

            }

            if ( currentToken().type != TwigToken.Type.WHITESPACE ) {
                empty = false;
                nonWhitespaces++;
            }

            if ( currentToken().type == TwigToken.Type.OPERATOR && currentToken().content.equals( "=" ) ) {
                foundOperator = true;
            }

            if ( tag != null && currentToken().type == TwigToken.Type.NAME ) {
                name = currentToken();
            }

            if ( currentToken().type == TwigToken.Type.TAG ) {
                tag = currentToken();
            }

            if ( currentToken().type == TwigToken.Type.ERROR ) {
                addError( "Unexpected '" + currentToken().content + "'", currentToken().offset, currentToken().content.length() );
                continue;
            }

            if ( currentToken().type == TwigToken.Type.EOF ) break;
            
        }

        addError( "Unclosed block", open.offset, open.content.length() );
        prevToken();

    }

    protected void parseVar() {

        TwigToken open = currentToken();
        boolean empty = true;

        while ( nextToken() ) {

            if ( currentToken().type == TwigToken.Type.VAR_END ) {

                if ( empty ) {

                    addError( "Empty variable", open.offset, ( currentToken().offset + currentToken().content.length() ) - open.offset );

                }

                return;
            }

            if ( currentToken().type == TwigToken.Type.ERROR ) {

                addError( "Unexpected '" + currentToken().content + "'", currentToken().offset, currentToken().content.length() );
                continue;

            }

            if ( currentToken().type == TwigToken.Type.EOF ) break;

            if ( currentToken().type != TwigToken.Type.WHITESPACE ) {
                empty = false;
            }

        }

        addError( "Unclosed variable", open.offset, open.content.length() );
        prevToken();

    }

    protected void parseComment() {

        TwigToken start = currentToken();

        while ( nextToken() ) {

            if ( currentToken().type == TwigToken.Type.COMMENT_END ) return;
            if ( currentToken().type != TwigToken.Type.COMMENT ) break;

        }
        
        addError( "Unclosed comment", start.offset, start.content.length() );
        prevToken();

    }

    protected boolean nextToken() {
        if ( stream.hasNext() ) {
            current = stream.next();
            return true;
        }
        return false;
    }

    protected boolean prevToken() {
        if ( stream.hasPrevious() ) {
            current = stream.previous();
            return true;
        }
        return false;
    }

    protected boolean hasNextToken() {
        return stream.hasNext();
    }

    protected boolean hasPrevToken() {
        return stream.hasPrevious();
    }

    protected TwigToken currentToken() {
        return current;
    }

    protected void addError( String message, int offset, int length ) {
        errors.add( new TwigError(
            message, offset, length
        ) );
    }

}
