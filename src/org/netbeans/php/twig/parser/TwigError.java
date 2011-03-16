package org.netbeans.php.twig.parser;

/**
 * Represents a Twig error
 *
 * @author Sebastian Hörl
 */
public class TwigError {

    public String message;
    public int offset;
    public int length;

    public TwigError( String message, int offset, int length ) {
        this.message = message;
        this.offset = offset;
        this.length = length;
    }

}
