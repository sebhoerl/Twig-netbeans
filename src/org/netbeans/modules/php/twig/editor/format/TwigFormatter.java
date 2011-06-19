package org.netbeans.modules.php.twig.editor.format;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.spi.Context;
import org.openide.util.Exceptions;

public class TwigFormatter implements Formatter {

    @Override
    public void reformat( Context context, ParserResult info ) {}

    @Override
    public void reindent( Context context ) {}

    @Override
    public boolean needsParserResult() { return false; }

    @Override
    public int indentSize() { return 0; }

    @Override
    public int hangingIndentSize() { return 0; }
    
}
