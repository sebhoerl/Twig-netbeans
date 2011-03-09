/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.php.twig.lexer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 *
 * @author sebastian
 */
public class TwigLexerTest {

    public TwigLexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isOperator method, of class TwigLexer.
     */
    @Test
    public void testIsOperator() {

        TwigLexer lexer = new TwigLexer();

        assertTrue( lexer.isOperator( "+" ) );
        assertTrue( lexer.isOperator( "%" ) );
        assertTrue( lexer.isOperator( "-" ) );

        assertFalse( lexer.isOperator( "abc is not" ) );
        assertFalse( lexer.isOperator( "test not in" ) );

        assertTrue( lexer.isOperator( "is not " ) );
        assertTrue( lexer.isOperator( "not in(" ) );
        assertTrue( lexer.isOperator( "is)" ) );

    }

    @Test
    public void testIsNumber() {

        TwigLexer lexer = new TwigLexer();

        assertTrue( lexer.isNumber( "44" ) );
        assertTrue( lexer.isNumber( "34.293" ) );
        assertFalse( lexer.isNumber( "asd34" ) );

    }

    @Test
    public void testIsName() {

        TwigLexer lexer = new TwigLexer();

        assertTrue( lexer.isName( "aBcD" ) );
        assertTrue( lexer.isName( "a_34vd" ) );
        assertFalse( lexer.isName( "3asd" ) );

    }

    @Test
    public void testIsString() {

        TwigLexer lexer = new TwigLexer();

        assertTrue( lexer.isString( "\"test\"" ) );
        assertTrue( lexer.isString( "\'test\'" ) );
        assertTrue( lexer.isString( "\'te\nst\'" ) );

        assertTrue( lexer.isString( "\'this is a \\\"big\\\" test\'" ) );

    }

    @Test
    public void testLexer() {

        TwigLexer lexer = new TwigLexer();
        List<TwigToken> list = lexer.tokenize(
            "<html>{# commenthaha #}"
            + "<ul>"
            + "{% for user in users %}"
            + "<li>{{ user.name }}</li>"
            + "{% endfor %}"
            + "</ul>"
            + "</html>", null
        );

        Iterator<TwigToken> it = list.iterator();

        assertEquals( it.next().type, TwigToken.Type.TEXT );
        assertEquals( it.next().type, TwigToken.Type.COMMENT_START );
        assertEquals( it.next().type, TwigToken.Type.COMMENT );
        assertEquals( it.next().type, TwigToken.Type.COMMENT_END );
        assertEquals( it.next().type, TwigToken.Type.TEXT );
        assertEquals( it.next().type, TwigToken.Type.BLOCK_START );
        assertEquals( it.next().type, TwigToken.Type._DEBUG_BLOCK );
        assertEquals( it.next().type, TwigToken.Type.BLOCK_END );
        assertEquals( it.next().type, TwigToken.Type.TEXT );
        assertEquals( it.next().type, TwigToken.Type.VAR_START );
        assertEquals( it.next().type, TwigToken.Type._DEBUG_VAR );
        assertEquals( it.next().type, TwigToken.Type.VAR_END );
        assertEquals( it.next().type, TwigToken.Type.TEXT );
        assertEquals( it.next().type, TwigToken.Type.BLOCK_START );
        assertEquals( it.next().type, TwigToken.Type._DEBUG_BLOCK );
        assertEquals( it.next().type, TwigToken.Type.BLOCK_END );
        assertEquals( it.next().type, TwigToken.Type.TEXT );
        assertEquals( it.next().type, TwigToken.Type.EOF );

        assertFalse( it.hasNext() );

    }

}