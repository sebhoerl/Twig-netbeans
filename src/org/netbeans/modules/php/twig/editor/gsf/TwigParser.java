/**
 * Twig for Netbeans
 * 
 * Copyright (c) 2011 Sebastian Hörl
 * 
 * For warranty and licesning information, view the LICENSE file.
 */

package org.netbeans.modules.php.twig.editor.gsf;

import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

public class TwigParser extends Parser {

    private Result fakeResult;

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
        fakeResult = new TwigFakeParserResult( snapshot );
    }

    @Override
    public Result getResult(Task task) throws ParseException { return fakeResult; }

    @Override
    public void cancel() {}

    @Override
    public void addChangeListener(ChangeListener changeListener) {}

    @Override
    public void removeChangeListener(ChangeListener changeListener) {}

    private static class TwigFakeParserResult extends ParserResult {

        public TwigFakeParserResult( Snapshot s ) { super(s); }

        @Override
        public List<? extends Error> getDiagnostics() { return Collections.emptyList(); }

        @Override
        protected void invalidate() { }

    }

}