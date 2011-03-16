package org.netbeans.php.twig.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.Exceptions;

/**
 * Task that highlights erors in Twig sourcode.
 *
 * @author Sebastian Hörl
 */
public class HighlightErrorsTask extends ParserResultTask {

    @Override
    public void run( Result r, SchedulerEvent event ) {
        NbTwigParser.Result result = (NbTwigParser.Result) r;
        List<TwigError> errors;
        Document document;
        List<ErrorDescription> descs = new ArrayList();

        try {
            errors = result.getErrors();
            document = result.getSnapshot().getSource().getDocument( false );
        } catch (ParseException ex) { return; }

        for ( TwigError error : errors ) {

            try {

                ErrorDescription desc = ErrorDescriptionFactory.createErrorDescription(
                        Severity.ERROR, error.message,
                        document,
                        document.createPosition(error.offset),
                        document.createPosition(error.offset + error.length)
                 );

                descs.add( desc );

            } catch ( BadLocationException ex ) {

                Exceptions.printStackTrace( ex );
                
            }

        }

        HintsController.setErrors( document, "twig", descs );
        
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {}

    static public class Factory extends TaskFactory {

        @Override
        public Collection<? extends SchedulerTask> create( Snapshot snapshot ) {
            return Collections.singletonList( new HighlightErrorsTask() );
        }

    }

}
