/**
 * Twig plugin for Netbeans.
 *
 * Copyright (c) 2011 Sebastian Hörl
 *
 * For warranty and licensing information, view the LICENSE file.
 */

package org.netbeans.php.twig.parser;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Some testcases.
 *
 * @author Sebastian Hörl
 */
public class TwigParserTest {

    /**
     * Test of needsEndTag
     */
    @Test
    public void testNeedsEndTag() {

        TwigParser lexer = new TwigParser();

        assertTrue( lexer.needsEndTag( "if" ) );
        assertTrue( lexer.needsEndTag( "for" ) );
        assertFalse( lexer.needsEndTag( "import" ) );

    }

    /**
     * Test of isEndTag
     */
    @Test
    public void testIsEndTag() {

        TwigParser lexer = new TwigParser();

        assertTrue( lexer.isEndTag( "endif" ) );
        assertTrue( lexer.isEndTag( "endfor" ) );
        assertFalse( lexer.isEndTag( "endnothing" ) );
        assertFalse( lexer.isEndTag( "import" ) );

    }


}